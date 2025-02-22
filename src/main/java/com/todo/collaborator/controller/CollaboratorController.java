package com.todo.collaborator.controller;

import com.todo.collaborator.dto.CollaboratorDto;
import com.todo.collaborator.dto.CollaboratorsDto;
import com.todo.collaborator.service.CollaboratorService;
import com.todo.collaborator.type.ConfirmType;
import com.todo.collaborator.type.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Collaborator API", description = "협업 인원 관리 API")
public class CollaboratorController {

  private final CollaboratorService collaboratorService;

  @PostMapping("/{projectId}/collaborators")
  @Operation(summary = "협업 인원 초대 API", description = "협업할 인원을 프로젝트에 초대할 수 있습니다.")
  public ResponseEntity<Void> addCollaborator(Authentication auth,
      @PathVariable("projectId") Long projectId,
      @RequestBody CollaboratorDto collaboratorDto) {

    collaboratorService.addCollaborator(auth, projectId, collaboratorDto);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{projectId}/collaborators")
  @Operation(summary = "협업 인원 목록 조회 API", description = "프로젝트에 속한 협업인원들을 조회할 수 있습니다.")
  public ResponseEntity<List<CollaboratorsDto>> getCollaborators(Authentication auth,
      @PathVariable("projectId") Long projectId) {

    return ResponseEntity.ok(collaboratorService.getCollaborators(auth, projectId));
  }

  @PutMapping("/{projectId}/collaborators/{collaboratorId}")
  @Operation(summary = "협업 인원 권한 수정 API", description = "협업중인 인원의 권한을 변경할 수 있습니다.")
  public ResponseEntity<List<CollaboratorsDto>> updateCollaborator(Authentication auth,
      @PathVariable("projectId") Long projectId,
      @PathVariable("collaboratorId") Long collaboratorId,
      @RequestBody RoleType roleType) {

    return ResponseEntity.ok(
        collaboratorService.updateCollaborator(auth, projectId, collaboratorId, roleType));
  }

  @PutMapping("/{projectId}/collaborators/confirm")
  @Operation(summary = "협업 인원 초대 승인 API", description = "프로젝트에 초대된 사용자가 초대를 승인할 수 있습니다.")
  public ResponseEntity<Void> updateCornFirm(Authentication auth,
      @PathVariable("projectId") Long projectId,
      @RequestBody ConfirmType confirmType) {

    collaboratorService.updateConfirm(auth, projectId, confirmType);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{projectId}/collaborators/{collaboratorId}")
  @Operation(summary = "협업 인원 제외 API", description = "프로젝트에 속한 인원을 제외시킬 수 있습니다.")
  public ResponseEntity<List<CollaboratorsDto>> deleteCollaborator(Authentication auth,
      @PathVariable("projectId") Long projectId,
      @PathVariable("collaboratorId") Long collaboratorId) {

    return ResponseEntity.ok(
        collaboratorService.deleteCollaborator(auth, projectId, collaboratorId));
  }
}