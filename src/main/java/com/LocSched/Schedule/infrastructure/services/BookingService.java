package com.LocSched.Schedule.infrastructure.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.LocSched.Schedule.infrastructure.entities.Booking;
import com.LocSched.Schedule.infrastructure.repository.BookingRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingService {
    
    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ResponseEntity<Booking> createSchedule(Booking booking) {
        if (repository.checkBooking(booking.getStartTime(), booking.getEndTime(), booking.getLocation())) {
            throw new RuntimeException("Booking already exists");
        }else{
            booking.setStatus(Booking.ScheduleStatus.SCHEDULED);
            return ResponseEntity.ok(repository.save(booking));
        }
    }

    public ResponseEntity<List<Booking>> getAllSchedules() {
        return ResponseEntity.ok(repository.findAll());
    }

    public ResponseEntity<Booking> findById(Long id) {
        return ResponseEntity.ok(repository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found")));
    }

    @Transactional
    public ResponseEntity<Booking> updateSchedule(Long id, Booking bookingDetails) {
        return repository.findById(id).map(existingBooking -> {
            existingBooking.setStartTime(bookingDetails.getStartTime());
            existingBooking.setEndTime(bookingDetails.getEndTime());
            existingBooking.setLocation(bookingDetails.getLocation());
            existingBooking.setStatus(bookingDetails.getStatus());
            existingBooking.setEmployee(bookingDetails.getEmployee());
            
            Booking updatedBooking = repository.save(existingBooking);
            return ResponseEntity.ok(updatedBooking);
        }).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Transactional
    public String deleteBooking(Long id) {
        repository.deleteById(id);
        return "Booking deleted successfully";
    }

}
