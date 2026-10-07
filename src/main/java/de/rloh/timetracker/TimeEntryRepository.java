package de.rloh.timetracker;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class TimeEntryRepository implements PanacheRepository<TimeEntry> {

    public List<TimeEntry> findByDay(LocalDate day) {
        return list("startTime >= ?1 and startTime < ?2 order by startTime",
                day.atStartOfDay(), day.plusDays(1).atStartOfDay());
    }
}
