package com.todo.activity.controller;

import com.todo.activity.dto.ActivityLogResponseDto;
import com.todo.activity.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/project")
@Tag(name = "Activity Log API", description = "활동 로그 API")
public class ActivityLogController {

  private final ActivityLogService activityLogService;

  @GetMapping("/{project_id}/logs")
  @Operation(summary = "활동 로그 목록 조회 API", description = "프로젝트에 속한 투두의 생성 및 변경 사항을 확인할 수 있습니다. 페이징 처리 되어있습니다.")
  public ResponseEntity<Page<ActivityLogResponseDto>> getActivityLogs(Authentication auth,
      @PathVariable("project_id") Long projectId,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    Page<ActivityLogResponseDto> dto = activityLogService.getActivityLogs(auth, projectId, page, pageSize);

    return ResponseEntity.ok(dto);
  }
}