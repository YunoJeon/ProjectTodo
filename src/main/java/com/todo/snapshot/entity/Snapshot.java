package com.todo.snapshot.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.todo.todo.entity.Todo;
import com.todo.todo.type.TodoCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = "snapshots")
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Snapshot {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "todo_id", nullable = false)
  private Todo todo;

  private String title;

  private String description;

  @Enumerated(STRING)
  private TodoCategory todoCategory;

  private boolean isCompleted;

  private boolean isPriority;

  private Long version;

  private LocalDateTime dueDate;

  public static Snapshot of(Todo todo) {

    return Snapshot.builder()
        .todo(todo)
        .title(todo.getTitle())
        .description(todo.getDescription())
        .todoCategory(todo.getTodoCategory())
        .isCompleted(todo.isCompleted())
        .isPriority(todo.isPriority())
        .version(todo.getVersion())
        .dueDate(todo.getDueDate())
        .build();
  }
}