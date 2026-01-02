package com.example.modules.auth.services;

import static com.example.base.enums.ErrorCode.EMAIL_USED;
import static com.example.base.enums.ErrorCode.INVALID_CREDENTIALS;
import static com.example.base.enums.ErrorCode.PASSWORD_NOT_MATCH;
import static com.example.base.enums.ErrorCode.TOKEN_REQUIRED;
import static com.example.base.enums.ErrorCode.TOKEN_REVOKED;
import static com.example.base.enums.ErrorCode.USER_NOT_FOUND;

import com.example.base.exceptions.AppException;
import com.example.modules.auth.dtos.AuthTokenDTO;
import com.example.modules.auth.dtos.ChangePasswordRequestDTO;
import com.example.modules.auth.dtos.LoginRequestDTO;
import com.example.modules.auth.dtos.RegisterRequestDTO;
import com.example.modules.auth.dtos.ResetPasswordRequestDTO;
import com.example.modules.auth.entities.Account;
import com.example.modules.auth.repositories.AccountsRepository;
import com.example.modules.email.dtos.SendEmailEventDTO;
import com.example.modules.redis.publishers.RedisStreamPublisher;
import com.example.modules.redis.services.RedisService;
import com.example.modules.users.entities.User;
import com.example.modules.users.repositories.UsersRepository;
import com.example.modules.users.utils.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AccountsRepository accountsRepository;
  private final UsersRepository usersRepository;
  private final JwtService jwtService;
  private final RedisService redisService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final RedisStreamPublisher redisStreamPublisher;

  @Value("${app.frontend-url}")
  private String frontendUrl;

  @Value("${jwt.reset-password.expiration}")
  private Long RESET_PASSWORD_EXPIRATION;

  public AuthTokenDTO login(LoginRequestDTO loginRequest) {
    String email = loginRequest.getEmail();
    String password = loginRequest.getPassword();

    Account account = accountsRepository
      .findByEmail(email)
      .orElseThrow(() -> new AppException(INVALID_CREDENTIALS));

    if (!passwordEncoder.matches(password, account.getPassword())) {
      throw new AppException(INVALID_CREDENTIALS);
    }

    User user = usersRepository.findByAccount(account).get();
    return getTokenResponse(user);
  }

  public AuthTokenDTO register(RegisterRequestDTO registerRequest) {
    final String email = registerRequest.getEmail();
    final String password = registerRequest.getPassword();

    final Optional<Account> existingAccount = accountsRepository.findByEmail(email);
    final User savedUser;

    if (!existingAccount.isPresent()) {
      final Account savedAccount = accountsRepository.save(
        Account.builder().email(email).password(passwordEncoder.encode(password)).build()
      );

      savedUser = usersRepository.save(User.builder().account(savedAccount).build());
    } else if (existingAccount.get().isEnabled()) {
      throw new AppException(EMAIL_USED);
    } else {
      final Account account = existingAccount.get();
      account.setEmail(email);
      account.setPassword(passwordEncoder.encode(password));
      account.setDeletedTimestamp(null);
      final Account savedAccount = accountsRepository.save(account);
      final User user = usersRepository
        .findByAccount(account)
        .orElse(User.builder().account(savedAccount).build());
      savedUser = usersRepository.save(user);
    }

    return getTokenResponse(savedUser);
  }

  public AuthTokenDTO refresh(String refreshToken) {
    if (refreshToken == null || refreshToken.isEmpty()) {
      throw new AppException(TOKEN_REQUIRED);
    }

    final Jws<Claims> decodedRefreshToken = jwtService.verifyRefreshToken(refreshToken);
    final String userId = decodedRefreshToken.getPayload().getSubject();
    final Date tokenIssuedAt = decodedRefreshToken.getPayload().getIssuedAt();

    if (jwtService.isTokenInvalidated(userId, tokenIssuedAt)) {
      throw new AppException(TOKEN_REVOKED);
    }

    final User user = usersRepository
      .findById(userId)
      .orElseThrow(() -> new AppException(USER_NOT_FOUND));

    invalidateTokens(userId);
    return getTokenResponse(user);
  }

  public void logout(User user) {
    invalidateTokens(user.getId());
  }

  public void changePassword(User user, ChangePasswordRequestDTO request) {
    String currentPassword = user.getAccount().getPassword();

    if (currentPassword != null && request.getPassword() == null) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Field `password` is required for this account."
      );
    }

    if (
      currentPassword != null && !passwordEncoder.matches(request.getPassword(), currentPassword)
    ) {
      throw new AppException(PASSWORD_NOT_MATCH);
    }

    Account userAccount = user.getAccount();
    userAccount.setPassword(passwordEncoder.encode(request.getNewPassword()));
    accountsRepository.save(userAccount);

    invalidateTokens(user.getId());
  }

  public void forgotPassword(String email) {
    User user = usersRepository
      .findByAccountEmail(email)
      .orElseThrow(() -> new AppException(USER_NOT_FOUND));

    String resetToken = jwtService.generateResetPasswordToken(user);

    String resetLink = UriComponentsBuilder.fromUriString(frontendUrl)
      .path("/reset-password")
      .queryParam("token", resetToken)
      .build()
      .toUriString();

    SendEmailEventDTO event = SendEmailEventDTO.builder()
      .to(user.getAccount().getEmail())
      .subject("Reset your password")
      .templateName("forgot-password")
      .variables(
        Map.of(
          "username",
          "%s %s".formatted(user.getFirstName(), user.getLastName()),
          "resetLink",
          resetLink,
          "expirationTime",
          TimeUnit.SECONDS.toMinutes(RESET_PASSWORD_EXPIRATION)
        )
      )
      .build();

    redisStreamPublisher.send("stream:email_sending", event);
  }

  public void resetPassword(ResetPasswordRequestDTO request) {
    String token = request.getToken();
    String newPassword = request.getPassword();
    String userId = jwtService.extractUserIdUnverified(token);
    User user = usersRepository
      .findById(userId)
      .orElseThrow(() -> new AppException(USER_NOT_FOUND));

    jwtService.verifyResetPasswordToken(token, user);

    user.getAccount().setPassword(passwordEncoder.encode(newPassword));
    usersRepository.save(user);
  }

  private AuthTokenDTO getTokenResponse(User user) {
    final String accessToken = jwtService.generateAccessToken(user);
    final String refreshToken = jwtService.generateRefreshToken(user);

    return AuthTokenDTO.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .user(userMapper.toUserProfileDTO(user))
      .build();
  }

  private void invalidateTokens(String userId) {
    redisService.set(
      "user:%s:tokens:invalidated_before".formatted(userId),
      Instant.now().truncatedTo(ChronoUnit.SECONDS)
    );
  }
}
