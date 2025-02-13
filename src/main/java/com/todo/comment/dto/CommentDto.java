package com.todo.comment.dto;

public record CommentDto(

    Long parentCommentId,
    String content
) {

}