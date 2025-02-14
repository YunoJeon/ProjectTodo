package com.todo.collaborator.repository;

import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.type.ConfirmType;
import com.todo.collaborator.type.RoleType;
import com.todo.project.entity.Project;
import com.todo.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

  boolean existsByProjectAndCollaboratorAndConfirmType(Project project, User collaborator, ConfirmType confirmType);

  List<Collaborator> findByProject(Project project);

  @Query("SELECT DISTINCT c.project FROM Collaborator c WHERE c.collaborator = :user AND c.confirmType = :confirmType")
  List<Project> findProjectsByCollaborator(@Param("user") User user, @Param("confirmType") ConfirmType confirmType);

  boolean existsByProjectAndCollaboratorAndRoleTypeAndConfirmType(Project project, User collaborator,
      RoleType roleType, ConfirmType confirmType);

  Collaborator findByProjectAndCollaboratorAndConfirmType(Project project, User commentAuthor,
      ConfirmType confirmType);
}