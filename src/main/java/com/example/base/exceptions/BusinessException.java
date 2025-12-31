package com.example.base.exceptions;

import com.example.base.enums.ErrorCode;

public class BusinessException extends BaseException {

  public BusinessException(ErrorCode errorCode) {
    super(errorCode);
  }

  public BusinessException(ErrorCode errorCode, String customMessage) {
    super(errorCode, customMessage);
  }
}
