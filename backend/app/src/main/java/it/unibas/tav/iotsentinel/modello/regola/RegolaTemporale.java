package it.unibas.tav.iotsentinel.modello.regola;

import java.time.Duration;
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
public class RegolaTemporale implements IRegola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> idSensori = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ETipoMisurazione tipoMisurazione;

    private double soglia;

    private Duration durataMinima;

    @Enumerated(EnumType.STRING)
    private EGravita gravita = EGravita.ALTA;

    @Override
    public Event valuta(Misurazione misurazione, List<Misurazione> storicoRecente) {
        if (misurazione == null || !(misurazione.getSensore() instanceof SensoreBase sensore)) {
            return null;
        }
        if (idSensori != null && !idSensori.isEmpty() && !idSensori.contains(sensore.getId())) {
            return null;
        }
        if (tipoMisurazione != null && tipoMisurazione != misurazione.getTipoMisurazione()) {
            return null;
        }

        if (misurazione.getValore() <= soglia) {
            return null;
        }

        Duration durata = durataMinima == null ? Duration.ZERO : durataMinima;
        if (durata.isZero()) {
            return new Event(getNome() + ": soglia " + soglia + " superata", Instant.now());
        }

        Instant fine = misurazione.getTimestamp() == null ? Instant.now() : misurazione.getTimestamp();

        if (storicoRecente == null || storicoRecente.isEmpty()) {
            return null;
        }

        boolean foundSufficientHistory = false;

        for (int i = storicoRecente.size() - 1; i >= 0; i--) {
            Misurazione m = storicoRecente.get(i);

            if (!(m.getSensore() instanceof SensoreBase s) || s.getId() != sensore.getId()) {
                continue;
            }
            if (tipoMisurazione != null && m.getTipoMisurazione() != tipoMisurazione) {
                continue;
            }

            if (m.getValore() <= soglia) {
                // Interrupted!
                return null;
            }

            Instant ts = m.getTimestamp() == null ? Instant.now() : m.getTimestamp();
            if (Duration.between(ts, fine).compareTo(durata) >= 0) {
                foundSufficientHistory = true;
                break;
            }
        }

        if (!foundSufficientHistory) {
            return null;
        }

        return new Event(getNome() + ": soglia " + soglia + " superata per " + durata.toSeconds() + " secondi",
                Instant.now());
    }

    @Override
    public String getNome() {
        if (nome != null && !nome.isBlank()) {
            return nome;
        }
        return "Temporale " + (tipoMisurazione != null ? tipoMisurazione : "Generica");
    }
}
