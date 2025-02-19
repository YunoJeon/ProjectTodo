package com.todo.todo.mapper;

import com.todo.search.dto.SearchResponseDto;
import com.todo.todo.dto.TodoFilterRequestDto;
import com.todo.todo.entity.Todo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TodoMapper {

  List<Todo> filterTodos(TodoFilterRequestDto todoFilterRequestDto);

  List<SearchResponseDto> searchTodosAndProjects(@Param("keyword") String keyword, @Param("userId") Long userId);
}