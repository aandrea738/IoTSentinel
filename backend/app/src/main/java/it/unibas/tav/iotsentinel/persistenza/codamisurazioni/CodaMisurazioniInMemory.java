package it.unibas.tav.iotsentinel.persistenza.codamisurazioni;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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

    private Queue<Misurazione> misurazioni = new ConcurrentLinkedQueue<>();

    @Override
    public void push(Misurazione misurazione) {
        if (misurazione != null) {
            misurazioni.add(misurazione);
        }
    }

    @Override
    public Misurazione pop() {
        return misurazioni.poll();
    }

    @Override
    public List<Misurazione> popBatch(int maxResults) {
        List<Misurazione> batch = new ArrayList<>();
        Misurazione m;
        while (batch.size() < maxResults && (m = misurazioni.poll()) != null) {
            batch.add(m);
        }
        return batch;
    }

    @Override
    public List<Misurazione> findByIdSensore(int idSensore) {
        return misurazioni.stream()
                .filter(m -> m.getSensore() != null
                        && m.getSensore() instanceof SensoreBase sb
                        && sb.getId() == idSensore)
                .collect(Collectors.toList());
    }

    public List<Misurazione> getMisurazioni() {
        return new ArrayList<>(misurazioni);
    }

    public void setMisurazioni(List<Misurazione> misurazioni) {
        this.misurazioni = new ConcurrentLinkedQueue<>();
        if (misurazioni != null) {
            this.misurazioni.addAll(misurazioni);
        }
    }
}
