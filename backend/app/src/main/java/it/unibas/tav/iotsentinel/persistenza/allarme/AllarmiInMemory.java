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
    public Allarme findById(int id) {
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
