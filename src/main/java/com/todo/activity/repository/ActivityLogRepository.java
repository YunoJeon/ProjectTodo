package com.todo.activity.repository;

import com.todo.activity.entity.ActivityLog;
import com.todo.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

  Page<ActivityLog> findByProject(Project project, Pageable pageable);
}