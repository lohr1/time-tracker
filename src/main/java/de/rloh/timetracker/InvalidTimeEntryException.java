package de.rloh.timetracker;

/** Thrown when a time entry violates a business rule. Mapped to HTTP 400 by the REST layer. */
public class InvalidTimeEntryException extends RuntimeException {

    public InvalidTimeEntryException(String message) {
        super(message);
    }
}
