package com.todo.comment.dto;

public record CommentUpdateDto(

    String content,
    boolean isDeleted
) {

}