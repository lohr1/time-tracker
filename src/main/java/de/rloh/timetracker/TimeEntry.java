package de.rloh.timetracker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A single booking of working time on a project. Duration is derived, not stored.
 */
@Entity
@Table(name = "time_entry")
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String project;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(length = 500)
    private String description;

    /** Required by JPA. */
    protected TimeEntry() {
    }

    public TimeEntry(String project, LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.project = project;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public Duration duration() {
        return Duration.between(startTime, endTime);
    }

    /** Two intervals overlap when each one starts before the other ends. Touching edges do not overlap. */
    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return otherStart.isBefore(endTime) && otherEnd.isAfter(startTime);
    }

    public Long getId() {
        return id;
    }

    public String getProject() {
        return project;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }
}
