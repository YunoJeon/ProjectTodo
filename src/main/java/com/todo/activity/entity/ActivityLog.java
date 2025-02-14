package com.todo.activity.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.todo.activity.type.ActionType;
import com.todo.project.entity.Project;
import com.todo.todo.entity.Todo;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "activity_logs")
@EntityListeners(AuditingEntityListener.class)
public class ActivityLog {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "todo_id")
  private Todo todo;

  @Enumerated(STRING)
  private ActionType actionType;

  private String actionDetail;

  private String changerName;

  private Long todoVersion;

  private Long snapshotId;

  @CreatedDate
  private LocalDateTime createdAt;

  public static ActivityLog of(Project project, Todo todo, ActionType actionType,
      String actionDetail, String changerName, Long todoVersion, Long snapshotId) {

    return ActivityLog.builder()
        .project(project)
        .todo(todo)
        .actionType(actionType)
        .actionDetail(actionDetail)
        .changerName(changerName)
        .todoVersion(todoVersion)
        .snapshotId(snapshotId)
        .build();
  }
}