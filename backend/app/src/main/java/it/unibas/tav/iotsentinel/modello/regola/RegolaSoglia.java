package it.unibas.tav.iotsentinel.modello.regola;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import it.unibas.tav.iotsentinel.modello.eventi.Event;
import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

    private String nome;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> idSensori = new ArrayList<>();

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

    @Override
    public String getNome() {
        if (nome != null && !nome.isBlank()) {
            return nome;
        }
        return "Soglia " + (tipoMisurazione != null ? tipoMisurazione : "Generica");
    }

    private boolean sensoreCompatibile(Misurazione misurazione) {
        if (idSensori == null || idSensori.isEmpty()) {
            return true;
        }
        return misurazione.getSensore() instanceof SensoreBase sensore && idSensori.contains(sensore.getId());
    }

    private boolean tipoCompatibile(Misurazione misurazione) {
        return tipoMisurazione == null || tipoMisurazione == misurazione.getTipoMisurazione();
    }
}
