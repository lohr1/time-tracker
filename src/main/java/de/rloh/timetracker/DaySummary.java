package de.rloh.timetracker;

import java.time.LocalDate;
import java.util.Map;

/** Total booked minutes of one day, overall and per project. */
public record DaySummary(LocalDate date, long totalMinutes, Map<String, Long> minutesPerProject) {
}
