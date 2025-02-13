package com.todo.todo.service;

import static com.todo.activity.type.ActionType.TODO;
import static com.todo.collaborator.type.RoleType.EDITOR;
import static com.todo.exception.ErrorCode.FORBIDDEN;
import static com.todo.exception.ErrorCode.VERSION_CONFLICT;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.todo.activity.service.ActivityLogService;
import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.service.CollaboratorQueryService;
import com.todo.exception.CustomException;
import com.todo.notification.service.NotificationService;
import com.todo.project.entity.Project;
import com.todo.project.service.ProjectQueryService;
import com.todo.todo.dto.TodoDto;
import com.todo.todo.dto.TodoFilterRequestDto;
import com.todo.todo.dto.TodoFilterResponseDto;
import com.todo.todo.dto.TodoResponseDto;
import com.todo.todo.dto.TodoUpdateDto;
import com.todo.todo.entity.Todo;
import com.todo.todo.mapper.TodoMapper;
import com.todo.todo.repository.TodoRepository;
import com.todo.todo.type.TodoCategory;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

  private final TodoRepository todoRepository;

  private final UserQueryService userQueryService;

  private final TodoQueryService todoQueryService;

  private final ProjectQueryService projectQueryService;

  private final NotificationService notificationService;

  private final ActivityLogService activityLogService;

  private final CollaboratorQueryService collaboratorQueryService;

  private final TodoMapper todoMapper;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public void createTodo(Authentication auth, TodoDto todoDto) {

    User user = userQueryService.findByEmail(auth.getName());

    if (todoDto.projectId() != null) {

      Project project = projectQueryService.findById(todoDto.projectId());

      validEditorCollaborator(project, user);

      Todo todo = todoRepository.save(Todo.of(user, todoDto));

      List<Collaborator> collaborators = collaboratorQueryService.findByProject(project);

      List<User> users = collaborators.stream().map(Collaborator::getCollaborator)
          .filter(author -> !author.getId().equals(user.getId())).toList();

      notificationService.notifyTodoAddedByOthers(users, todo, project);

      activityLogService.recordTodoCreation(project, todo, TODO, user.getName());

    } else {

      todoRepository.save(Todo.of(user, todoDto));
    }
  }

  public PageInfo<TodoFilterResponseDto> getTodos(Authentication auth,
      Long projectId, TodoCategory todoCategory, Boolean isPriority, Boolean isCompleted,
      int page, int pageSize) {

    User user = userQueryService.findByEmail(auth.getName());

    Long filterAuthorId;

    if (projectId == null) {

      filterAuthorId = user.getId();
    } else {
      Project project = projectQueryService.findById(projectId);

      validCollaborator(project, user);

      filterAuthorId = null;
    }

    TodoFilterRequestDto todoFilterRequestDto = new TodoFilterRequestDto(
        filterAuthorId,
        projectId,
        todoCategory,
        isPriority,
        isCompleted);

    PageHelper.startPage(page, pageSize);
    List<Todo> todos = todoMapper.filterTodos(todoFilterRequestDto);
    PageInfo<Todo> todoPageInfo = new PageInfo<>(todos);

    List<TodoFilterResponseDto> dtoList = todos.stream()
        .map(TodoFilterResponseDto::fromEntity).toList();

    PageInfo<TodoFilterResponseDto> result = new PageInfo<>();
    result.setList(dtoList);
    result.setPageNum(todoPageInfo.getPageNum());
    result.setPageSize(todoPageInfo.getPageSize());
    result.setTotal(todoPageInfo.getTotal());
    result.setPages(todoPageInfo.getPages());

    return result;
  }

  public TodoResponseDto getTodoDetail(Authentication auth, Long todoId) {

    User user = userQueryService.findByEmail(auth.getName());

    Todo todo = todoQueryService.findById(todoId);

    if (todo.getProjectId() != null) {

      Project project = projectQueryService.findById(todo.getProjectId());
      validCollaborator(project, user);
    } else {

      if (!todo.getAuthor().getId().equals(user.getId())) {

        throw new CustomException(FORBIDDEN);
      }
    }

    return TodoResponseDto.fromEntity(todo);
  }

  @Transactional
  public TodoResponseDto updateTodo(Authentication auth, Long todoId,
      TodoUpdateDto todoUpdateDto) {

    User user = userQueryService.findByEmail(auth.getName());

    Todo todo = todoQueryService.findById(todoId);

    Project project = null;

    if (!todo.getAuthor().getId().equals(user.getId())) {

      project = projectQueryService.findById(todo.getProjectId());
      validEditorCollaborator(project, user);
    }

    todo.update(todoUpdateDto);

    if (project != null) {

      List<Collaborator> collaborators = collaboratorQueryService.findByProject(project);

      List<User> users = collaborators.stream().map(Collaborator::getCollaborator)
          .filter(author -> !author.getId().equals(user.getId())).toList();

      notificationService.notifyTodoStatusChangedByOthers(users, todo, project);

      if (!todo.isCompleted()) {
        activityLogService.recordTodoUpdate(project, todo, TODO, user.getName());
      } else {
        activityLogService.recordTodoComplete(project, todo, TODO, user.getName());
      }
    }

    try {
      entityManager.flush();
      return TodoResponseDto.fromEntity(todo);
    } catch (OptimisticLockException e) {
      throw new CustomException(VERSION_CONFLICT);
    }
  }

  private void validCollaborator(Project project, User user) {

    if (project.getOwner().getId().equals(user.getId())) {
      return;
    }

    boolean isConfirmed = collaboratorQueryService.existsByProjectAndCollaboratorAndIsConfirmed(
        project, user);

    if (!isConfirmed) {
      throw new CustomException(FORBIDDEN);
    }
  }

  private void validEditorCollaborator(Project project, User user) {

    if (project.getOwner().getId().equals(user.getId())) {
      return;
    }

    boolean isEditor = collaboratorQueryService.existsByProjectAndCollaboratorAndRoleTypeAndIsConfirmed(
        project, user, EDITOR);

    if (!isEditor) {
      throw new CustomException(FORBIDDEN);
    }
  }
}