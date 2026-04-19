package it.unibas.tav.iotsentinel.modello.eventi;

import java.time.Instant;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmEvent {
    private Allarme allarme;
    private Instant timestamp;
}
