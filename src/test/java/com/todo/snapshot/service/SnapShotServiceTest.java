package com.todo.snapshot.service;

import static com.todo.activity.type.ActionType.TODO;
import static com.todo.collaborator.type.RoleType.EDITOR;
import static com.todo.exception.ErrorCode.FORBIDDEN;
import static com.todo.exception.ErrorCode.PROJECT_NOT_FOUND;
import static com.todo.exception.ErrorCode.SNAPSHOT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class SnapShotServiceTest {

  @Mock
  private SnapShotRepository snapShotRepository;
  @Mock
  private UserQueryService userQueryService;
  @Mock
  private TodoQueryService todoQueryService;
  @Mock
  private ProjectQueryService projectQueryService;
  @Mock
  private CollaboratorQueryService collaboratorQueryService;
  @Mock
  private ActivityLogService activityLogService;

  @InjectMocks
  private SnapShotService snapShotService;

  private Authentication auth;
  private User testUser;
  private Todo todo;
  private Snapshot snapshot;
  private Project project;

  @BeforeEach
  void setUp() {

    auth = new UsernamePasswordAuthenticationToken("test@test.com", null);

    testUser = User.builder()
        .id(1L)
        .email("test@test.com")
        .name("사용자")
        .build();

    project = Project.builder()
        .id(10L)
        .build();

    todo = Todo.builder()
        .id(30L)
        .title("투두")
        .projectId(project.getId())
        .version(5L)
        .dueDate(LocalDateTime.now().plusDays(1))
        .build();

    snapshot = Snapshot.builder()
        .id(50L)
        .todo(todo)
        .title("스냅샷 투두")
        .version(3L)
        .dueDate(todo.getDueDate())
        .build();
  }

  @Test
  @DisplayName("투두 롤백에 성공한다")
  void restore_todo_success() {
    // given
    when(userQueryService.findByEmail(auth.getName())).thenReturn(testUser);
    when(todoQueryService.findById(todo.getId())).thenReturn(todo);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(snapShotRepository.findById(snapshot.getId())).thenReturn(Optional.of(snapshot));
    when(collaboratorQueryService.existsByProjectAndCollaboratorAndRoleTypeAndIsConfirmed(
        project, testUser, EDITOR)).thenReturn(true);
    // when
    snapShotService.restoreTodo(auth, todo.getId(), snapshot.getId());
    // then
    verify(activityLogService).recordTodoRollback(
        eq(project),
        eq(todo),
        eq(TODO),
        eq(testUser.getName()),
        eq(todo.getVersion()),
        eq(todo.getVersion()),
        eq(snapshot.getId())
    );
  }

  @Test
  @DisplayName("스냅샷이 존재하지 않으면 투두 롤백에 실패한다")
  void restore_todo_failure_snapshot_not_found() {
    // given
    when(userQueryService.findByEmail(auth.getName())).thenReturn(testUser);
    when(todoQueryService.findById(todo.getId())).thenReturn(todo);
    when(snapShotRepository.findById(snapshot.getId())).thenReturn(Optional.empty());
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> snapShotService.restoreTodo(auth, todo.getId(), snapshot.getId()));
    // then
    assertEquals(e.getErrorCode(), SNAPSHOT_NOT_FOUND);
  }

  @Test
  @DisplayName("프로젝트가 존재하지 않으면 투두 롤백에 실패한다")
  void restore_todo_failure_project_not_found() {
    // given
    Todo anotherTodo = Todo.builder()
        .id(100L)
        .author(testUser)
        .build();

    when(userQueryService.findByEmail(auth.getName())).thenReturn(testUser);
    when(todoQueryService.findById(anotherTodo.getId())).thenReturn(anotherTodo);
    when(snapShotRepository.findById(snapshot.getId())).thenReturn(Optional.of(snapshot));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> snapShotService.restoreTodo(auth, anotherTodo.getId(), snapshot.getId()));
    // then
    assertEquals(e.getErrorCode(), PROJECT_NOT_FOUND);
  }

  @Test
  @DisplayName("협업자 권한이 부족하면 투두 롤백에 실패한다")
  void restore_todo_failure_forbidden() {
    // given
    when(userQueryService.findByEmail(auth.getName())).thenReturn(testUser);
    when(todoQueryService.findById(todo.getId())).thenReturn(todo);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(snapShotRepository.findById(snapshot.getId())).thenReturn(Optional.of(snapshot));
    when(collaboratorQueryService.existsByProjectAndCollaboratorAndRoleTypeAndIsConfirmed(
        project, testUser, EDITOR)).thenReturn(false);
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> snapShotService.restoreTodo(auth, todo.getId(), snapshot.getId()));
    // then
    assertEquals(e.getErrorCode(), FORBIDDEN);
  }
}