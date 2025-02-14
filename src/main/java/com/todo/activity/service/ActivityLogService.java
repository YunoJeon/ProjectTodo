package com.todo.activity.service;

import static com.todo.exception.ErrorCode.FORBIDDEN;

import com.todo.activity.dto.ActivityLogResponseDto;
import com.todo.activity.entity.ActivityLog;
import com.todo.activity.repository.ActivityLogRepository;
import com.todo.activity.type.ActionType;
import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.service.CollaboratorQueryService;
import com.todo.exception.CustomException;
import com.todo.project.entity.Project;
import com.todo.project.service.ProjectQueryService;
import com.todo.todo.entity.Todo;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActivityLogService {

  private final ActivityLogRepository activityLogRepository;

  private final UserQueryService userQueryService;

  private final ProjectQueryService projectQueryService;

  private final CollaboratorQueryService collaboratorQueryService;

  private final ActivityQueryService activityQueryService;

  public Page<ActivityLogResponseDto> getActivityLogs(
      Authentication auth, Long projectId, int page, int pageSize) {

    User user = userQueryService.findByEmail(auth.getName());

    Project project = projectQueryService.findById(projectId);

    Collaborator collaborator = collaboratorQueryService.findByProjectAndCollaboratorIsConfirmed(
        project, user);

    if (collaborator == null) {
      throw new CustomException(FORBIDDEN);
    }

    Pageable pageable = PageRequest.of(page - 1, pageSize);

    Page<ActivityLog> activityLogs = activityQueryService.findByProject(project,
        pageable);

    List<ActivityLogResponseDto> dtoList = activityLogs.getContent().stream()
        .map(ActivityLogResponseDto::fromEntity).toList();

    return new PageImpl<>(dtoList, pageable, activityLogs.getTotalElements());
  }

  @Transactional
  public void saveLog(Project project, Todo todo, ActionType actionType, String changerName,
      String actionDetail, Long todoVersion, Long snapshotId) {

    ActivityLog activityLog = ActivityLog.of(project, todo, actionType, actionDetail, changerName, todoVersion, snapshotId);

    activityLogRepository.save(activityLog);
  }

  public void recordTodoCreation(Project project, Todo todo, ActionType actionType,
      String changerName, Long todoVersion, Long snapshotId) {

    String actionDetail = String.format("%s 할일이 생성되었습니다.", todo.getTitle());

    saveLog(project, todo, actionType, changerName, actionDetail, todoVersion, snapshotId);
  }

  public void recordTodoUpdate(Project project, Todo todo, ActionType actionType,
      String changerName, Long todoVersion, Long snapshotId) {

    String actionDetail = String.format("%s 할일이 수정 되었습니다.", todo.getTitle());

    saveLog(project, todo, actionType, changerName, actionDetail, todoVersion, snapshotId);
  }

  public void recordTodoComplete(Project project, Todo todo, ActionType actionType,
      String changerName, Long todoVersion, Long snapshotId) {

    String actionDetail = String.format("%s 할일이 완료 되었습니다.", todo.getTitle());

    saveLog(project, todo, actionType, changerName, actionDetail, todoVersion, snapshotId);
  }

  public void recordTodoRollback(Project project, Todo todo, ActionType actionType,
      String changerName, Long previousVersion, Long currentVersion, Long snapshotId) {

    String actionDetail = String.format("%s 님이 %s 할일을 롤백하였습니다. (롤백 전 버전: %d)",
        changerName, todo.getTitle(), previousVersion);

    saveLog(project, todo, actionType, changerName, actionDetail, currentVersion, snapshotId);
  }

  public void recordProjectParticipation(Project project, ActionType actionType,
      String invitedUser) {

    String actionDetail = String.format("%s 님이 %s 프로젝트에 참여하였습니다.", invitedUser, project.getName());

    saveLog(project, null, actionType, invitedUser, actionDetail, null, null);
  }

  public void recordProjectExclusion(Project project, ActionType actionType, String deletedUser) {

    String actionDetail = String.format("%s 님이 %s 프로젝트에 제외되었습니다.", deletedUser, project.getName());

    saveLog(project, null, actionType, deletedUser, actionDetail, null, null);
  }
}