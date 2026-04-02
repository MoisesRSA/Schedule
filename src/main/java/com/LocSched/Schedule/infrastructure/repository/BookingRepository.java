package com.LocSched.Schedule.infrastructure.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LocSched.Schedule.infrastructure.entities.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE s.startTime <= :endTime AND s.endTime >= :startTime AND s.location = :location")
    public boolean checkBooking(@Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime, @Param ("location") String location);
}
