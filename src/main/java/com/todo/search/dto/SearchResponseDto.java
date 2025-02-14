package com.todo.search.dto;

import com.todo.search.type.SearchType;

public record SearchResponseDto(

    Long id,
    String name,
    SearchType type,
    Boolean isCompleted
) {

}