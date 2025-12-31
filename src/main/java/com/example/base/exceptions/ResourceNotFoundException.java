package com.example.base.exceptions;

import com.example.base.enums.ErrorCode;

public class ResourceNotFoundException extends BaseException {

  public ResourceNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ResourceNotFoundException(String resourceName, Object id) {
    super(ErrorCode.UNKNOWN_ERROR, String.format("%s with id %s not found", resourceName, id));
  }
}
