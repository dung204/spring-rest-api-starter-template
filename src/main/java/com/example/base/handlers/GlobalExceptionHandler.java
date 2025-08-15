package com.example.base.handlers;

import com.example.base.dtos.ErrorResponseDTO;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponseDTO> handleHttpMethodNotSupportedException(
    HttpRequestMethodNotSupportedException e
  ) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
      new ErrorResponseDTO(HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage())
    );
  }

  @ExceptionHandler({ JwtException.class, IllegalArgumentException.class })
  public ResponseEntity<ErrorResponseDTO> handleJwtException(Exception e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage())
    );
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponseDTO> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
    String message = e
      .getParameterValidationResults()
      .stream()
      .flatMap(result -> result.getResolvableErrors().stream())
      .findFirst()
      .map(MessageSourceResolvable::getDefaultMessage)
      .orElse("Validation failed");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), message)
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidBodyFieldException(MethodArgumentNotValidException e) {
    final FieldError fieldError = e.getBindingResult().getFieldErrors().get(0);
    final String fieldName = fieldError.getField();
    final String errorMessage = fieldError.getDefaultMessage();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), "Field `%s` %s".formatted(fieldName, errorMessage))
    );
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponseDTO> handleHttpException(ResponseStatusException e) {
    if (e.getStatusCode().value() >= 500) {
      log.error("Unknown error: {}", e);
      return ResponseEntity.status(e.getStatusCode()).body(
        new ErrorResponseDTO(e.getStatusCode().value(), "Unknown error")
      );
    }

    return ResponseEntity.status(e.getStatusCode()).body(
      new ErrorResponseDTO(e.getStatusCode().value(), e.getReason())
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
    log.error("Unknown error: {}", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unknown error")
    );
  }
}
