package it.unibas.tav.iotsentinel.persistenza.codamisurazioni;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodaMisurazioniInMemory implements ICodaMisurazioni {

    private List<Misurazione> misurazioni = new ArrayList<>();

    @Override
    public void push(Misurazione misurazione) {
        if (misurazione != null) {
            misurazioni.add(misurazione);
        }
    }

    @Override
    public Misurazione pop() {
        if (misurazioni.isEmpty()) {
            return null;
        }
        return misurazioni.remove(0);
    }

    @Override
    public List<Misurazione> findByIdSensore(int idSensore) {
        return misurazioni.stream()
                .filter(m -> m.getSensore() != null
                        && m.getSensore() instanceof SensoreBase sb
                        && sb.getId() == idSensore)
                .collect(Collectors.toList());
    }
}
