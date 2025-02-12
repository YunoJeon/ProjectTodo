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
  INVALID_CREDENTIALS(UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),

  // 403 FORBIDDEN
  FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

  // 404 NOT FOUND
  USER_NOT_FOUND(NOT_FOUND, "회원을 찾을 수 없습니다."),
  TODO_NOT_FOUND(NOT_FOUND, "할일을 찾을 수 없습니다."),
  PROJECT_NOT_FOUND(NOT_FOUND, "프로젝트를 찾을 수 없습니다."),
  COLLABORATOR_NOT_FOUND(NOT_FOUND, "협업자를 찾을 수 없습니다."),

  // 408 REQUEST TIMEOUT

  // 409 CONFLICT
  ALREADY_EXISTS_EMAIL(CONFLICT, "이미 존재하는 메일입니다."),
  VERSION_CONFLICT(CONFLICT, "동시성 충돌이 발생했습니다. 다시 시도해 주세요."),
  ALREADY_EXISTS_USER(CONFLICT, "이미 초대된 회원입니다."),

  // 500 INTERNAL SEVER ERROR
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}