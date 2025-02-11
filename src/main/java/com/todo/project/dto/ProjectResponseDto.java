package com.todo.project.dto;

import com.todo.project.entity.Project;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ProjectResponseDto(

    Long id,
    String ownerName,
    String name,
    String description,
    LocalDateTime createdAt
) {

  public static ProjectResponseDto fromEntity(Project project) {

    return ProjectResponseDto.builder()
        .id(project.getId())
        .ownerName(project.getOwner().getName())
        .name(project.getName())
        .description(project.getDescription())
        .createdAt(project.getCreatedAt())
        .build();
  }
}