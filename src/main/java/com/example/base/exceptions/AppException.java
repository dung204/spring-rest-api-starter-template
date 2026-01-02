package com.example.base.exceptions;

import com.example.base.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppException extends RuntimeException {

  ErrorCode errorCode;
  Object[] args;

  public AppException(ErrorCode errorCode, Object... args) {
    super(errorCode.getMessageKey());
    this.errorCode = errorCode;
    this.args = args;
  }

  public AppException(ErrorCode errorCode, String customMessage, Object... args) {
    super(customMessage);
    this.errorCode = errorCode;
    this.args = args;
  }

  public AppException(ErrorCode errorCode, Throwable cause, Object... args) {
    super(errorCode.getMessageKey(), cause);
    this.errorCode = errorCode;
    this.args = args;
  }

  public AppException(ErrorCode errorCode, String customMessage, Throwable cause, Object... args) {
    super(customMessage, cause);
    this.errorCode = errorCode;
    this.args = args;
  }
}
