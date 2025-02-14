package com.todo.todo.service;

import static com.todo.exception.ErrorCode.FORBIDDEN;
import static com.todo.todo.type.TodoCategory.INDIVIDUAL;
import static com.todo.todo.type.TodoCategory.WORK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.pagehelper.PageInfo;
import com.todo.activity.service.ActivityLogService;
import com.todo.collaborator.service.CollaboratorQueryService;
import com.todo.exception.CustomException;
import com.todo.notification.service.NotificationService;
import com.todo.project.entity.Project;
import com.todo.project.service.ProjectQueryService;
import com.todo.snapshot.service.SnapShotService;
import com.todo.todo.dto.TodoDto;
import com.todo.todo.dto.TodoFilterRequestDto;
import com.todo.todo.dto.TodoFilterResponseDto;
import com.todo.todo.dto.TodoResponseDto;
import com.todo.todo.dto.TodoUpdateDto;
import com.todo.todo.entity.Todo;
import com.todo.todo.mapper.TodoMapper;
import com.todo.todo.repository.TodoRepository;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

  @Mock
  private TodoRepository todoRepository;

  @Mock
  private UserQueryService userQueryService;

  @Mock
  private TodoQueryService todoQueryService;

  @Mock
  private TodoMapper todoMapper;

  @Mock
  private ProjectQueryService projectQueryService;

  @Mock
  private EntityManager entityManager;

  @Mock
  private NotificationService notificationService;

  @Mock
  private SnapShotService snapShotService;

  @Mock
  private CollaboratorQueryService collaboratorQueryService;

  @Mock
  private ActivityLogService activityLogService;

  @InjectMocks
  private TodoService todoService;

  private Authentication auth;
  private User testUser;
  private Todo todo;
  private Project project;

  @BeforeEach
  void setUp() {
    auth = new UsernamePasswordAuthenticationToken("test@mail.com", null);

    testUser = User.builder()
        .id(1L)
        .email("test@mail.com")
        .build();

    todo = Todo.builder()
        .id(1L)
        .author(testUser)
        .projectId(null)
        .title("제목")
        .description("설명")
        .todoCategory(INDIVIDUAL)
        .isCompleted(false)
        .isPriority(false)
        .version(1L)
        .dueDate(LocalDateTime.of(2025, 2, 11, 11, 0))
        .createdAt(LocalDateTime.now())
        .build();

    project = Project.builder()
        .id(2L)
        .owner(testUser)
        .name("프로젝트")
        .build();

    ReflectionTestUtils.setField(todoService, "entityManager", entityManager);
  }

  @Test
  @DisplayName("할일 생성이 성공한다")
  void create_todo_success() {
    // given
    TodoDto todoDto = new TodoDto(project.getId(), "할일", "설명", WORK, true, LocalDateTime.now());
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(todoRepository.save(any(Todo.class))).thenAnswer(
        invocationOnMock -> invocationOnMock.getArgument(0));
    // when
    todoService.createTodo(auth, todoDto);
    // then
    verify(todoRepository).save(any(Todo.class));
  }

  @Test
  @DisplayName("할일 목록 조회가 성공한다")
  void get_todo_success() {
    // given
    List<Todo> todoList = Collections.singletonList(todo);
    when(todoMapper.filterTodos(any(TodoFilterRequestDto.class))).thenReturn(todoList);
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    // when
    PageInfo<TodoFilterResponseDto> pageInfo = todoService.getTodos(auth, null, INDIVIDUAL, false,
        false,
        1, 10);
    // then
    assertEquals(todo.getId(), pageInfo.getList().get(0).id());
    assertEquals(todo.getTitle(), pageInfo.getList().get(0).title());
  }

  @Test
  @DisplayName("할일 상세 조회가 성공한다")
  void get_todo_detail_success() {
    // given
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(todoQueryService.findById(todo.getId())).thenReturn(todo);
    // when
    TodoResponseDto responseDto = todoService.getTodoDetail(auth, todo.getId());
    // then
    assertEquals(todo.getId(), responseDto.id());
    assertEquals(testUser.getId(), responseDto.authorId());
  }

  @Test
  @DisplayName("작성자 또는 협업자가 아니면 할일 상세 조회가 실패한다")
  void get_todo_detail_failure_forbidden() {
    // given
    User otherUser = User.builder().id(2L).email("other@email.com").build();
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(todoQueryService.findById(todo.getId())).thenReturn(
        Todo.builder().id(1L).author(otherUser).build());
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> todoService.getTodoDetail(auth, todo.getId()));
    // then
    assertEquals(FORBIDDEN, e.getErrorCode());
  }

  @Test
  @DisplayName("할일 수정이 성공한다")
  void update_todo_success() {
    // given
    TodoUpdateDto updateDto = new TodoUpdateDto(
        null,
        "수정",
        "설명",
        WORK,
        false,
        true,
        LocalDateTime.of(2025, 2, 17, 0, 0));
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(todoQueryService.findById(todo.getId())).thenReturn(todo);
    // when
    TodoResponseDto responseDto = todoService.updateTodo(auth, todo.getId(), updateDto);
    // then
    assertEquals("수정", responseDto.title());
    assertEquals(LocalDateTime.of(2025, 2, 17, 0, 0), responseDto.dueDate());
  }
}