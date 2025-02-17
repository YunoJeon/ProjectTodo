package com.todo.project.service;

import static com.todo.exception.ErrorCode.FORBIDDEN;

import com.todo.collaborator.service.CollaboratorQueryService;
import com.todo.exception.CustomException;
import com.todo.project.dto.ProjectDto;
import com.todo.project.dto.ProjectPageResponseDto;
import com.todo.project.dto.ProjectResponseDto;
import com.todo.project.entity.Project;
import com.todo.project.repository.ProjectRepository;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

  private final ProjectRepository projectRepository;

  private final UserQueryService userQueryService;

  private final ProjectQueryService projectQueryService;

  private final CollaboratorQueryService collaboratorQueryService;

  @Transactional
  public void createProject(Authentication auth, ProjectDto projectDto) {

    projectRepository.save(Project.of(userQueryService.findByEmail(auth.getName()), projectDto));
  }

  public Page<ProjectPageResponseDto> getProjects(Authentication auth, int page, int pageSize) {

    User user = userQueryService.findByEmail(auth.getName());

    Page<Project> projects = projectRepository.findProjectByUser(user,
        PageRequest.of(page - 1, pageSize));

    return projects.map(project -> new ProjectPageResponseDto(project.getId(), project.getName()));
  }

  public ProjectResponseDto getProjectDetail(Authentication auth, Long projectId) {

    User currentUser = userQueryService.findByEmail(auth.getName());

    Project project = projectQueryService.findById(projectId);

    boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
    boolean isCollaboratorConfirmed = collaboratorQueryService.existsByProjectAndCollaboratorAndIsConfirmed(
        project, currentUser);

    if (!isOwner && !isCollaboratorConfirmed) {

      throw new CustomException(FORBIDDEN);
    }
    return ProjectResponseDto.fromEntity(project);
  }

  @Transactional
  public ProjectResponseDto updateProject(Authentication auth, Long projectId,
      ProjectDto projectDto) {

    Long ownerId = userQueryService.findByEmail(auth.getName()).getId();

    Project project = projectQueryService.findById(projectId);

    if (!project.getOwner().getId().equals(ownerId)) {
      throw new CustomException(FORBIDDEN);
    }

    project.update(projectDto);

    return ProjectResponseDto.fromEntity(project);
  }
}