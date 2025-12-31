package com.example.base.handlers;

import static com.example.base.enums.ErrorCode.INVALID_REQUEST;
import static com.example.base.enums.ErrorCode.UNKNOWN_ERROR;

import com.example.base.dtos.ErrorResponseDTO;
import com.example.base.enums.ErrorCode;
import com.example.base.exceptions.BaseException;
import io.jsonwebtoken.JwtException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ErrorResponseDTO> handleBaseException(BaseException e) {
    ErrorCode errorCode = e.getErrorCode();

    ErrorResponseDTO response = ErrorResponseDTO.of(errorCode, e.getMessage());

    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponseDTO> handleHttpMethodNotSupportedException(
    HttpRequestMethodNotSupportedException e
  ) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
      ErrorResponseDTO.builder()
        .status(HttpStatus.METHOD_NOT_ALLOWED.value())
        .message(e.getMessage())
        .build()
    );
  }

  @ExceptionHandler({ JwtException.class, IllegalArgumentException.class })
  public ResponseEntity<ErrorResponseDTO> handleJwtException(Exception e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      ErrorResponseDTO.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(e.getMessage())
        .build()
    );
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<ErrorResponseDTO> handleMissingServletRequestPartException(
    MissingServletRequestPartException e
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      ErrorResponseDTO.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(e.getMessage())
        .build()
    );
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponseDTO> handleMaxUploadSizeExceededException(
    MaxUploadSizeExceededException e
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      ErrorResponseDTO.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(e.getMessage())
        .build()
    );
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponseDTO> handleHandlerMethodValidationException(
    HandlerMethodValidationException e
  ) {
    String message = e
      .getParameterValidationResults()
      .stream()
      .flatMap(result -> result.getResolvableErrors().stream())
      .findFirst()
      .map(MessageSourceResolvable::getDefaultMessage)
      .orElse("Validation failed");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      ErrorResponseDTO.builder().status(HttpStatus.BAD_REQUEST.value()).message(message).build()
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidBodyFieldException(
    MethodArgumentNotValidException e
  ) {
    List<ErrorResponseDTO.ValidationError> errors = e
      .getBindingResult()
      .getFieldErrors()
      .stream()
      .map(error ->
        new ErrorResponseDTO.ValidationError(error.getField(), error.getDefaultMessage())
      )
      .toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      ErrorResponseDTO.ofValidation(errors)
    );
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDTO> handleJsonError(HttpMessageNotReadableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDTO.of(INVALID_REQUEST));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleUnknownException(Exception e) {
    log.error("Unknown error: ", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      ErrorResponseDTO.of(UNKNOWN_ERROR)
    );
  }
}
