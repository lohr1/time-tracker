package de.rloh.timetracker;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Translates business rule violations into HTTP 400 with a small JSON body. */
@Provider
public class InvalidTimeEntryExceptionMapper implements ExceptionMapper<InvalidTimeEntryException> {

    @Override
    public Response toResponse(InvalidTimeEntryException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}
