package it.unibas.tav.iotsentinel.modello.regola;

import java.time.Duration;

import it.unibas.tav.iotsentinel.modello.eventi.Event;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegolaTemporale implements IRegola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double soglia;

    private Duration durataMinima;

    @Override
    public Event valuta() {
        return new Event("RegolaTemporale", null);
    }
}
