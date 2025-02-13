package com.todo.activity.controller;

import com.todo.activity.dto.ActivityLogResponseDto;
import com.todo.activity.service.ActivityLogService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ActivityLogController {

  private final ActivityLogService activityLogService;

  @GetMapping("/{project_id}/logs")
  public ResponseEntity<Page<ActivityLogResponseDto>> getActivityLogs(Authentication auth,
      @PathVariable("project_id") Long projectId,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    Page<ActivityLogResponseDto> dto = activityLogService.getActivityLogs(auth, projectId, page, pageSize);

    return ResponseEntity.ok(dto);
  }
}