package it.unibas.tav.iotsentinel.modello.regola;

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
public class RegolaSoglia implements IRegola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double soglia;

    @Override
    public Event valuta() {
        return new Event("RegolaSoglia", null);
    }
}
