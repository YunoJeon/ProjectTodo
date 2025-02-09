package com.todo.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // 400 BAD REQUEST
  REQUEST_VALIDATION_FAIL(BAD_REQUEST, "잘못된 요청 값입니다"),

  // 401 UNAUTHORIZED
  INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),

  // 403 FORBIDDEN
  FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

  // 404 NOT FOUND

  // 408 REQUEST TIMEOUT

  // 409 CONFLICT

  // 500 INTERNAL SEVER ERROR
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}