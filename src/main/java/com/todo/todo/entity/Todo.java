package com.todo.todo.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.todo.snapshot.entity.Snapshot;
import com.todo.todo.dto.TodoDto;
import com.todo.todo.dto.TodoUpdateDto;
import com.todo.todo.type.TodoCategory;
import com.todo.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "todos")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Todo {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @JoinColumn(name = "author_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private User author;

  private Long projectId;

  @Column(nullable = false)
  private String title;

  private String description;

  @Enumerated(STRING)
  private TodoCategory todoCategory;

  private boolean isCompleted;

  private boolean isPriority;

  @Version
  private Long version;

  private LocalDateTime dueDate;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  public static Todo of(User author, TodoDto todoDto) {

    return Todo.builder()
        .author(author)
        .projectId(todoDto.projectId())
        .title(todoDto.title())
        .description(todoDto.description())
        .todoCategory(todoDto.todoCategory())
        .isCompleted(false)
        .isPriority(todoDto.isPriority())
        .version(1L)
        .dueDate(todoDto.dueDate())
        .build();
  }

  public void restoreTodo(Snapshot snapshot) {

    title = snapshot.getTitle();
    description = snapshot.getDescription();
    todoCategory = snapshot.getTodoCategory();
    isCompleted = snapshot.isCompleted();
    isPriority = snapshot.isPriority();
    dueDate = snapshot.getDueDate();
  }

  public void update(TodoUpdateDto todoUpdateDto) {
    projectId = todoUpdateDto.projectId();
    title = todoUpdateDto.title();
    description = todoUpdateDto.description();
    todoCategory = todoUpdateDto.todoCategory();
    isPriority = todoUpdateDto.isPriority();
    isCompleted = todoUpdateDto.isCompleted();
    dueDate = todoUpdateDto.dueDate();
  }
}