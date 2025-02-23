package com.todo.comment.dto;

import com.todo.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentResponseDto(

    Long commentId,
    Long commentAuthorId,
    String commentAuthorProfileImageUrl,
    String commentAuthorName,
    Long parentCommentId,
    String content,
    LocalDateTime createdAt
) {

  public static CommentResponseDto fromEntity(Comment comment) {

    return CommentResponseDto.builder()
        .commentId(comment.getId())
        .commentAuthorId(comment.getCommentAuthor().getId())
        .commentAuthorProfileImageUrl(comment.getCommentAuthor().getProfileImageUrl())
        .commentAuthorName(comment.getCommentAuthor().getName())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .build();
  }
}