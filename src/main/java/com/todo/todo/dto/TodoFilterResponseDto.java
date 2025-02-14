package com.todo.todo.dto;

import com.todo.todo.entity.Todo;
import com.todo.todo.type.TodoCategory;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TodoFilterResponseDto(

    Long id,
    Long authorId,
    Long projectId,
    String title,
    TodoCategory todoCategory,
    boolean isCompleted,
    boolean isPriority,
    LocalDateTime dueDate
) {

  public static TodoFilterResponseDto fromEntity(Todo todo) {

    return TodoFilterResponseDto.builder()
        .id(todo.getId())
        .authorId(todo.getAuthor().getId())
        .title(todo.getTitle())
        .todoCategory(todo.getTodoCategory())
        .isCompleted(todo.isCompleted())
        .isPriority(todo.isPriority())
        .dueDate(todo.getDueDate())
        .build();
  }
}