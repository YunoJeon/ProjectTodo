package com.todo.comment.controller;

import com.todo.comment.dto.CommentDto;
import com.todo.comment.dto.CommentResponseDto;
import com.todo.comment.dto.CommentUpdateDto;
import com.todo.comment.service.CommentService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/{todoId}/comments")
  public ResponseEntity<Void> addComment(Authentication auth,
      @PathVariable(value = "todoId") Long todoId,
      @RequestBody CommentDto commentDto) {

    commentService.addComment(auth, todoId, commentDto);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{todoId}/comments")
  public ResponseEntity<Page<CommentResponseDto>> getComments(Authentication auth,
      @PathVariable("todoId") Long todoId,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    Page<CommentResponseDto> comments = commentService.getComments(auth, todoId, page, pageSize);

    return ResponseEntity.ok(comments);
  }

  @PutMapping("/{todoId}/comments/{commentId}")
  public ResponseEntity<Void> updateComments(Authentication auth,
      @PathVariable(value = "todoId") Long todoId,
      @PathVariable(value = "commentId") Long commentId,
      @RequestBody CommentUpdateDto commentUpdateDto) {

    commentService.updateComments(auth, todoId, commentId, commentUpdateDto);

    return ResponseEntity.ok().build();
  }
}