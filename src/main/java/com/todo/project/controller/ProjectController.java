package com.todo.project.controller;

import com.todo.project.dto.ProjectDto;
import com.todo.project.dto.ProjectPageResponseDto;
import com.todo.project.dto.ProjectResponseDto;
import com.todo.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Project API", description = "프로젝트 API")
public class ProjectController {

  private final ProjectService projectService;

  @PostMapping
  @Operation(summary = "프로젝트 생성 API", description = "개인 및 협업 투두는 프로젝트로 관리할 수 있습니다. 협업은 프로젝트 생성이 필수입니다.")
  public ResponseEntity<Void> createProject(Authentication auth,
      @RequestBody ProjectDto projectDto) {

    projectService.createProject(auth, projectDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  @Operation(summary = "프로젝트 목록 조회 API", description = "특정 회원의 프로젝트 목록을 조회할 수 있습니다. 페이징 처리 되어있습니다.")
  public ResponseEntity<Page<ProjectPageResponseDto>> getProjects(Authentication auth,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    return ResponseEntity.ok(projectService.getProjects(auth, page, pageSize));
  }

  @GetMapping("/{projectId}")
  @Operation(summary = "프로젝트 상세 조회 API", description = "특정 프로젝트 상세 정보를 조회할 수 있습니다.")
  public ResponseEntity<ProjectResponseDto> getProjectDetail(Authentication auth,
      @PathVariable Long projectId) {

    return ResponseEntity.ok(projectService.getProjectDetail(auth, projectId));
  }

  @PutMapping("/{projectId}")
  @Operation(summary = "프로젝트 수정 API", description = "프로젝트 내용을 수정할 수 있습니다.")
  public ResponseEntity<ProjectResponseDto> updateProject(Authentication auth,
      @PathVariable Long projectId, @RequestBody ProjectDto projectDto) {

    return ResponseEntity.ok(projectService.updateProject(auth, projectId, projectDto));
  }
}
