package com.example.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PageResponseDto {
    private final List<ScheduleResponseDto> data;
    private final int currentPage;
    private final int pageSize;
    private final int totalPages;
    private final long totalElements;
}
