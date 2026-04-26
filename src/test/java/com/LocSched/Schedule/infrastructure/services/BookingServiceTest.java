package com.LocSched.Schedule.infrastructure.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.LocSched.Schedule.DTO.BookingDTO;
import com.LocSched.Schedule.infrastructure.entities.Booking;
import com.LocSched.Schedule.infrastructure.entities.Employee;
import com.LocSched.Schedule.infrastructure.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SseService sseService;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");

        booking = new Booking();
        booking.setId(1L);
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        booking.setLocation("Room A");
        booking.setEmployee(employee);
        booking.setStatus(Booking.ScheduleStatus.SCHEDULED);
    }

    @Test
    void createSchedule_Success() {
        when(bookingRepository.checkBooking(any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        ResponseEntity<BookingDTO> response = bookingService.createSchedule(booking);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Room A", response.getBody().location());
        assertEquals("SCHEDULED", response.getBody().status());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createSchedule_ThrowsException_WhenBookingExists() {
        when(bookingRepository.checkBooking(any(), any(), any())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createSchedule(booking);
        });

        assertEquals("Booking already exists", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getAllSchedules_ReturnsList() {
        when(bookingRepository.findByEmployee(any(Employee.class))).thenReturn(List.of(booking));

        ResponseEntity<List<BookingDTO>> response = bookingService.getAllSchedules(employee);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Room A", response.getBody().get(0).location());
    }

    @Test
    void getAllForTimeline_ReturnsList() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        ResponseEntity<List<BookingDTO>> response = bookingService.getAllForTimeline();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void findById_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ResponseEntity<BookingDTO> response = bookingService.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
    }

    @Test
    void findById_ThrowsException_WhenNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.findById(1L);
        });

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void updateSchedule_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking newDetails = new Booking();
        newDetails.setLocation("Room B");
        newDetails.setStatus(Booking.ScheduleStatus.IN_PROGRESS);

        ResponseEntity<BookingDTO> response = bookingService.updateSchedule(1L, newDetails);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void deleteBooking_Success() {
        doNothing().when(bookingRepository).deleteById(1L);

        String result = bookingService.deleteBooking(1L);

        assertEquals("Booking deleted successfully", result);
        verify(bookingRepository, times(1)).deleteById(1L);
    }
}
