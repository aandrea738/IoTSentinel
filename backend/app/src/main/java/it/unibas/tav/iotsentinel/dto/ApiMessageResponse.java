package it.unibas.tav.iotsentinel.dto;

import java.time.Instant;

public record ApiMessageResponse(String message, Instant timestamp) {

    public static ApiMessageResponse of(String message) {
        return new ApiMessageResponse(message, Instant.now());
    }
}
