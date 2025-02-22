package com.todo.collaborator.service;

import static com.todo.activity.type.ActionType.PROJECT;
import static com.todo.collaborator.type.ConfirmType.FALSE;
import static com.todo.collaborator.type.ConfirmType.TRUE;
import static com.todo.collaborator.type.RoleType.EDITOR;
import static com.todo.exception.ErrorCode.ALREADY_EXISTS_USER;
import static com.todo.exception.ErrorCode.FORBIDDEN;

import com.todo.activity.service.ActivityLogService;
import com.todo.collaborator.dto.CollaboratorDto;
import com.todo.collaborator.dto.CollaboratorsDto;
import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.repository.CollaboratorRepository;
import com.todo.collaborator.type.ConfirmType;
import com.todo.collaborator.type.RoleType;
import com.todo.exception.CustomException;
import com.todo.notification.service.NotificationService;
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

  private final NotificationService notificationService;

  private final ActivityLogService activityLogService;

  @Transactional
  public void addCollaborator(Authentication auth, Long projectId,
      CollaboratorDto collaboratorDto) {

    Project project = findOwnerWithProject(auth, projectId);

    User owner = registerOwner(project.getOwner(), project);

    User invitedUser = userQueryService.findById(collaboratorDto.collaboratorId());

    if (collaboratorQueryService.existsByProjectAndCollaboratorAndIsConfirmed(
        project, invitedUser)) {
      throw new CustomException(ALREADY_EXISTS_USER);
    }

    collaboratorRepository.save(
        Collaborator.of(invitedUser, project, collaboratorDto.roleType(), FALSE));

    notificationService.sendInvitationNotification(owner, invitedUser, project);
  }

  private User registerOwner(User owner, Project project) {

    boolean ownerAlreadyExists = collaboratorQueryService.existsByProjectAndCollaboratorAndIsConfirmed(
        project, owner);

    if (!ownerAlreadyExists) {
      collaboratorRepository.save(Collaborator.of(owner, project, EDITOR, TRUE));
    }
    return owner;
  }

  public List<CollaboratorsDto> getCollaborators(Authentication auth, Long projectId) {

    User collaborator = userQueryService.findByEmail(auth.getName());

    Project project = projectQueryService.findById(projectId);

    if (!collaboratorQueryService.existsByProjectAndCollaboratorAndIsConfirmed(
        project, collaborator)) {
      throw new CustomException(FORBIDDEN);
    }

    List<Collaborator> collaborators = collaboratorQueryService.findByProject(project);

    return collaborators.stream().map(CollaboratorsDto::fromEntity).collect(Collectors.toList());
  }

  @Transactional
  public List<CollaboratorsDto> updateCollaborator(Authentication auth, Long projectId,
      Long collaboratorId, RoleType roleType) {

    Long userId = userQueryService.findByEmail(auth.getName()).getId();

    Project project = findOwnerWithProject(auth, projectId);

    Collaborator collaborator = collaboratorQueryService.findById(collaboratorId);

    if (userId.equals(collaborator.getCollaborator().getId())) {
      throw new CustomException(FORBIDDEN, "자기 자신은 수정할 수 없습니다.");
    }

    collaborator.update(roleType);

    List<Collaborator> collaborators = collaboratorQueryService.findByProject(project);

    return collaborators.stream().map(CollaboratorsDto::fromEntity).collect(Collectors.toList());
  }

  @Transactional
  public List<CollaboratorsDto> deleteCollaborator(Authentication auth, Long projectId,
      Long collaboratorId) {

    Project project = findOwnerWithProject(auth, projectId);

    Collaborator collaborator = collaboratorQueryService.findById(collaboratorId);

    User deletedUser = collaborator.getCollaborator();

    if (deletedUser.getId().equals(project.getOwner().getId())) {
      throw new CustomException(FORBIDDEN, "자기 자신은 삭제할 수 없습니다.");
    }

    collaboratorRepository.delete(collaborator);

    List<Collaborator> collaborators = collaboratorQueryService.findByProject(project);

    List<User> users = collaborators.stream().map(Collaborator::getCollaborator).toList();

    notificationService.notifyProjectUserRemoval(users, deletedUser, project);

    activityLogService.recordProjectExclusion(project, PROJECT, deletedUser.getName());

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

  @Transactional
  public void updateConfirm(Authentication auth, Long projectId, ConfirmType confirmType) {

    User invitedUser = userQueryService.findByEmail(auth.getName());

    Project project = projectQueryService.findById(projectId);

    Collaborator collaborator = collaboratorQueryService.findByProjectAndCollaboratorIsConfirmedFalse(
        project, invitedUser);

    if (!invitedUser.getId().equals(collaborator.getCollaborator().getId())) {
      throw new CustomException(FORBIDDEN);
    }

    collaborator.updateConfirm(confirmType);

    List<Collaborator> collaborators = collaboratorQueryService.findByProject(project);

    List<User> users = collaborators.stream().map(Collaborator::getCollaborator).toList();

    notificationService.notifyUserOnJoin(users, invitedUser, project);

    activityLogService.recordProjectParticipation(project, PROJECT, invitedUser.getName());
  }
}