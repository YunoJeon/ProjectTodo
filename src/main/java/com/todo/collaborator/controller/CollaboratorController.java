package com.todo.collaborator.controller;

import com.todo.collaborator.dto.CollaboratorDto;
import com.todo.collaborator.dto.CollaboratorsDto;
import com.todo.collaborator.service.CollaboratorService;
import com.todo.collaborator.type.RoleType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class CollaboratorController {

  private final CollaboratorService collaboratorService;

  @PostMapping("/{projectId}/collaborators")
  public ResponseEntity<Void> addCollaborator(Authentication auth,
      @PathVariable("projectId") Long projectId,
      @RequestBody CollaboratorDto collaboratorDto) {

    collaboratorService.addCollaborator(auth, projectId, collaboratorDto);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{projectId}/collaborators")
  public ResponseEntity<List<CollaboratorsDto>> getCollaborators(Authentication auth,
      @PathVariable("projectId") Long projectId) {

    return ResponseEntity.ok(collaboratorService.getCollaborators(auth, projectId));
  }

  @PutMapping("/{projectId}/collaborators/{collaboratorId}")
  public ResponseEntity<List<CollaboratorsDto>> updateCollaborator(Authentication auth,
      @PathVariable("projectId") Long projectId,
      @PathVariable("collaboratorId") Long collaboratorId,
      @RequestBody RoleType roleType) {

    return ResponseEntity.ok(
        collaboratorService.updateCollaborator(auth, projectId, collaboratorId, roleType));
  }

  @DeleteMapping("/{projectId}/collaborators/{collaboratorId}")
  public ResponseEntity<List<CollaboratorsDto>> deleteCollaborator(Authentication auth,
      @PathVariable("projectId") Long projectId,
      @PathVariable("collaboratorId") Long collaboratorId) {

    return ResponseEntity.ok(
        collaboratorService.deleteCollaborator(auth, projectId, collaboratorId));
  }
}