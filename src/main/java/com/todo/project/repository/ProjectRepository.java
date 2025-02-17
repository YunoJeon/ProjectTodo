package com.todo.project.repository;

import com.todo.project.entity.Project;
import com.todo.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  List<Project> findByNameContainingIgnoreCase(String keyword);

  @Query("SELECT DISTINCT p FROM Project p LEFT JOIN Collaborator c ON c.project = p WHERE p.owner = :user OR (c.collaborator = :user AND c.confirmType = 'TRUE')")
  Page<Project> findProjectByUser(User user, PageRequest of);
}