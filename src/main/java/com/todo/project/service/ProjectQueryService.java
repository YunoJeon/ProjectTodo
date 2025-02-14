package com.todo.project.service;

import static com.todo.exception.ErrorCode.PROJECT_NOT_FOUND;

import com.todo.exception.CustomException;
import com.todo.project.entity.Project;
import com.todo.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectQueryService {

  private final ProjectRepository projectRepository;

  public Project findById(Long id) {

    return projectRepository.findById(id).orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));
  }
}