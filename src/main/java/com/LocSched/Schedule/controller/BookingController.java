package com.LocSched.Schedule.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LocSched.Schedule.infrastructure.entities.Booking;
import com.LocSched.Schedule.infrastructure.services.BookingService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/schedule")
public class BookingController {
    
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<Booking> createSchedule(@RequestBody Booking booking) {

        try {
            return service.createSchedule(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Booking>> getAllSchedules() {
        return service.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> findById(Long id) {
        return service.findById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Booking> updateSchedule(Long id, Booking bookingDetails) {
        return service.updateSchedule(id, bookingDetails);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSchedule(Long id) {
        return service.deleteBooking(id);
    }
}
