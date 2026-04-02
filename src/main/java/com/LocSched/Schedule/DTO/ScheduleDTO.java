package com.LocSched.Schedule.DTO;

public record ScheduleDTO(
    Long id,
    String startTime,
    String endTime,
    String location,
    String status
) {}
