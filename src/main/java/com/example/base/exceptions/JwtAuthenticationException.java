package com.example.base.exceptions;

import com.example.base.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtAuthenticationException extends RuntimeException {

  ErrorCode errorCode;

  public JwtAuthenticationException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  // Constructor kèm theo Throwable để hứng lỗi gốc của thư viện (nếu cần log)
  public JwtAuthenticationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }
}
