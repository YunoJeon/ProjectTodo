package com.todo.comment.service;

import com.todo.comment.entity.Comment;
import com.todo.comment.repository.CommentRepository;
import com.todo.todo.entity.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentQueryService {

  private final CommentRepository commentRepository;

  public Page<Comment> findByTodo(Todo todo, Pageable pageable) {
    return commentRepository.findByTodo(todo, pageable);
  }
}