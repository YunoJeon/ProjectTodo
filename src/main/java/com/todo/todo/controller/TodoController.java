package com.todo.todo.controller;

import com.github.pagehelper.PageInfo;
import com.todo.todo.dto.TodoDto;
import com.todo.todo.dto.TodoFilterResponseDto;
import com.todo.todo.dto.TodoResponseDto;
import com.todo.todo.dto.TodoUpdateDto;
import com.todo.todo.service.TodoService;
import com.todo.todo.type.TodoCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

  private final TodoService todoService;

  @PostMapping
  public ResponseEntity<Void> createTodo(Authentication auth,
      @RequestBody @Valid TodoDto todoDto) {

    todoService.createTodo(auth, todoDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<PageInfo<TodoFilterResponseDto>> getTodos(Authentication auth,
      @RequestParam(value = "category", required = false) TodoCategory todoCategory,
      @RequestParam(value = "priority", required = false) Boolean isPriority,
      @RequestParam(value = "completed", required = false) Boolean isCompleted,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    return ResponseEntity.ok(todoService.getTodos(auth, todoCategory, isPriority, isCompleted, page, pageSize));
  }

  @GetMapping("/{todoId}")
  public ResponseEntity<TodoResponseDto> getTodoDetail(Authentication auth, @PathVariable Long todoId) {

    return ResponseEntity.ok(todoService.getTodoDetail(auth, todoId));
  }

  @PutMapping("/{todoId}")
  public ResponseEntity<TodoResponseDto> updateTodo(Authentication auth,
      @PathVariable Long todoId,
  @RequestBody @Valid TodoUpdateDto todoUpdateDto) {

    return ResponseEntity.ok(todoService.updateTodo(auth, todoId, todoUpdateDto));
  }
}