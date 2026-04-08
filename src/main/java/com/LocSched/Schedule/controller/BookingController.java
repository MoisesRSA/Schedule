package com.LocSched.Schedule.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LocSched.Schedule.DTO.BookingDTO;
import com.LocSched.Schedule.infrastructure.entities.Booking;
import com.LocSched.Schedule.infrastructure.services.BookingService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.LocSched.Schedule.infrastructure.entities.Employee;

@CrossOrigin(origins = {"http://localhost:5173", "https://booking-front-pi.vercel.app"})
@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<BookingDTO> createSchedule(
            @RequestBody Booking booking,
            @AuthenticationPrincipal Employee currentEmployee) {
        try {
            booking.setEmployee(currentEmployee);
            return service.createSchedule(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingDTO>> getAllSchedules(
            @AuthenticationPrincipal Employee currentEmployee) {
        return service.getAllSchedules(currentEmployee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookingDTO> updateSchedule(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        return service.updateSchedule(id, bookingDetails);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        return service.deleteBooking(id);
    }
}
