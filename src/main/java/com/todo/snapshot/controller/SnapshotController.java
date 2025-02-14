package com.todo.snapshot.controller;

import com.todo.snapshot.service.SnapShotService;
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
public class SnapshotController {

  private final SnapShotService snapShotService;

  @PostMapping("/{todoId}/restore/{snapshotId}")
  public ResponseEntity<Void> restoreTodo(Authentication auth,
      @PathVariable Long todoId,
      @PathVariable Long snapshotId) {

    snapShotService.restoreTodo(auth, todoId, snapshotId);

    return ResponseEntity.ok().build();
  }
}