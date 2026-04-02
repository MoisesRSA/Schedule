package com.LocSched.Schedule.infrastructure.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.LocSched.Schedule.infrastructure.entities.Schedule;
import com.LocSched.Schedule.infrastructure.repository.ScheduleRepository;

import jakarta.transaction.Transactional;

@Service
public class ScheduleService {
    
    private final ScheduleRepository repository;

    public ScheduleService(ScheduleRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Schedule createSchedule(Schedule schedule) {
        if (repository.checkSchedule(schedule.getStartTime(), schedule.getEndTime(), schedule.getLocation())) {
            throw new RuntimeException("Schedule already exists");
        }else{
            schedule.setStatus(Schedule.ScheduleStatus.SCHEDULED);
            return repository.save(schedule);
        }
    }

    public ResponseEntity<List<Schedule>> getAllSchedules() {
        return ResponseEntity.ok(repository.findAll());
    }

    public ResponseEntity<Schedule> findById(Long id) {
        return ResponseEntity.ok(repository.findById(id).orElseThrow(() -> new RuntimeException("Schedule not found")));
    }

    @Transactional
    public ResponseEntity<Schedule> updateSchedule(Long id, Schedule scheduleDetails) {
        return repository.findById(id).map(existingSchedule -> {
            existingSchedule.setStartTime(scheduleDetails.getStartTime());
            existingSchedule.setEndTime(scheduleDetails.getEndTime());
            existingSchedule.setLocation(scheduleDetails.getLocation());
            existingSchedule.setStatus(scheduleDetails.getStatus());
            existingSchedule.setEmployee(scheduleDetails.getEmployee());
            
            Schedule updatedSchedule = repository.save(existingSchedule);
            return ResponseEntity.ok(updatedSchedule);
        }).orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    @Transactional
    public String deleteSchedule(Long id) {
        repository.deleteById(id);
        return "Schedule deleted successfully";
    }

}
