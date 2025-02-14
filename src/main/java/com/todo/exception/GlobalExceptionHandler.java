package com.todo.exception;

import static com.todo.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.todo.exception.ErrorCode.REQUEST_VALIDATION_FAIL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

    ErrorCode errorCode = e.getErrorCode();
    ErrorResponse response = ErrorResponse.from(errorCode);

    log.error("CustomException: {}, Code: {}", errorCode.getMessage(), errorCode.getHttpStatus());

    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {

    ErrorResponse response = ErrorResponse.from(INTERNAL_SERVER_ERROR);

    log.error("Unexpected: {}", e.getMessage(), e);

    return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      HttpServletRequest request, MethodArgumentNotValidException e) {

    String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    String requestUri = request.getRequestURI();

    ErrorResponse response = ErrorResponse.from(REQUEST_VALIDATION_FAIL);

    log.error("MethodArgumentNotValidException: {}, Request URI: {}", errorMessage, requestUri);

    return ResponseEntity.status(BAD_REQUEST).body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(HttpServletRequest request, ConstraintViolationException e) {

    String errorMessage = e.getMessage();
    String requestUri = request.getRequestURI();

    ErrorResponse response = ErrorResponse.from(REQUEST_VALIDATION_FAIL);

    log.error("ConstraintViolationException: {}, Request URI: {}", errorMessage, requestUri);

    return ResponseEntity.status(BAD_REQUEST).body(response);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleMethodValidationException(ConstraintViolationException e) {

    String errorMessage = e.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining(", "));

    ErrorResponse response = ErrorResponse.withMessage(REQUEST_VALIDATION_FAIL, errorMessage);

    log.error("HandlerMethodValidationException: {}", errorMessage);

    return ResponseEntity.status(BAD_REQUEST).body(response);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {

    ErrorResponse response = ErrorResponse.withMessage(INTERNAL_SERVER_ERROR, e.getMessage());

    log.error("IllegalStateException: {}", e.getMessage(), e);

    return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
  }

  @ExceptionHandler(AsyncRequestTimeoutException.class)
  public ResponseEntity<ErrorResponse> handleAsyncRequestTimeoutException(
      AsyncRequestTimeoutException e) {

    log.error("SSE Connection Timeout: {}", e.getMessage());

    return ResponseEntity.noContent().build();
  }
}