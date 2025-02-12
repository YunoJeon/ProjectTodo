package com.todo.collaborator.repository;

import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.type.RoleType;
import com.todo.project.entity.Project;
import com.todo.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

  boolean existsByProjectAndCollaboratorAndConfirmed(Project project, User collaborator, boolean confirmed);

  List<Collaborator> findByProject(Project project);

  @Query("SELECT DISTINCT c.project FROM Collaborator c WHERE c.collaborator = :user AND c.isConfirmed = true")
  List<Project> findProjectsByCollaborator(@Param("user") User user);

  boolean existsByProjectAndCollaboratorAndRoleTypeAndConfirmed(Project project, User collaborator,
      RoleType roleType, boolean confirmed);
}