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
  // 1. Common errors
  UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR", "An unexpected error occurred"),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Invalid request format"),
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Input validation failed"),
  OPERATION_NOT_ALLOWED(
    HttpStatus.FORBIDDEN,
    "OPERATION_NOT_ALLOWED",
    "This operation is not allowed."
  ),

  // 2. Used for JwtAuthenticationException
  TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "TOKEN_REQUIRED", "Token is required"),
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Token has expired"),
  TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "Token is invalid"),

  // 3. Used for BusinessException
  TOKEN_INVALIDATED(HttpStatus.UNAUTHORIZED, "TOKEN_INVALIDATED", "Token is invalidated"),
  INVALID_CREDENTIALS(
    HttpStatus.UNAUTHORIZED,
    "INVALID_CREDENTIALS",
    "Email or password is incorrect"
  ),
  PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASSWORD_NOT_MATCH", "Passwords do not match"),
  USER_EXISTED(HttpStatus.BAD_REQUEST, "USER_EXISTED", "User already exists"),
  EMAIL_USED(HttpStatus.BAD_REQUEST, "EMAIL_USED", "Email has already been used"),
  INVALID_DISCOUNT_CODE(
    HttpStatus.BAD_REQUEST,
    "INVALID_DISCOUNT_CODE",
    "Discount code is invalid or expired"
  ),

  // 4. Used for ResourceNotFoundException
  ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "Account not found"),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"),
  POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "Post not found");

  HttpStatus status;
  String code;
  String message;
}
