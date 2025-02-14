package com.todo.snapshot.service;

import static com.todo.activity.type.ActionType.TODO;
import static com.todo.collaborator.type.RoleType.EDITOR;
import static com.todo.exception.ErrorCode.FORBIDDEN;
import static com.todo.exception.ErrorCode.PROJECT_NOT_FOUND;
import static com.todo.exception.ErrorCode.SNAPSHOT_NOT_FOUND;

import com.todo.activity.service.ActivityLogService;
import com.todo.collaborator.service.CollaboratorQueryService;
import com.todo.exception.CustomException;
import com.todo.project.entity.Project;
import com.todo.project.service.ProjectQueryService;
import com.todo.snapshot.entity.Snapshot;
import com.todo.snapshot.repository.SnapShotRepository;
import com.todo.todo.entity.Todo;
import com.todo.todo.service.TodoQueryService;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SnapShotService {

  private final SnapShotRepository snapShotRepository;

  private final UserQueryService userQueryService;

  private final TodoQueryService todoQueryService;

  private final ProjectQueryService projectQueryService;

  private final CollaboratorQueryService collaboratorQueryService;

  private final ActivityLogService activityLogService;

  public Long saveSnapshot(Todo todo) {

    return snapShotRepository.save(Snapshot.of(todo)).getId();
  }

  public void restoreTodo(Authentication auth, Long todoId, Long snapshotId) {

    User user = userQueryService.findByEmail(auth.getName());

    Todo todo = todoQueryService.findById(todoId);

    Snapshot snapshot = snapShotRepository.findById(snapshotId)
        .orElseThrow(() -> new CustomException(SNAPSHOT_NOT_FOUND));

    Long projectId = todo.getProjectId();

    if (projectId == null) {
      throw new CustomException(PROJECT_NOT_FOUND);
    }

    Project project = projectQueryService.findById(projectId);

    boolean isCollaborator = collaboratorQueryService.existsByProjectAndCollaboratorAndRoleTypeAndIsConfirmed(
        project, user, EDITOR);

    if (!isCollaborator) {
      throw new CustomException(FORBIDDEN);
    }

    Long previousVersion = todo.getVersion();

    todo.restoreTodo(snapshot);

    Long currentVersion = todo.getVersion();

    activityLogService.recordTodoRollback(
        project, todo, TODO, user.getName(), previousVersion, currentVersion, snapshotId);
  }
}