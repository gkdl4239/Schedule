package com.example.schedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class PageResponseDto<T> {
    private List<T> data;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;

    public PageResponseDto(List<T> data, int currentPage, int pageSize, int totalPages, long totalElements) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
