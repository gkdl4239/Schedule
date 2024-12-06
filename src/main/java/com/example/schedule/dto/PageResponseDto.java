package com.example.schedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class PageResponseDto<T> {
    private final List<T> data;
    private final int currentPage;
    private final int pageSize;
    private final int totalPages;
    private final long totalElements;

    public PageResponseDto(List<T> data, int currentPage, int pageSize, int totalPages, long totalElements) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
