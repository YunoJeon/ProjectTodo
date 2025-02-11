package com.todo.search.controller;

import com.todo.search.dto.SearchResponseDto;
import com.todo.search.service.SearchService;
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
public class SearchController {

  private final SearchService searchService;

  @GetMapping
  public ResponseEntity<Page<SearchResponseDto>> search(
      @RequestParam(value = "q") String keyword,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    Page<SearchResponseDto> response = searchService.search(keyword, page, pageSize);
    return ResponseEntity.ok(response);
  }
}
