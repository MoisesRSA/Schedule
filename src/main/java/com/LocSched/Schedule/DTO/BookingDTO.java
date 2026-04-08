package com.LocSched.Schedule.DTO;

import java.time.LocalDateTime;

public record BookingDTO(
    Long id,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String location,
    String status,
    Long employeeId,
    String employeeName
) {}
