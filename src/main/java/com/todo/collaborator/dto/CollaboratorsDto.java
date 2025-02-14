package com.todo.collaborator.dto;

import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.type.ConfirmType;
import com.todo.collaborator.type.RoleType;
import lombok.Builder;

@Builder
public record CollaboratorsDto(

    Long collaboratorId,
    String name,
    RoleType roleType,
    ConfirmType confirmType
) {

  public static CollaboratorsDto fromEntity(Collaborator collaborator) {

    return CollaboratorsDto.builder()
        .collaboratorId(collaborator.getId())
        .name(collaborator.getCollaborator().getName())
        .roleType(collaborator.getRoleType())
        .confirmType(collaborator.getConfirmType())
        .build();
  }
}