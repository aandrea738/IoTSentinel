package it.unibas.tav.iotsentinel.dto;

import it.unibas.tav.iotsentinel.modello.sensore.EStatoSensore;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;

public record SensoreResponse(int id, String seriale, String unitaDiMisura, String tipo, EStatoSensore stato) {

    public static SensoreResponse from(SensoreBase sensore) {
        if (sensore == null) {
            return null;
        }
        return new SensoreResponse(sensore.getId(), sensore.getSeriale(), sensore.getUnitaDiMisura(), tipoSensore(sensore), sensore.getStato());
    }

    private static String tipoSensore(SensoreBase sensore) {
        String simpleName = sensore.getClass().getSimpleName();
        return simpleName.replace("Sensore", "").toUpperCase();
    }
}
