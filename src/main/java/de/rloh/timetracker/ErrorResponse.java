package de.rloh.timetracker;

/** Minimal error body: {"error": "..."} */
public record ErrorResponse(String error) {
}
