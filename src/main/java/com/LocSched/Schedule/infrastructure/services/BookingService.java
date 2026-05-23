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
    private final SseService sseService;

    public BookingService(BookingRepository repository, SseService sseService) {
        this.repository = repository;
        this.sseService = sseService;
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
            emp != null ? emp.getName() : null,
            booking.getDescription()
        );
    }

    @Transactional
    public ResponseEntity<BookingDTO> createSchedule(Booking booking) {
        if (repository.checkBooking(booking.getStartTime(), booking.getEndTime(), booking.getLocation())) {
            throw new RuntimeException("Booking already exists");
        } else {
            booking.setStatus(Booking.ScheduleStatus.SCHEDULED);
            Booking savedBooking = repository.save(booking);
            sseService.notifyUpdate();
            return ResponseEntity.ok(toDTO(savedBooking));
        }
    }

    public ResponseEntity<List<BookingDTO>> getAllSchedules(Employee employee) {
        List<Booking> bookings;
        if ("ADMIN".equals(employee.getRole())) {
            bookings = repository.findAll();
        } else {
            bookings = repository.findByEmployee(employee);
        }
        return ResponseEntity.ok(
            bookings.stream()
                      .map(this::toDTO)
                      .toList()
        );
    }

    public ResponseEntity<List<BookingDTO>> getAllForTimeline() {
        return ResponseEntity.ok(
            repository.findAll().stream().map(this::toDTO).toList()
        );
    }

    public ResponseEntity<BookingDTO> findById(Long id, Employee currentEmployee) {
        Booking booking = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!"ADMIN".equals(currentEmployee.getRole()) && !booking.getEmployee().getId().equals(currentEmployee.getId())) {
            throw new RuntimeException("Not authorized to view this booking");
        }
        return ResponseEntity.ok(toDTO(booking));
    }

    @Transactional
    public ResponseEntity<BookingDTO> updateSchedule(Long id, Booking bookingDetails, Employee currentEmployee) {
        return repository.findById(id).map(existingBooking -> {
            if (!"ADMIN".equals(currentEmployee.getRole()) && !existingBooking.getEmployee().getId().equals(currentEmployee.getId())) {
                throw new RuntimeException("Not authorized to update this booking");
            }
            if (repository.checkBookingForUpdate(id, bookingDetails.getStartTime(), bookingDetails.getEndTime(), bookingDetails.getLocation())) {
                throw new RuntimeException("Booking already exists for this time and location");
            }
            existingBooking.setStartTime(bookingDetails.getStartTime());
            existingBooking.setEndTime(bookingDetails.getEndTime());
            existingBooking.setLocation(bookingDetails.getLocation());
            if (bookingDetails.getStatus() != null) {
                existingBooking.setStatus(bookingDetails.getStatus());
            }
            if (bookingDetails.getDescription() != null) {
                existingBooking.setDescription(bookingDetails.getDescription());
            }
            if ("ADMIN".equals(currentEmployee.getRole()) && bookingDetails.getEmployee() != null) {
                existingBooking.setEmployee(bookingDetails.getEmployee());
            }
            Booking savedBooking = repository.save(existingBooking);
            sseService.notifyUpdate();
            return ResponseEntity.ok(toDTO(savedBooking));
        }).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Transactional
    public String deleteBooking(Long id, Employee currentEmployee) {
        Booking booking = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!"ADMIN".equals(currentEmployee.getRole()) && !booking.getEmployee().getId().equals(currentEmployee.getId())) {
            throw new RuntimeException("Not authorized to delete this booking");
        }
        repository.delete(booking);
        sseService.notifyUpdate();
        return "Booking deleted successfully";
    }

}
