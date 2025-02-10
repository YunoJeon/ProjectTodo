package com.todo.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(

    @NotBlank(message = "비밀번호는 필수 입력 입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String password,

    @NotBlank(message = "현재 비밀번호는 필수 입력 입니다.")
    String confirmPassword
) {

}