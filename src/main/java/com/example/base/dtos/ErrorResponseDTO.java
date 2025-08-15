package com.example.base.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ErrorResponseDTO extends ResponseDTO {

  public ErrorResponseDTO(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
