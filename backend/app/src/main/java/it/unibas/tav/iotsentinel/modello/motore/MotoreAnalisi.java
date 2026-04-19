package it.unibas.tav.iotsentinel.modello.motore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.regola.IRegola;
import it.unibas.tav.iotsentinel.modello.strategia.IStrategiaAnalisi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotoreAnalisi {

    private IStrategiaAnalisi strategia;
    private List<IRegola> regole = new ArrayList<>();
    private Map<Integer, Allarme> allarmiAttivi = new HashMap<>();

    public Allarme analizza(Misurazione misurazione) {
        if (strategia == null) {
            return null;
        }
        return strategia.valuta(misurazione);
    }

    public void aggiungiRegola(IRegola regola) {
        if (regola != null) {
            regole.add(regola);
        }
    }

    public void rimuoviRegola(int index) {
        if (index >= 0 && index < regole.size()) {
            regole.remove(index);
        }
    }
}
