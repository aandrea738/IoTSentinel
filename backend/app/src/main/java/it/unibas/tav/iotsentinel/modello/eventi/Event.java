package it.unibas.tav.iotsentinel.modello.eventi;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String descrizione;
    private Instant timestamp;
}
