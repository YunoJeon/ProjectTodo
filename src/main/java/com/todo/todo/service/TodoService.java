package com.todo.todo.service;

import static com.todo.exception.ErrorCode.FORBIDDEN;
import static com.todo.exception.ErrorCode.VERSION_CONFLICT;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.todo.exception.CustomException;
import com.todo.todo.dto.TodoDto;
import com.todo.todo.dto.TodoFilterRequestDto;
import com.todo.todo.dto.TodoFilterResponseDto;
import com.todo.todo.dto.TodoResponseDto;
import com.todo.todo.dto.TodoUpdateDto;
import com.todo.todo.entity.Todo;
import com.todo.todo.mapper.TodoMapper;
import com.todo.todo.repository.TodoRepository;
import com.todo.todo.type.TodoCategory;
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

  private final TodoMapper todoMapper;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public void createTodo(Authentication auth, TodoDto todoDto) {

    todoRepository.save(Todo.of(userQueryService.findByEmail(auth.getName()), todoDto));
  }

  public PageInfo<TodoFilterResponseDto> getTodos(Authentication auth,
      TodoCategory todoCategory, Boolean isPriority, Boolean isCompleted,
      int page, int pageSize) {

    Long authorId = userQueryService.findByEmail(auth.getName()).getId();

    TodoFilterRequestDto todoFilterRequestDto = new TodoFilterRequestDto(
        authorId,
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

    Long authorId = userQueryService.findByEmail(auth.getName()).getId();

    Todo todo = todoQueryService.findById(todoId);

    if (!todo.getAuthor().getId().equals(authorId)) {
      throw new CustomException(FORBIDDEN);
    }

    return TodoResponseDto.fromEntity(todo);
  }

  @Transactional
  public TodoResponseDto updateTodo(Authentication auth, Long todoId,
      TodoUpdateDto todoUpdateDto) {

    Long authorId = userQueryService.findByEmail(auth.getName()).getId();

    Todo todo = todoQueryService.findById(todoId);

    if (!todo.getAuthor().getId().equals(authorId)) {
      throw new CustomException(FORBIDDEN);
    }

    todo.update(todoUpdateDto);

    try {
      entityManager.flush();
      return TodoResponseDto.fromEntity(todo);
    } catch (OptimisticLockException e) {
      throw new CustomException(VERSION_CONFLICT);
    }
  }
}