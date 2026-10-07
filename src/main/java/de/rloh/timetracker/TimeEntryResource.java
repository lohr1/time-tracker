package de.rloh.timetracker;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/** HTTP entry point. Maps JSON to service calls and back, no business logic here. */
@Path("/time-entries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeEntryResource {

    private final TimeEntryService service;

    public TimeEntryResource(TimeEntryService service) {
        this.service = service;
    }

    @POST
    public RestResponse<TimeEntryResponse> create(@Valid CreateTimeEntryRequest request) {
        TimeEntry entry = service.create(
                request.project(), request.startTime(), request.endTime(), request.description());
        return RestResponse.status(Response.Status.CREATED, TimeEntryResponse.from(entry));
    }

    @GET
    public List<TimeEntryResponse> entriesForDay(@RestQuery @NotNull String date) {
        return service.entriesForDay(parseDate(date)).stream()
                .map(TimeEntryResponse::from)
                .toList();
    }

    @GET
    @Path("/summary")
    public DaySummary summaryForDay(@RestQuery @NotNull String date) {
        return service.summaryForDay(parseDate(date));
    }

    /**
     * Parsed by hand: JAX-RS answers a failed query parameter conversion with 404,
     * which would hide a client mistake behind "not found". We want a plain 400.
     */
    private static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("date must be an ISO date like 2026-10-07");
        }
    }
}
