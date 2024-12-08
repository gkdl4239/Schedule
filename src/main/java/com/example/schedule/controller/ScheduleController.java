package com.example.schedule.controller;

import com.example.schedule.dto.PageResponseDto;
import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }


    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(@Valid @RequestBody ScheduleRequestDto dto) {

        return new ResponseEntity<>(scheduleService.saveSchedule(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponseDto> findAllSchedule(
            @ModelAttribute ScheduleRequestDto dto
    ) {

        PageResponseDto result = scheduleService.findAllSchedule(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> findScheduleById(@PathVariable Long id) {
        return new ResponseEntity<>(scheduleService.findScheduleById(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> updateToDoAndName(
            @PathVariable Long id,
            @RequestBody ScheduleRequestDto dto
    ) {
        return new ResponseEntity<>(scheduleService.updateToDoAndName(id, dto.getName(), dto.getToDo(), dto.getPassword()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleRequestDto dto
    ) {
        scheduleService.deleteSchedule(id, dto.getPassword());
    }
}
