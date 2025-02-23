package com.todo.comment.controller;

import com.todo.comment.dto.CommentDto;
import com.todo.comment.dto.CommentResponseDto;
import com.todo.comment.dto.CommentUpdateDto;
import com.todo.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Tag(name = "Comment API", description = "댓글 API")
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/{todoId}/comments")
  @Operation(summary = "댓글 추가 API", description = "특정 투두에 댓글을 추가할 수 있습니다. 대댓글의 경우 parentCommentId 필드에 상위 댓글 아이디가 포함되어야 합니다.")
  public ResponseEntity<Void> addComment(Authentication auth,
      @PathVariable(value = "todoId") Long todoId,
      @RequestBody CommentDto commentDto) {

    commentService.addComment(auth, todoId, commentDto);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{todoId}/comments")
  @Operation(summary = "댓글 조회 API", description = "특정 투두에 있는 댓글을 조회할 수 있습니다. 페이징 처리 되어있습니다.")
  public ResponseEntity<Page<CommentResponseDto>> getComments(Authentication auth,
      @PathVariable("todoId") Long todoId,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    Page<CommentResponseDto> comments = commentService.getComments(auth, todoId, page, pageSize);

    return ResponseEntity.ok(comments);
  }

  @PutMapping("/{todoId}/comments/{commentId}")
  @Operation(summary = "댓글 수정 API", description = "특정 투두에 있는 댓글 내용을 수정할 수 있습니다.")
  public ResponseEntity<Void> updateComments(Authentication auth,
      @PathVariable(value = "todoId") Long todoId,
      @PathVariable(value = "commentId") Long commentId,
      @RequestBody CommentUpdateDto commentUpdateDto) {

    commentService.updateComments(auth, todoId, commentId, commentUpdateDto);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{todoId}/comments/{commentId}")
  @Operation(summary = "댓글 삭제 API", description = "특정 투두에 있는 댓글을 삭제할 수 있습니다.")
  public ResponseEntity<Void> deleteComments(Authentication auth,
      @PathVariable(value = "todoId") Long todoId,
      @PathVariable(value = "commentId") Long commentId) {

    commentService.deleteComments(auth, todoId, commentId);

    return ResponseEntity.ok().build();
  }
}