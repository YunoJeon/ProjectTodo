package com.todo.search.controller;

import com.todo.search.dto.SearchResponseDto;
import com.todo.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search API", description = "검색 API")
public class SearchController {

  private final SearchService searchService;

  @GetMapping
  @Operation(summary = "이름 기반 검색 API", description = "투두 및 프로젝트 제목(이름) 을 기준으로 %{검색어}% 로 검색이 가능합니다.")
  public ResponseEntity<Page<SearchResponseDto>> search(
      @RequestParam(value = "q") String keyword,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    Page<SearchResponseDto> response = searchService.search(keyword, page, pageSize);
    return ResponseEntity.ok(response);
  }
}
