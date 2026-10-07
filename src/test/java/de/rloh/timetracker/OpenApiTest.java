package de.rloh.timetracker;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

@QuarkusTest
class OpenApiTest {

    @Test
    void openApiDocumentDescribesAllEndpoints() {
        given()
            .accept("application/json")
        .when()
            .get("/q/openapi")
        .then()
            .statusCode(200)
            .body("paths", hasKey("/time-entries"))
            .body("paths", hasKey("/time-entries/summary"));
    }

    @Test
    void swaggerUiIsServed() {
        given()
        .when()
            .get("/q/swagger-ui/")
        .then()
            .statusCode(200);
    }
}
