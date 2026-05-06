package it.unibas.tav.iotsentinel.modello.motore;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.allarme.EStatoAllarme;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.regola.IRegola;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import it.unibas.tav.iotsentinel.modello.strategia.AnalisiStandard;
import it.unibas.tav.iotsentinel.modello.strategia.IStrategiaAnalisi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotoreAnalisi {

    private IStrategiaAnalisi strategia;
    private List<IRegola> regole = new CopyOnWriteArrayList<>();
    private Map<Integer, Allarme> allarmiAttivi = new ConcurrentHashMap<>();
    private List<Allarme> storicoAllarmi = new CopyOnWriteArrayList<>();

    public Allarme analizza(Misurazione misurazione) {
        inizializzaStrategia();
        Allarme nuovoAllarme = strategia.valuta(misurazione);
        Integer idSensore = estraiIdSensore(misurazione);
        
        if (nuovoAllarme == null) {
            if (idSensore != null) revocaAllarmeSePresente(idSensore);
            return null;
        }

        if (idSensore == null) {
            storicoAllarmi.add(nuovoAllarme);
            return nuovoAllarme;
        }

        Allarme allarmeAttivo = allarmiAttivi.get(idSensore);
        if (allarmeAttivo != null && allarmeAttivo.getStato() == EStatoAllarme.ATTIVO) {
            allarmeAttivo.setOccorrenze(allarmeAttivo.getOccorrenze() + 1);
            allarmeAttivo.setDescrizione(nuovoAllarme.getDescrizione());
            return allarmeAttivo;
        }

        nuovoAllarme.setOccorrenze(1);
        nuovoAllarme.setStato(EStatoAllarme.ATTIVO);
        allarmiAttivi.put(idSensore, nuovoAllarme);
        storicoAllarmi.add(nuovoAllarme);
        return nuovoAllarme;
    }

    public void aggiungiRegola(IRegola regola) {
        if (regola != null) {
            regole.add(regola);
            if (strategia instanceof AnalisiStandard analisiStandard) {
                analisiStandard.setRegole(regole);
            }
        }
    }

    public IRegola rimuoviRegola(int index) {
        if (index >= 0 && index < regole.size()) {
            return regole.remove(index);
        }
        return null;
    }

    public List<Allarme> getAllarmiAttiviList() {
        return new ArrayList<>(allarmiAttivi.values());
    }

    public List<Allarme> getStoricoAllarmi() {
        return new ArrayList<>(storicoAllarmi);
    }

    private void inizializzaStrategia() {
        if (strategia == null) {
            strategia = new AnalisiStandard(null, regole, new ArrayList<>(), Duration.ofHours(24));
        }
    }

    private Integer estraiIdSensore(Misurazione misurazione) {
        if (misurazione == null || !(misurazione.getSensore() instanceof SensoreBase sensore)) {
            return null;
        }
        return sensore.getId();
    }

    private void revocaAllarmeSePresente(Integer idSensore) {
        if (idSensore == null) {
            return;
        }
        Allarme allarme = allarmiAttivi.remove(idSensore);
        if (allarme != null) {
            allarme.setStato(EStatoAllarme.REVOCATO);
            allarme.setTimestampFine(java.time.Instant.now());
        }
    }
}
