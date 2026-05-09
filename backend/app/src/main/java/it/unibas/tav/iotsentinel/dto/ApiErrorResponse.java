package it.unibas.tav.iotsentinel.dto;

import java.time.Instant;

public record ApiErrorResponse(String message, Instant timestamp) {

    public static ApiErrorResponse of(String message) {
        return new ApiErrorResponse(message, Instant.now());
    }
}
