package com.LocSched.Schedule.DTO;

public record BookingDTO(
    Long id,
    String startTime,
    String endTime,
    String location,
    String status
) {}
