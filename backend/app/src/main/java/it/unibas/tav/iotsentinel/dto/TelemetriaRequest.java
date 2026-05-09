package it.unibas.tav.iotsentinel.dto;

import java.time.Instant;

import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;
import lombok.Data;

@Data
public class TelemetriaRequest {
    private long idSensore;
    private String serialeSensore;
    private double valore;
    private ETipoMisurazione tipoMisurazione;
    private Instant timestamp;
}
