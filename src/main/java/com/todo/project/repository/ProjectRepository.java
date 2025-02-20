package com.todo.project.repository;

import com.todo.project.entity.Project;
import com.todo.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  @Query("SELECT DISTINCT p FROM Project p WHERE p.owner = :user OR p.id IN (SELECT c.project.id FROM Collaborator c WHERE c.collaborator = :user AND c.confirmType = 'TRUE')")
  Page<Project> findProjectByUser(User user, Pageable pageable);
}