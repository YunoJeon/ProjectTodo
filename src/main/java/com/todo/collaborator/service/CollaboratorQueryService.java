package com.todo.collaborator.service;

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
      Project project, User collaborator, boolean isConfirmed) {
    return collaboratorRepository.existsByProjectAndCollaboratorAndConfirmed(
        project, collaborator, isConfirmed);
  }

  public Collaborator findById(Long id) {
    return collaboratorRepository.findById(id)
        .orElseThrow(() -> new CustomException(COLLABORATOR_NOT_FOUND));
  }

  public List<Collaborator> findByProject(Project project) {
    return collaboratorRepository.findByProject(project);
  }

  public List<Project> findProjectsByCollaborator(User user) {
    return collaboratorRepository.findProjectsByCollaborator(user);
  }

  public boolean existsByProjectAndCollaboratorAndRoleTypeAndConfirmed(Project project, User user,
      RoleType roleType, boolean isConfirmed) {

    return collaboratorRepository.existsByProjectAndCollaboratorAndRoleTypeAndConfirmed(
        project, user, roleType, isConfirmed);
  }
}