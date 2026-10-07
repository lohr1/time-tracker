package de.rloh.timetracker;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class ApplicationStartsTest {

    @Inject
    Flyway flyway;

    @Test
    void flywayMigrationIsApplied() {
        assertEquals("1", flyway.info().current().getVersion().getVersion());
    }
}
