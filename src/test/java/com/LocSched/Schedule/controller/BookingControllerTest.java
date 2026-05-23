package com.LocSched.Schedule.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.LocSched.Schedule.DTO.BookingDTO;
import com.LocSched.Schedule.infrastructure.entities.Booking;
import com.LocSched.Schedule.infrastructure.entities.Employee;
import com.LocSched.Schedule.infrastructure.services.BookingService;
import com.LocSched.Schedule.infrastructure.services.SseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @Mock
    private SseService sseService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;
    private BookingDTO bookingDTO;
    private Booking booking;
    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setName("John Doe");
        mockEmployee.setEmail("john.doe@example.com");
        mockEmployee.setRole("USER");

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return mockEmployee;
                    }
                })
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        bookingDTO = new BookingDTO(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusHours(1),
            "Room A",
            "SCHEDULED",
            1L,
            "John Doe",
            "Sample description"
        );

        booking = new Booking();
        booking.setLocation("Room A");
        booking.setStartTime(bookingDTO.startTime());
        booking.setEndTime(bookingDTO.endTime());
        booking.setDescription("Sample description");
    }

    @Test
    void createSchedule_Success() throws Exception {
        when(bookingService.createSchedule(any(Booking.class))).thenReturn(ResponseEntity.ok(bookingDTO));

        mockMvc.perform(post("/booking/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Room A"));
    }

    @Test
    void createSchedule_BadRequest_WhenExceptionThrown() throws Exception {
        when(bookingService.createSchedule(any(Booking.class))).thenThrow(new RuntimeException("Booking already exists"));

        mockMvc.perform(post("/booking/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Booking already exists"));
    }

    @Test
    void getAllSchedules_Success() throws Exception {
        when(bookingService.getAllSchedules(any())).thenReturn(ResponseEntity.ok(List.of(bookingDTO)));

        mockMvc.perform(get("/booking/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].location").value("Room A"));
    }

    @Test
    void getTimelineSchedules_Success() throws Exception {
        when(bookingService.getAllForTimeline()).thenReturn(ResponseEntity.ok(List.of(bookingDTO)));

        mockMvc.perform(get("/booking/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findById_Success() throws Exception {
        when(bookingService.findById(eq(1L), any())).thenReturn(ResponseEntity.ok(bookingDTO));

        mockMvc.perform(get("/booking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateSchedule_Success() throws Exception {
        when(bookingService.updateSchedule(eq(1L), any(Booking.class), any())).thenReturn(ResponseEntity.ok(bookingDTO));

        mockMvc.perform(put("/booking/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Room A"));
    }

    @Test
    void deleteSchedule_Success() throws Exception {
        when(bookingService.deleteBooking(eq(1L), any())).thenReturn("Booking deleted successfully");

        mockMvc.perform(delete("/booking/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Booking deleted successfully"));
    }
}
