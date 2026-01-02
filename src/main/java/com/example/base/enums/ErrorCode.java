package com.example.base.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ErrorCode {
  UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR", "error.system.unknown"),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "error.request.invalid_format"),
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "error.validation.failed"),
  OPERATION_NOT_ALLOWED(
    HttpStatus.FORBIDDEN,
    "OPERATION_NOT_ALLOWED",
    "error.operation.not_allowed"
  ),

  TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "TOKEN_REQUIRED", "auth.token.required"),
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "auth.token.expired"),
  TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "auth.token.invalid"),

  TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "TOKEN_REVOKED", "auth.token.revoked"),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "auth.invalid_credentials"),
  PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASSWORD_NOT_MATCH", "user.password.not_match"),
  USER_EXISTED(HttpStatus.BAD_REQUEST, "USER_EXISTED", "user.register.exists"),
  EMAIL_USED(HttpStatus.BAD_REQUEST, "EMAIL_USED", "user.email.duplicate"),

  ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "user.account.not_found"),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "user.not_found"),
  POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "content.post.not_found");

  HttpStatus status;
  String code;
  String messageKey;
}
