package com.todo.search.service;

import static com.todo.search.type.SearchType.PROJECT;
import static com.todo.search.type.SearchType.TODO;

import com.todo.project.entity.Project;
import com.todo.project.repository.ProjectRepository;
import com.todo.search.dto.SearchResponseDto;
import com.todo.todo.entity.Todo;
import com.todo.todo.mapper.TodoMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

  private final TodoMapper todoMapper;

  private final ProjectRepository projectRepository;

  public Page<SearchResponseDto> search(String keyword, int page, int pageSize) {

    List<SearchResponseDto> todoResults = searchTodos(keyword);

    List<SearchResponseDto> projectResults = searchProjects(keyword);

    List<SearchResponseDto> combinedResults = new ArrayList<>();
    combinedResults.addAll(todoResults);
    combinedResults.addAll(projectResults);

    return paginateResults(combinedResults, page, pageSize);
  }

  private List<SearchResponseDto> searchTodos(String keyword) {

    List<Todo> todos = todoMapper.searchTodosByTitle(keyword);
    return todos.stream().map(
            todo -> new SearchResponseDto(
                todo.getId(),
                todo.getTitle(),
                TODO,
                todo.isCompleted()))
        .toList();
  }

  private List<SearchResponseDto> searchProjects(String keyword) {

    List<Project> projects = projectRepository.findByNameContainingIgnoreCase(keyword);
    return projects.stream().map(
            project -> new SearchResponseDto(
                project.getId(),
                project.getName(),
                PROJECT,
                null))
        .toList();
  }

  private Page<SearchResponseDto> paginateResults(List<SearchResponseDto> combinedResults, int page,
      int pageSize) {

    int total = combinedResults.size();
    int fromIndex = (page - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, total);

    List<SearchResponseDto> pageContent = fromIndex < total ?
        combinedResults.subList(fromIndex, toIndex)
        : Collections.emptyList();

    return new PageImpl<>(pageContent, PageRequest.of(page - 1, pageSize), total);
  }
}