package com.example.modules.auth.services;

import static com.example.base.enums.ErrorCode.EMAIL_USED;
import static com.example.base.enums.ErrorCode.INVALID_CREDENTIALS;
import static com.example.base.enums.ErrorCode.PASSWORD_NOT_MATCH;
import static com.example.base.enums.ErrorCode.TOKEN_INVALIDATED;
import static com.example.base.enums.ErrorCode.USER_NOT_FOUND;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.base.BaseServiceIntegrationTest;
import com.example.base.exceptions.AppException;
import com.example.modules.auth.dtos.AuthTokenDTO;
import com.example.modules.auth.dtos.ChangePasswordRequestDTO;
import com.example.modules.auth.dtos.LoginRequestDTO;
import com.example.modules.auth.dtos.RegisterRequestDTO;
import com.example.modules.auth.entities.Account;
import com.example.modules.users.entities.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthServiceIntegrationTest extends BaseServiceIntegrationTest {

  @Autowired
  private AuthService authService;

  @Autowired
  private JwtService jwtService;

  @Test
  void login_WhenValidCredentials_ShouldReturnAuthTokenDTO() {
    LoginRequestDTO loginRequest = LoginRequestDTO.builder()
      .email("email@example.com")
      .password("password@123456")
      .build();

    AuthTokenDTO tokenDTO = authService.login(loginRequest);

    assertNotNull(tokenDTO.getAccessToken());
    assertNotNull(tokenDTO.getRefreshToken());
    assertEquals("email@example.com", tokenDTO.getUser().getEmail());
  }

  @Test
  void login_WhenInvalidEmail_ShouldThrowInvalidCredentialsException() {
    LoginRequestDTO loginRequest = LoginRequestDTO.builder()
      .email("invalid@example.com")
      .password("password@123456")
      .build();

    AppException ex = assertThrows(AppException.class, () -> authService.login(loginRequest));
    assertEquals(INVALID_CREDENTIALS, ex.getErrorCode());
  }

  @Test
  void login_WhenInvalidPassword_ShouldThrowInvalidCredentialsException() {
    LoginRequestDTO loginRequest = LoginRequestDTO.builder()
      .email("email@example.com")
      .password("invalidpassword")
      .build();

    AppException ex = assertThrows(AppException.class, () -> authService.login(loginRequest));
    assertEquals(INVALID_CREDENTIALS, ex.getErrorCode());
  }

  @Test
  void register_WhenValidRequest_ShouldReturnAuthTokenDTO() {
    RegisterRequestDTO registerRequest = RegisterRequestDTO.builder()
      .email("newemail@example.com")
      .password("newpassword")
      .build();

    AuthTokenDTO tokenDTO = authService.register(registerRequest);

    assertNotNull(tokenDTO.getAccessToken());
    assertNotNull(tokenDTO.getRefreshToken());
    assertEquals("newemail@example.com", tokenDTO.getUser().getEmail());
  }

  @Test
  void register_WhenEmailAlreadyUsedAndExistingAccountIsEnabled_ShouldThrowEmailHasAlreadyBeenUsedException() {
    RegisterRequestDTO registerRequest = RegisterRequestDTO.builder()
      .email("email@example.com")
      .password("newpassword")
      .build();

    AppException ex = assertThrows(AppException.class, () -> authService.register(registerRequest));

    assertEquals(EMAIL_USED, ex.getErrorCode());
  }

  @Test
  void register_WhenEmailAlreadyUsedAndExistingAccountNotEnabled_ShouldCreateUserAndReturnAuthTokenDTO() {
    Account account = getAccount();
    account.setDeletedTimestamp(Instant.now());
    accountsRepository.save(account);

    RegisterRequestDTO registerRequest = RegisterRequestDTO.builder()
      .email("email@example.com")
      .password("newpassword")
      .build();

    AuthTokenDTO tokenDTO = authService.register(registerRequest);

    assertNotNull(tokenDTO.getAccessToken());
    assertNotNull(tokenDTO.getRefreshToken());
    assertEquals("email@example.com", tokenDTO.getUser().getEmail());
  }

  @Test
  void refresh_WhenTokenIsValidAndUserExists_ShouldReturnAuthTokenDTO() {
    User user = getUser();
    String refreshToken = jwtService.generateRefreshToken(user);

    AuthTokenDTO tokenDTO = authService.refresh(refreshToken);

    assertNotNull(tokenDTO.getAccessToken());
    assertNotNull(tokenDTO.getRefreshToken());
    assertEquals(user.getAccount().getEmail(), tokenDTO.getUser().getEmail());
  }

  @Test
  void refresh_WhenTokenIsInvalidated_ShouldThrowTokenInvalidatedException() throws Exception {
    User user = getUser();
    String refreshToken = jwtService.generateRefreshToken(user);

    Thread.sleep(2000);
    authService.logout(user);

    AppException ex = assertThrows(AppException.class, () -> authService.refresh(refreshToken));
    assertEquals(TOKEN_INVALIDATED, ex.getErrorCode());
  }

  @Test
  void refresh_WhenUserNotFound_ShouldThrowUserNotFoundException() {
    User user = getUser();
    String refreshToken = jwtService.generateRefreshToken(user);

    usersRepository.deleteAll();

    AppException ex = assertThrows(AppException.class, () -> authService.refresh(refreshToken));
    assertEquals(USER_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void changePassword_WhenCurrentPasswordIsNull_ShouldUpdatePasswordAndInvalidateTokens() {
    User user = getUser();
    Account account = user.getAccount();
    account.setPassword(null);
    accountsRepository.save(account);

    ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
      .password(null)
      .newPassword("newPassword")
      .build();

    authService.changePassword(user, request);
    Account savedAccount = getAccount();

    assertTrue(passwordEncoder.matches(request.getNewPassword(), savedAccount.getPassword()));
  }

  @Test
  void changePassword_WhenRequestPasswordIsNullAndCurrentPasswordIsNotNull_ShouldThrowResponseStatusException() {
    User user = getUser();

    ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
      .password(null)
      .newPassword("newPassword")
      .build();

    ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
      authService.changePassword(user, request)
    );

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
  }

  @Test
  void changePassword_WhenPasswordDoesNotMatchCurrent_ShouldThrowPasswordNotMatchException() {
    User user = getUser();

    ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
      .password("wrongPassword")
      .newPassword("newPassword")
      .build();

    AppException ex = assertThrows(AppException.class, () ->
      authService.changePassword(user, request)
    );
    assertEquals(PASSWORD_NOT_MATCH, ex.getErrorCode());
  }

  @Test
  void changePassword_WhenPasswordMatchesCurrent_ShouldUpdatePasswordAndInvalidateTokens() {
    User user = getUser();

    ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
      .password("password@123456")
      .newPassword("newPassword")
      .build();

    authService.changePassword(user, request);
    Account savedAccount = getAccount();

    assertTrue(passwordEncoder.matches(request.getNewPassword(), savedAccount.getPassword()));
  }
}
