package com.todo.comment.repository;

import com.todo.comment.entity.Comment;
import com.todo.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  Page<Comment> findByTodo(Todo todo, Pageable pageable);
}