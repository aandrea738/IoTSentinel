package it.unibas.tav.iotsentinel.dto;

import java.util.Map;
import it.unibas.tav.iotsentinel.modello.allarme.EGravita;

public record AlarmStatsResponse(Map<EGravita, Long> counts) {
    public static AlarmStatsResponse of(Map<EGravita, Long> counts) {
        return new AlarmStatsResponse(counts);
    }
}
