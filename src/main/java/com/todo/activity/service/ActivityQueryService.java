package com.todo.activity.service;

import com.todo.activity.entity.ActivityLog;
import com.todo.activity.repository.ActivityLogRepository;
import com.todo.project.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActivityQueryService {

  private final ActivityLogRepository activityLogRepository;

  public Page<ActivityLog> findByProject(Project project, Pageable pageable) {

    return activityLogRepository.findByProject(project, pageable);
  }
}