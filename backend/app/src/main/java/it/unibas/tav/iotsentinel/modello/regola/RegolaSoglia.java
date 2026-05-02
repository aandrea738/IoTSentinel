package it.unibas.tav.iotsentinel.modello.regola;

import java.time.Instant;
import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import it.unibas.tav.iotsentinel.modello.eventi.Event;
import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private Integer idSensore;

    @Enumerated(EnumType.STRING)
    private ETipoMisurazione tipoMisurazione;

    private double soglia;

    @Enumerated(EnumType.STRING)
    private EGravita gravita = EGravita.MEDIA;

    @Override
    public Event valuta(Misurazione misurazione, List<Misurazione> storicoRecente) {
        if (misurazione == null || !sensoreCompatibile(misurazione) || !tipoCompatibile(misurazione)) {
            return null;
        }
        if (misurazione.getValore() <= soglia) {
            return null;
        }
        return new Event(getNome() + ": " + misurazione.getTipoMisurazione() + " oltre soglia " + soglia,
                Instant.now());
    }

    private boolean sensoreCompatibile(Misurazione misurazione) {
        if (idSensore == null) {
            return true;
        }
        return misurazione.getSensore() instanceof SensoreBase sensore && sensore.getId() == idSensore;
    }

    private boolean tipoCompatibile(Misurazione misurazione) {
        return tipoMisurazione == null || tipoMisurazione == misurazione.getTipoMisurazione();
    }
}
