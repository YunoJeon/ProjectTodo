package com.todo.todo.mapper;

import com.todo.todo.dto.TodoFilterRequestDto;
import com.todo.todo.entity.Todo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoMapper {

  List<Todo> filterTodos(TodoFilterRequestDto todoFilterRequestDto);
}