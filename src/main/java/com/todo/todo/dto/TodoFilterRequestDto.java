package com.todo.todo.dto;

import com.todo.todo.type.TodoCategory;

public record TodoFilterRequestDto(

    Long authorId,
    TodoCategory todoCategory,
    Boolean isPriority,
    Boolean isCompleted
) {

}
