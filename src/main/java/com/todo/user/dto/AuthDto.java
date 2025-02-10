package com.todo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthDto(

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력 입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수 입력 입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String password,

    @NotBlank(message = "연락처는 필수 입력 입니다.")
    @Pattern(regexp = "01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "대한민국 전화번호 형식에 맞게 입력해주세요.")
    String phone,

    @NotBlank(message = "이름은 필수 입력 입니다.")
    String name,

    String profileImageUrl
) {

}