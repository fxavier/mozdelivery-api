package com.xavier.mozdeliveryapi.dispatch.domain.valueobject;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Value object representing a courier's availability schedule.
 */
public record AvailabilitySchedule(
    Map<DayOfWeek, TimeSlot> weeklySchedule,
    Set<DayOfWeek> availableDays
) {
    
    public AvailabilitySchedule {
        Objects.requireNonNull(weeklySchedule, "Weekly schedule cannot be null");
        Objects.requireNonNull(availableDays, "Available days cannot be null");
        
        // Validate that available days match the schedule
        for (DayOfWeek day : availableDays) {
            if (!weeklySchedule.containsKey(day)) {
                throw new IllegalArgumentException("Available day " + day + " not found in weekly schedule");
            }
        }
    }
    
    /**
     * Check if courier is available on a specific day.
     */
    public boolean isAvailableOn(DayOfWeek day) {
        return availableDays.contains(day);
    }
    
    /**
     * Get time slot for a specific day.
     */
    public TimeSlot getTimeSlotFor(DayOfWeek day) {
        return weeklySchedule.get(day);
    }
    
    /**
     * Check if courier is available at a specific day and time.
     */
    public boolean isAvailableAt(DayOfWeek day, LocalTime time) {
        if (!isAvailableOn(day)) {
            return false;
        }
        
        TimeSlot timeSlot = getTimeSlotFor(day);
        return timeSlot != null && timeSlot.contains(time);
    }
    
    /**
     * Get total weekly hours.
     */
    public double getTotalWeeklyHours() {
        return weeklySchedule.values().stream()
            .mapToDouble(TimeSlot::getDurationHours)
            .sum();
    }
    
    /**
     * Time slot representing start and end times.
     */
    public record TimeSlot(LocalTime startTime, LocalTime endTime) {
        
        public TimeSlot {
            Objects.requireNonNull(startTime, "Start time cannot be null");
            Objects.requireNonNull(endTime, "End time cannot be null");
            
            if (!endTime.isAfter(startTime)) {
                throw new IllegalArgumentException("End time must be after start time");
            }
        }
        
        /**
         * Check if a time falls within this slot.
         */
        public boolean contains(LocalTime time) {
            return !time.isBefore(startTime) && !time.isAfter(endTime);
        }
        
        /**
         * Get duration in hours.
         */
        public double getDurationHours() {
            return (endTime.toSecondOfDay() - startTime.toSecondOfDay()) / 3600.0;
        }
    }
}