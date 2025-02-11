package com.todo.project.controller;

import com.todo.project.dto.ProjectDto;
import com.todo.project.dto.ProjectPageResponseDto;
import com.todo.project.dto.ProjectResponseDto;
import com.todo.project.service.ProjectService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

  private final ProjectService projectService;

  @PostMapping
  public ResponseEntity<Void> createProject(Authentication auth,
      @RequestBody ProjectDto projectDto) {

    projectService.createProject(auth, projectDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<Page<ProjectPageResponseDto>> getProjects(Authentication auth,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    return ResponseEntity.ok(projectService.getProjects(auth, page, pageSize));
  }

  @GetMapping("/{projectId}")
  public ResponseEntity<ProjectResponseDto> getProjectDetail(Authentication auth,
      @PathVariable Long projectId) {

    return ResponseEntity.ok(projectService.getProjectDetail(auth, projectId));
  }

  @PutMapping("/{projectId}")
  public ResponseEntity<ProjectResponseDto> updateProject(Authentication auth,
      @PathVariable Long projectId, @RequestBody ProjectDto projectDto) {

    return ResponseEntity.ok(projectService.updateProject(auth, projectId, projectDto));
  }
}
