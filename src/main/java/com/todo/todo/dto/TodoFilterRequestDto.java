package com.todo.todo.dto;

import com.todo.todo.type.TodoCategory;

public record TodoFilterRequestDto(

    Long authorId,
    Long projectId,
    TodoCategory todoCategory,
    Boolean isPriority,
    Boolean isCompleted
) {

}
