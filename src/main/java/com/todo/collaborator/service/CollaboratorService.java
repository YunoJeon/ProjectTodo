package com.todo.collaborator.service;

import static com.todo.collaborator.type.RoleType.EDITOR;
import static com.todo.exception.ErrorCode.ALREADY_EXISTS_USER;
import static com.todo.exception.ErrorCode.FORBIDDEN;

import com.todo.collaborator.dto.CollaboratorDto;
import com.todo.collaborator.dto.CollaboratorsDto;
import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.repository.CollaboratorRepository;
import com.todo.collaborator.type.RoleType;
import com.todo.exception.CustomException;
import com.todo.project.entity.Project;
import com.todo.project.service.ProjectQueryService;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollaboratorService {

  private final UserQueryService userQueryService;

  private final ProjectQueryService projectQueryService;

  private final CollaboratorQueryService collaboratorQueryService;

  private final CollaboratorRepository collaboratorRepository;

  @Transactional
  public void addCollaborator(Authentication auth, Long projectId,
      CollaboratorDto collaboratorDto) {

    Project project = findOwnerWithProject(auth, projectId);

    registerOwner(project.getOwner(), project);

    User invitedUser = userQueryService.findById(collaboratorDto.collaboratorId());

    if (collaboratorQueryService.existsByProjectAndCollaboratorAndIsConfirmed(
        project, invitedUser, true)) {
      throw new CustomException(ALREADY_EXISTS_USER);
    }

    collaboratorRepository.save(
        Collaborator.of(invitedUser, project, collaboratorDto.roleType(), false));
  }

  private void registerOwner(User owner, Project project) {

    boolean ownerAlreadyExists = collaboratorQueryService.existsByProjectAndCollaboratorAndIsConfirmed(
        project, owner, true);

    if (!ownerAlreadyExists) {
      collaboratorRepository.save(Collaborator.of(owner, project, EDITOR, true));
    }
  }

  public List<CollaboratorsDto> getCollaborators(Authentication auth, Long projectId) {

    User collaborator = userQueryService.findByEmail(auth.getName());

    Project project = projectQueryService.findById(projectId);

    if (!collaboratorQueryService.existsByProjectAndCollaboratorAndIsConfirmed(
        project, collaborator, true)) {
      throw new CustomException(FORBIDDEN);
    }

    List<Collaborator> collaborators = collaboratorQueryService.findByProject(project);

    return collaborators.stream().map(CollaboratorsDto::fromEntity).collect(Collectors.toList());
  }

  @Transactional
  public List<CollaboratorsDto> updateCollaborator(Authentication auth, Long projectId,
      Long collaboratorId, RoleType roleType) {

    Project project = findOwnerWithProject(auth, projectId);

    Collaborator collaborator = collaboratorQueryService.findById(collaboratorId);

    collaborator.update(roleType);

    List<Collaborator> collaborators = collaboratorQueryService.findByProject(project);

    return collaborators.stream().map(CollaboratorsDto::fromEntity).collect(Collectors.toList());
  }

  @Transactional
  public List<CollaboratorsDto> deleteCollaborator(Authentication auth, Long projectId,
      Long collaboratorId) {

    Project project = findOwnerWithProject(auth, projectId);

    collaboratorRepository.delete(collaboratorQueryService.findById(collaboratorId));

    List<Collaborator> collaborators = collaboratorQueryService.findByProject(project);

    return collaborators.stream().map(CollaboratorsDto::fromEntity).collect(Collectors.toList());
  }

  public Project findOwnerWithProject(Authentication auth, Long projectId) {

    User owner = userQueryService.findByEmail(auth.getName());

    Project project = projectQueryService.findById(projectId);

    if (!project.getOwner().getId().equals(owner.getId())) {
      throw new CustomException(FORBIDDEN);
    }
    return project;
  }
}