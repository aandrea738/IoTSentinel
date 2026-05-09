package it.unibas.tav.iotsentinel.dto;

import java.time.Instant;

import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.misurazione.Telemetria;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;

public record MisurazioneResponse(long id, Integer idSensore, double valore, String unitaDiMisura, ETipoMisurazione tipoMisurazione,
        Instant timestamp) {

    public static MisurazioneResponse from(Misurazione misurazione) {
        if (misurazione == null) {
            return null;
        }
        Integer idSensore = null;
        String unitaDiMisura = null;
        if (misurazione.getSensore() instanceof SensoreBase sensore) {
            idSensore = sensore.getId();
            unitaDiMisura = sensore.getUnitaDiMisura();
        }
        return new MisurazioneResponse(misurazione.getId(), idSensore, misurazione.getValore(), unitaDiMisura,
                misurazione.getTipoMisurazione(), misurazione.getTimestamp());
    }

    public static MisurazioneResponse from(Telemetria t) {
        if (t == null) return null;
        Integer idSensore = (t.getSensore() != null) ? t.getSensore().getId() : null;
        String unitaDiMisura = (t.getSensore() != null) ? t.getSensore().getUnitaDiMisura() : null;
        return new MisurazioneResponse(t.getId(), idSensore, t.getValore(), unitaDiMisura, t.getTipoMisurazione(), t.getTimestamp());
    }
}
