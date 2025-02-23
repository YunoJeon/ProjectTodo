package com.todo.activity.dto;

import com.todo.activity.entity.ActivityLog;
import com.todo.activity.type.ActionType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ActivityLogResponseDto(

    Long activityLogId,
    String actionDetail,
    String changerName,
    ActionType actionType,
    Long todoId,
    String todoTitle,
    Long todoVersion,
    Long snapshotId,
    LocalDateTime createdAt
) {

  public static ActivityLogResponseDto fromEntity(ActivityLog activityLog) {

    Long todoId = activityLog.getTodo() != null ? activityLog.getTodo().getId() : null;
    String todoTitle = activityLog.getTodo() != null ? activityLog.getTodo().getTitle() : null;

    return ActivityLogResponseDto.builder()
        .activityLogId(activityLog.getId())
        .actionDetail(activityLog.getActionDetail())
        .changerName(activityLog.getChangerName())
        .todoId(todoId)
        .todoTitle(todoTitle)
        .actionType(activityLog.getActionType())
        .todoVersion(activityLog.getTodoVersion())
        .snapshotId(activityLog.getSnapshotId())
        .createdAt(activityLog.getCreatedAt())
        .build();
  }
}