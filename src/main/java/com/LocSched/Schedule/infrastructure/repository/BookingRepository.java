package com.LocSched.Schedule.infrastructure.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LocSched.Schedule.infrastructure.entities.Booking;
import com.LocSched.Schedule.infrastructure.entities.Employee;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.startTime <= :endTime AND b.endTime >= :startTime AND b.location = :location")
    public boolean checkBooking(@Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime, @Param ("location") String location);

    List<Booking> findByEmployee(Employee employee);
}
