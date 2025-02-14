package com.todo.collaborator.dto;

import com.todo.collaborator.type.RoleType;

public record CollaboratorDto(

    Long collaboratorId,
    RoleType roleType
) {

}