package com.todo.collaborator.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.todo.collaborator.type.ConfirmType;
import com.todo.collaborator.type.RoleType;
import com.todo.project.entity.Project;
import com.todo.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "collaborators")
public class Collaborator {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @JoinColumn(name = "collaborator_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private User collaborator;

  @JoinColumn(name = "project_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private Project project;

  @Enumerated(STRING)
  private RoleType roleType;

  private ConfirmType confirmType;

  public static Collaborator of(User invitedUser, Project project, RoleType roleType,
      ConfirmType confirmType) {

    return Collaborator.builder()
        .collaborator(invitedUser)
        .project(project)
        .roleType(roleType)
        .confirmType(confirmType)
        .build();
  }

  public void update(RoleType roleType) {

    this.roleType = roleType;
  }
}