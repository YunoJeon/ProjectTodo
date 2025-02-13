package com.todo.activity.service;

import static com.todo.activity.type.ActionType.PROJECT;
import static com.todo.activity.type.ActionType.TODO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todo.activity.dto.ActivityLogResponseDto;
import com.todo.activity.entity.ActivityLog;
import com.todo.activity.repository.ActivityLogRepository;
import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.service.CollaboratorQueryService;
import com.todo.project.entity.Project;
import com.todo.project.service.ProjectQueryService;
import com.todo.todo.entity.Todo;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

  @Mock
  private ActivityLogRepository activityLogRepository;

  @Mock
  private UserQueryService userQueryService;

  @Mock
  private ProjectQueryService projectQueryService;

  @Mock
  private CollaboratorQueryService collaboratorQueryService;

  @Mock
  private ActivityQueryService activityQueryService;

  @InjectMocks
  private ActivityLogService activityLogService;

  private Authentication auth;
  private User testUser;
  private Project project;
  private Todo todo;
  private Collaborator collaborator;

  @BeforeEach
  void setUp() {

    auth = new UsernamePasswordAuthenticationToken("test@test.com", null);

    testUser = User.builder()
        .id(1L)
        .email("test@test.com")
        .build();

    project = Project.builder()
        .id(10L)
        .name("프로젝트")
        .build();

    todo = Todo.builder()
        .id(20L)
        .title("투두")
        .build();

    collaborator = Collaborator.builder()
        .id(30L)
        .collaborator(testUser)
        .build();
  }

  @Test
  @DisplayName("활동 로그 조회에 성공한다")
  void get_activity_log_success() {
    // given
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(collaboratorQueryService.findByProjectAndCollaboratorIsConfirmed(project, testUser)).thenReturn(collaborator);

    ActivityLog activityLog = ActivityLog.builder()
        .id(100L)
        .project(project)
        .build();

    ActivityLog activityLog2 = ActivityLog.builder()
        .id(200L)
        .project(project)
        .build();

    ActivityLog activityLog3 = ActivityLog.builder()
        .id(300L)
        .project(project)
        .build();

    List<ActivityLog> logs = List.of(activityLog, activityLog2, activityLog3);

    Page<ActivityLog> logPage = new PageImpl<>(logs, PageRequest.of(0, 10), logs.size());
    when(activityQueryService.findByProject(project, PageRequest.of(0, 10))).thenReturn(logPage);
    // when
    Page<ActivityLogResponseDto> result = activityLogService.getActivityLogs(auth, project.getId(), 1, 10);
    // then
    assertEquals(3, result.getTotalElements());
  }

  @Test
  @DisplayName("투두 활동 로그 저장에 성공한다")
  void save_todo_activity_log_success() {
    // given
    String actionDetail = String.format("%s 할일이 생성되었습니다.", todo.getTitle());
    // when
    activityLogService.recordTodoCreation(project, todo, TODO, actionDetail);
    // then
    verify(activityLogRepository).save(any(ActivityLog.class));
  }

  @Test
  @DisplayName("프로젝트 참여 활동 로그 저장에 성공한다")
  void save_project_join_activity_log_success() {
    // given
    String actionDetail = String.format("%s 님이 %s 프로젝트에 참여하였습니다.", testUser.getName(), project.getName());
    // when
    activityLogService.recordProjectParticipation(project, PROJECT, actionDetail);
    // then
    verify(activityLogRepository).save(any(ActivityLog.class));
  }
}