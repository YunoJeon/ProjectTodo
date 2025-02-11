package com.todo.project.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectDto(

    @NotBlank(message = "프로젝트명은 필수 입력입니다.")
    String name,
    String description
) {

}