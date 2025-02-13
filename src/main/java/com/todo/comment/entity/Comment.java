package com.todo.comment.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.todo.comment.dto.CommentDto;
import com.todo.comment.dto.CommentUpdateDto;
import com.todo.todo.entity.Todo;
import com.todo.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
public class Comment {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @JoinColumn(name = "comment_author_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private User commentAuthor;

  @JoinColumn(name = "todo_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private Todo todo;

  private Long parentCommentId;

  private String content;

  @CreatedDate
  private LocalDateTime createdAt;

  private LocalDateTime deletedAt;

  public static Comment of(User commentAuthor, Todo todo, CommentDto commentDto) {

    return Comment.builder()
        .commentAuthor(commentAuthor)
        .todo(todo)
        .parentCommentId(commentDto.parentCommentId() == null ?
            null : commentDto.parentCommentId())
        .content(commentDto.content())
        .build();
  }

  public void update(CommentUpdateDto commentUpdateDto) {

    content = commentUpdateDto.content();
    deletedAt = commentUpdateDto.isDeleted() ? LocalDateTime.now() : null;
  }
}