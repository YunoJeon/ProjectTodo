package com.todo.search.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.todo.search.dto.SearchResponseDto;
import com.todo.todo.mapper.TodoMapper;
import com.todo.user.service.UserQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

  private final TodoMapper todoMapper;

  private final UserQueryService userQueryService;

  public PageInfo<SearchResponseDto> search(Authentication auth, String keyword,
      int page, int pageSize) {

    Long userId = userQueryService.findByEmail(auth.getName()).getId();

    PageHelper.startPage(page, pageSize);
    List<SearchResponseDto> results = todoMapper.searchTodosAndProjects(keyword, userId);

    return new PageInfo<>(results);
  }
}