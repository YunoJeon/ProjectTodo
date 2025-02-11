package com.todo.project.repository;

import com.todo.project.entity.Project;
import com.todo.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  Page<Project> findByOwner(User owner, Pageable pageable);

  List<Project> findByNameContainingIgnoreCase(String keyword);
}