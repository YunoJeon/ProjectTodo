package com.todo.todo.service;

import static com.todo.exception.ErrorCode.TODO_NOT_FOUND;

import com.todo.exception.CustomException;
import com.todo.todo.entity.Todo;
import com.todo.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoQueryService {

  private final TodoRepository todoRepository;

  public Todo findById(Long id) {
    return todoRepository.findById(id).orElseThrow(() -> new CustomException(TODO_NOT_FOUND));
  }
}