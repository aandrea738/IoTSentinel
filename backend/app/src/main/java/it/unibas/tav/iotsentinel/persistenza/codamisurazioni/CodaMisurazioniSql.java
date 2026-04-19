package it.unibas.tav.iotsentinel.persistenza.codamisurazioni;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor
public class CodaMisurazioniSql implements ICodaMisurazioni, PanacheRepository<Misurazione> {

    @Override
    @Transactional
    public void push(Misurazione misurazione) {
        if (misurazione != null) {
            persist(misurazione);
        }
    }

    @Override
    @Transactional
    public Misurazione pop() {
        Misurazione first = find("ORDER BY timestamp ASC").firstResult();
        if (first != null) {
            delete(first);
        }
        return first;
    }

    @Override
    public List<Misurazione> findByIdSensore(int idSensore) {
        return list("sensore.id", idSensore);
    }
}
