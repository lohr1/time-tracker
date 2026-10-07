package de.rloh.timetracker;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class TimeEntryResourceTest {

    @Inject
    TimeEntryRepository repository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void postCreatesEntryWithDuration() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"project": "Kunde A", "startTime": "2026-10-07T09:00", "endTime": "2026-10-07T10:30", "description": "Code Review"}
                """)
        .when()
            .post("/time-entries")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("project", is("Kunde A"))
            .body("startTime", is("2026-10-07T09:00:00"))
            .body("durationMinutes", is(90));
    }

    @Test
    void postWithBlankProjectReturns400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"project": "   ", "startTime": "2026-10-07T09:00", "endTime": "2026-10-07T10:00"}
                """)
        .when()
            .post("/time-entries")
        .then()
            .statusCode(400);
    }

    @Test
    void postWithEndBeforeStartReturns400WithMessage() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"project": "Kunde A", "startTime": "2026-10-07T10:00", "endTime": "2026-10-07T09:00"}
                """)
        .when()
            .post("/time-entries")
        .then()
            .statusCode(400)
            .body("error", is("endTime must be after startTime"));
    }

    @Test
    void getReturnsEntriesOfRequestedDayOnly() {
        createEntry("Kunde A", "2026-10-07T09:00", "2026-10-07T10:00");
        createEntry("Kunde A", "2026-10-08T09:00", "2026-10-08T10:00");

        given()
            .queryParam("date", "2026-10-07")
        .when()
            .get("/time-entries")
        .then()
            .statusCode(200)
            .body("size()", is(1))
            .body("[0].project", is("Kunde A"))
            .body("[0].durationMinutes", is(60));
    }

    @Test
    void getWithoutDateReturns400() {
        given()
        .when()
            .get("/time-entries")
        .then()
            .statusCode(400);
    }

    @Test
    void getWithUnparsableDateReturns400() {
        given()
            .queryParam("date", "kein-datum")
        .when()
            .get("/time-entries")
        .then()
            .statusCode(400);
    }

    @Test
    void summaryReturnsTotalAndPerProject() {
        createEntry("Kunde A", "2026-10-07T09:00", "2026-10-07T10:30");
        createEntry("Kunde B", "2026-10-07T11:00", "2026-10-07T12:00");

        given()
            .queryParam("date", "2026-10-07")
        .when()
            .get("/time-entries/summary")
        .then()
            .statusCode(200)
            .body("date", is("2026-10-07"))
            .body("totalMinutes", is(150))
            .body("minutesPerProject.'Kunde A'", is(90))
            .body("minutesPerProject.'Kunde B'", is(60));
    }

    private static void createEntry(String project, String start, String end) {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"project": "%s", "startTime": "%s", "endTime": "%s"}
                """.formatted(project, start, end))
        .when()
            .post("/time-entries")
        .then()
            .statusCode(201);
    }
}
