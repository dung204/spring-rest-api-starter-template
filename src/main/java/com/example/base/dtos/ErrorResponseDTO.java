package com.example.base.dtos;

import com.example.base.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * Data Transfer Object representing an error response.
 * <p>
 * This class extends {@link ResponseDTO} to provide a standardized structure
 * for error responses in the API. It encapsulates error information including
 * status code and error message.
 * </p>
 *
 * @see ResponseDTO
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO extends ResponseDTO {

  private final String code;
  private final List<ValidationError> errors;

  @Builder
  private ErrorResponseDTO(int status, String code, String message, List<ValidationError> errors) {
    super(validateErrorStatus(status), message);
    this.code = code;
    this.errors = errors;
  }

  public static ErrorResponseDTO of(ErrorCode errorCode) {
    return new ErrorResponseDTO(
      errorCode.getStatus().value(),
      errorCode.getCode(),
      errorCode.getMessage(),
      null
    );
  }

  public static ErrorResponseDTO of(ErrorCode errorCode, String customMessage) {
    return new ErrorResponseDTO(
      errorCode.getStatus().value(),
      errorCode.getCode(),
      customMessage,
      null
    );
  }

  public static ErrorResponseDTO ofValidation(List<ValidationError> errors) {
    return new ErrorResponseDTO(
      ErrorCode.VALIDATION_ERROR.getStatus().value(),
      ErrorCode.VALIDATION_ERROR.getCode(),
      ErrorCode.VALIDATION_ERROR.getMessage(),
      errors
    );
  }

  @Data
  @AllArgsConstructor
  public static class ValidationError {

    private String field;
    private String message;
  }
}
