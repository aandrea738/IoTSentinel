package it.unibas.tav.iotsentinel.modello.strategia;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.allarme.EStatoAllarme;
import it.unibas.tav.iotsentinel.modello.eventi.Event;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.regola.IRegola;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalisiStandard implements IStrategiaAnalisi {

    private IStrategiaAnalisi strategia;
    private List<IRegola> regole;
    private List<Misurazione> storicoRecente = new ArrayList<>();
    private Duration finestraStorico = Duration.ofHours(24);

    /**
     * Valuta una nuova misurazione rispetto alle regole definite.
     * Mantiene traccia dello storico recente e, se una regola viene violata,
     * genera e restituisce un nuovo Allarme.
     */
    @Override
    public Allarme valuta(Misurazione misurazione) {
        if (strategia != null) {
            return strategia.valuta(misurazione);
        }
        if (misurazione == null || regole == null || regole.isEmpty()) {
            return null;
        }
        storicoRecente.add(misurazione);
        compattaStorico(misurazione.getTimestamp() == null ? Instant.now() : misurazione.getTimestamp());
        for (IRegola regola : regole) {
            Event evento = regola.valuta(misurazione, storicoRecente);
            if (evento != null) {
                Allarme allarme = new Allarme();
                if (misurazione.getSensore() instanceof SensoreBase sensore) {
                    allarme.setSensori(List.of(sensore));
                }
                allarme.setGravita(regola.getGravita());
                allarme.setStato(EStatoAllarme.ATTIVO);
                allarme.setTimestampInizio(Instant.now());
                allarme.setDescrizione(evento.getDescrizione());
                allarme.setOccorrenze(1);
                return allarme;
            }
        }
        return null;
    }

    /**
     * Rimuove dallo storico le misurazioni più vecchie della finestra temporale
     * configurata (finestraStorico), calcolata a partire dall'istante di riferimento.
     */
    private void compattaStorico(Instant riferimento) {
        if (finestraStorico == null) return;
        Instant soglia = riferimento.minus(finestraStorico);
        storicoRecente.removeIf(m -> {
            Instant ts = m.getTimestamp() == null ? Instant.now() : m.getTimestamp();
            return ts.isBefore(soglia);
        });
    }
}
