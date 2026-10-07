package de.rloh.timetracker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** Incoming JSON for creating a time entry. Structural validation lives here; business rules live in the service. */
public record CreateTimeEntryRequest(
        @NotBlank @Size(max = 100) String project,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @Size(max = 500) String description) {
}
