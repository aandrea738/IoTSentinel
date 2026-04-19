package it.unibas.tav.iotsentinel.modello.strategia;

import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.regola.IRegola;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalisiStandard implements IStrategiaAnalisi {

    private IStrategiaAnalisi strategia;
    private List<IRegola> regole;

    @Override
    public Allarme valuta(Misurazione misurazione) {
        if (strategia != null) {
            return strategia.valuta(misurazione);
        }
        return null;
    }
}
