package com.todo.snapshot.controller;

import com.todo.snapshot.service.SnapShotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "Snapshot API", description = "스냅샷 API")
public class SnapshotController {

  private final SnapShotService snapShotService;

  @PostMapping("/{todoId}/restore/{snapshotId}")
  @Operation(summary = "투두 복구 API", description = "프로젝트에 속한 특정 투두를 활동 로그에 저장된 시점(버전) 으로 롤백 할 수 있습니다.")
  public ResponseEntity<Void> restoreTodo(Authentication auth,
      @PathVariable Long todoId,
      @PathVariable Long snapshotId) {

    snapShotService.restoreTodo(auth, todoId, snapshotId);

    return ResponseEntity.ok().build();
  }
}