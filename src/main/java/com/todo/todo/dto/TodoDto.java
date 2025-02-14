package com.todo.todo.dto;

import com.todo.todo.type.TodoCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TodoDto(

    Long projectId,

    @NotBlank(message = "제목은 필수 입력입니다.")
    String title,

    String description,

    @NotNull(message = "카테고리를 설정해 주세요.")
    TodoCategory todoCategory,

    boolean isPriority,

    LocalDateTime dueDate
) {

}