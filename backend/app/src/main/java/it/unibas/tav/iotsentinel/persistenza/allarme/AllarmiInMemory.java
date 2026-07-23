package it.unibas.tav.iotsentinel.persistenza.allarme;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.allarme.EStatoAllarme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllarmiInMemory implements DAOAllarmi {

    private List<Allarme> allarmi = new ArrayList<>();

    @Override
    public Allarme makePersistent(Allarme allarme) {
        if (allarme != null && !allarmi.contains(allarme)) {
            allarmi.add(allarme);
        }
        return allarme;
    }

    @Override
    public void makeTransient(Allarme allarme) {
        allarmi.remove(allarme);
    }

    @Override
    public Allarme findById(Long id) {
        return allarmi.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Allarme> findAll() {
        return new ArrayList<>(allarmi);
    }

    @Override
    public List<Allarme> findByStato(EStatoAllarme stato) {
        return allarmi.stream()
                .filter(a -> Objects.equals(a.getStato(), stato))
                .collect(Collectors.toList());
    }
}
