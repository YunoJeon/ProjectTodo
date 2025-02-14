package com.todo.todo.controller;

import com.github.pagehelper.PageInfo;
import com.todo.todo.dto.TodoDto;
import com.todo.todo.dto.TodoFilterResponseDto;
import com.todo.todo.dto.TodoResponseDto;
import com.todo.todo.dto.TodoUpdateDto;
import com.todo.todo.service.TodoService;
import com.todo.todo.type.TodoCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Todo API", description = "투두 API")
public class TodoController {

  private final TodoService todoService;

  @PostMapping
  @Operation(summary = "투두 생성 API", description = "투두를 생성할 수 있습니다.")
  public ResponseEntity<Void> createTodo(Authentication auth,
      @RequestBody @Valid TodoDto todoDto) {

    todoService.createTodo(auth, todoDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  @Operation(summary = "투두 목록 조회 API", description = "투두 목록을 조회할 수 있습니다. parameter 값으로 동적 필터링 되어 조건에 맞는 목록을 조회할 수 있습니다. 페이징 처리 되어있습니다.")
  public ResponseEntity<PageInfo<TodoFilterResponseDto>> getTodos(Authentication auth,
      @RequestParam(value = "projectId", required = false) Long projectId,
      @RequestParam(value = "category", required = false) TodoCategory todoCategory,
      @RequestParam(value = "priority", required = false) Boolean isPriority,
      @RequestParam(value = "completed", required = false) Boolean isCompleted,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    return ResponseEntity.ok(
        todoService.getTodos(auth, projectId, todoCategory, isPriority, isCompleted,
            page, pageSize));
  }

  @GetMapping("/{todoId}")
  @Operation(summary = "투두 상세 조회 API", description = "특정 투두의 상세 정보를 조회할 수 있습니다.")
  public ResponseEntity<TodoResponseDto> getTodoDetail(Authentication auth,
      @PathVariable Long todoId) {

    return ResponseEntity.ok(todoService.getTodoDetail(auth, todoId));
  }

  @PutMapping("/{todoId}")
  @Operation(summary = "투두 수정 API", description = "특정 투두를 수정할 수 있습니다.")
  public ResponseEntity<TodoResponseDto> updateTodo(Authentication auth,
      @PathVariable Long todoId,
      @RequestBody @Valid TodoUpdateDto todoUpdateDto) {

    return ResponseEntity.ok(todoService.updateTodo(auth, todoId, todoUpdateDto));
  }
}