package com.LocSched.Schedule.infrastructure.services;

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
            schedule.setStatus("Scheduled!");
            return repository.save(schedule);
        }
    }


}
