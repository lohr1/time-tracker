package de.rloh.timetracker;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TimeEntryServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 7);

    @Inject
    TimeEntryService service;

    @Inject
    TimeEntryRepository repository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsEntryAndComputesDuration() {
        TimeEntry entry = service.create("Kunde A", at(9, 0), at(10, 30), "Code Review");

        assertNotNull(entry.getId());
        assertEquals(90, entry.duration().toMinutes());
    }

    @Test
    void rejectsEndNotAfterStart() {
        InvalidTimeEntryException ex = assertThrows(InvalidTimeEntryException.class,
                () -> service.create("Kunde A", at(10, 0), at(10, 0), null));
        assertEquals("endTime must be after startTime", ex.getMessage());
    }

    @Test
    void rejectsEntryCrossingMidnight() {
        LocalDateTime start = at(23, 0);
        LocalDateTime end = DAY.plusDays(1).atTime(1, 0);

        assertThrows(InvalidTimeEntryException.class, () -> service.create("Kunde A", start, end, null));
    }

    @Test
    void rejectsOverlapWithExistingEntry() {
        service.create("Kunde A", at(9, 0), at(11, 0), null);

        assertThrows(InvalidTimeEntryException.class, () -> service.create("Kunde B", at(10, 0), at(12, 0), null));
    }

    @Test
    void allowsEntryStartingExactlyWhenPreviousEnds() {
        service.create("Kunde A", at(9, 0), at(11, 0), null);

        assertDoesNotThrow(() -> service.create("Kunde B", at(11, 0), at(12, 0), null));
    }

    @Test
    void entriesForDayReturnsOnlyThatDaySortedByStart() {
        service.create("Kunde A", at(13, 0), at(14, 0), null);
        service.create("Kunde A", at(9, 0), at(10, 0), null);
        service.create("Kunde A", DAY.plusDays(1).atTime(9, 0), DAY.plusDays(1).atTime(10, 0), null);

        List<TimeEntry> entries = service.entriesForDay(DAY);

        assertEquals(2, entries.size());
        assertEquals(at(9, 0), entries.get(0).getStartTime());
        assertEquals(at(13, 0), entries.get(1).getStartTime());
    }

    private static LocalDateTime at(int hour, int minute) {
        return DAY.atTime(hour, minute);
    }
}
