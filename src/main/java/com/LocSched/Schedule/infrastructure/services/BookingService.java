package com.LocSched.Schedule.infrastructure.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.LocSched.Schedule.DTO.BookingDTO;
import com.LocSched.Schedule.infrastructure.entities.Booking;
import com.LocSched.Schedule.infrastructure.entities.Employee;
import com.LocSched.Schedule.infrastructure.repository.BookingRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public BookingDTO toDTO(Booking booking) {
        Employee emp = booking.getEmployee();
        return new BookingDTO(
            booking.getId(),
            booking.getStartTime(),
            booking.getEndTime(),
            booking.getLocation(),
            booking.getStatus() != null ? booking.getStatus().name() : null,
            emp != null ? emp.getId() : null,
            emp != null ? emp.getName() : null
        );
    }

    @Transactional
    public ResponseEntity<BookingDTO> createSchedule(Booking booking) {
        if (repository.checkBooking(booking.getStartTime(), booking.getEndTime(), booking.getLocation())) {
            throw new RuntimeException("Booking already exists");
        } else {
            booking.setStatus(Booking.ScheduleStatus.SCHEDULED);
            return ResponseEntity.ok(toDTO(repository.save(booking)));
        }
    }

    public ResponseEntity<List<BookingDTO>> getAllSchedules(Employee employee) {
        return ResponseEntity.ok(
            repository.findByEmployee(employee)
                      .stream()
                      .map(this::toDTO)
                      .toList()
        );
    }

    public ResponseEntity<BookingDTO> findById(Long id) {
        return ResponseEntity.ok(
            toDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found")))
        );
    }

    @Transactional
    public ResponseEntity<BookingDTO> updateSchedule(Long id, Booking bookingDetails) {
        return repository.findById(id).map(existingBooking -> {
            existingBooking.setStartTime(bookingDetails.getStartTime());
            existingBooking.setEndTime(bookingDetails.getEndTime());
            existingBooking.setLocation(bookingDetails.getLocation());
            existingBooking.setStatus(bookingDetails.getStatus());
            existingBooking.setEmployee(bookingDetails.getEmployee());
            return ResponseEntity.ok(toDTO(repository.save(existingBooking)));
        }).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Transactional
    public String deleteBooking(Long id) {
        repository.deleteById(id);
        return "Booking deleted successfully";
    }

}
