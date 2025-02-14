package com.todo.todo.dto;

import com.todo.todo.entity.Todo;
import com.todo.todo.type.TodoCategory;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TodoResponseDto(

    Long id,
    Long authorId,
    Long projectId,
    String title,
    String description,
    TodoCategory todoCategory,
    boolean isCompleted,
    boolean isPriority,
    Long version,
    LocalDateTime dueDate,
    LocalDateTime createdAt
) {

  public static TodoResponseDto fromEntity(Todo todo) {

    return TodoResponseDto.builder()
        .id(todo.getId())
        .authorId(todo.getAuthor().getId())
        .projectId(todo.getProjectId())
        .title(todo.getTitle())
        .description(todo.getDescription())
        .todoCategory(todo.getTodoCategory())
        .isCompleted(todo.isCompleted())
        .isPriority(todo.isPriority())
        .version(todo.getVersion())
        .dueDate(todo.getDueDate())
        .createdAt(todo.getCreatedAt())
        .build();
  }
}