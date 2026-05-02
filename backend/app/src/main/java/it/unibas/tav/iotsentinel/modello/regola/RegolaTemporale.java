package it.unibas.tav.iotsentinel.modello.regola;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import jakarta.persistence.Transient;
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

    private Integer idSensore;

    @Enumerated(EnumType.STRING)
    private ETipoMisurazione tipoMisurazione;

    private double soglia;

    private Duration durataMinima;

    @Enumerated(EnumType.STRING)
    private EGravita gravita = EGravita.ALTA;

    @Transient
    private Map<Integer, Instant> inizioSuperamentoPerSensore = new ConcurrentHashMap<>();

    @Override
    public Event valuta(Misurazione misurazione, List<Misurazione> storicoRecente) {
        if (misurazione == null || !(misurazione.getSensore() instanceof SensoreBase sensore)) {
            return null;
        }
        if (idSensore != null && sensore.getId() != idSensore) {
            return null;
        }
        if (tipoMisurazione != null && tipoMisurazione != misurazione.getTipoMisurazione()) {
            return null;
        }
        Instant timestamp = misurazione.getTimestamp() == null ? Instant.now() : misurazione.getTimestamp();
        if (misurazione.getValore() <= soglia) {
            inizioSuperamentoPerSensore.remove(sensore.getId());
            return null;
        }
        Instant inizio = inizioSuperamentoPerSensore.computeIfAbsent(sensore.getId(), id -> timestamp);
        Duration durata = durataMinima == null ? Duration.ZERO : durataMinima;
        if (Duration.between(inizio, timestamp).compareTo(durata) < 0) {
            return null;
        }
        return new Event(getNome() + ": soglia " + soglia + " superata per " + durata.toSeconds() + " secondi",
                Instant.now());
    }
}
