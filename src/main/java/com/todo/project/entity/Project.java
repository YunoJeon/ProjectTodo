package com.todo.project.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.todo.project.dto.ProjectDto;
import com.todo.user.entity.User;
import jakarta.persistence.Column;
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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "projects")
public class Project {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @JoinColumn(name = "owner_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private User owner;

  @Column(nullable = false)
  private String name;

  private String description;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  public static Project of(User owner, ProjectDto projectDto) {

    return Project.builder()
        .owner(owner)
        .name(projectDto.name())
        .description(projectDto.description())
        .build();
  }

  public void update(ProjectDto projectDto) {
    name = projectDto.name();
    description = projectDto.description();
  }
}