package com.todo.collaborator.service;

import static com.todo.collaborator.type.ConfirmType.FALSE;
import static com.todo.collaborator.type.ConfirmType.TRUE;
import static com.todo.exception.ErrorCode.COLLABORATOR_NOT_FOUND;

import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.repository.CollaboratorRepository;
import com.todo.collaborator.type.RoleType;
import com.todo.exception.CustomException;
import com.todo.project.entity.Project;
import com.todo.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollaboratorQueryService {

  private final CollaboratorRepository collaboratorRepository;

  public boolean existsByProjectAndCollaboratorAndIsConfirmed(
      Project project, User collaborator) {
    return collaboratorRepository.existsByProjectAndCollaboratorAndConfirmType(
        project, collaborator, TRUE);
  }

  public Collaborator findById(Long id) {
    return collaboratorRepository.findById(id)
        .orElseThrow(() -> new CustomException(COLLABORATOR_NOT_FOUND));
  }

  public List<Collaborator> findByProject(Project project) {
    return collaboratorRepository.findByProject(project);
  }

  public boolean existsByProjectAndCollaboratorAndRoleTypeAndIsConfirmed(Project project, User user,
      RoleType roleType) {

    return collaboratorRepository.existsByProjectAndCollaboratorAndRoleTypeAndConfirmType(
        project, user, roleType, TRUE);
  }

  public Collaborator findByProjectAndCollaboratorIsConfirmed(Project project, User user) {
    return collaboratorRepository.findByProjectAndCollaboratorAndConfirmType(project, user, TRUE);
  }

  public Collaborator findByProjectAndCollaboratorIsConfirmedFalse(Project project, User user) {
    return collaboratorRepository.findByProjectAndCollaboratorAndConfirmType(project, user, FALSE);
  }
}