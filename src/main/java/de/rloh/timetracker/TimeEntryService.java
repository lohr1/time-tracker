package de.rloh.timetracker;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Business rules for time entries. Knows nothing about HTTP. */
@ApplicationScoped
public class TimeEntryService {

    private final TimeEntryRepository repository;

    public TimeEntryService(TimeEntryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TimeEntry create(String project, LocalDateTime start, LocalDateTime end, String description) {
        if (!end.isAfter(start)) {
            throw new InvalidTimeEntryException("endTime must be after startTime");
        }
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new InvalidTimeEntryException("startTime and endTime must be on the same day");
        }
        boolean overlaps = repository.findByDay(start.toLocalDate()).stream()
                .anyMatch(existing -> existing.overlaps(start, end));
        if (overlaps) {
            throw new InvalidTimeEntryException("time entry overlaps with an existing entry");
        }

        TimeEntry entry = new TimeEntry(project, start, end, description);
        repository.persist(entry);
        return entry;
    }

    public List<TimeEntry> entriesForDay(LocalDate day) {
        return repository.findByDay(day);
    }
}
