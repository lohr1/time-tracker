package de.rloh.timetracker;

import java.time.LocalDateTime;

/** Outgoing JSON for a time entry. Decouples the API from the JPA entity. */
public record TimeEntryResponse(
        Long id,
        String project,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description,
        long durationMinutes) {

    static TimeEntryResponse from(TimeEntry entry) {
        return new TimeEntryResponse(
                entry.getId(),
                entry.getProject(),
                entry.getStartTime(),
                entry.getEndTime(),
                entry.getDescription(),
                entry.duration().toMinutes());
    }
}
