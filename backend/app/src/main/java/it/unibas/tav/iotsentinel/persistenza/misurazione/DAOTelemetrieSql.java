package it.unibas.tav.iotsentinel.persistenza.misurazione;

import java.util.List;
import it.unibas.tav.iotsentinel.modello.misurazione.Telemetria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class DAOTelemetrieSql implements IDAOTelemetrie {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void save(Telemetria t) {
        log.debug("Saving telemetria to history: {}", t);
        entityManager.persist(t);
        entityManager.flush();
    }

    public List<Telemetria> findPaginated(int page, int size) {
        log.debug("Querying history: page={}, size={}", page, size);
        List<Telemetria> results = entityManager.createQuery("SELECT t FROM Telemetria t ORDER BY t.timestamp DESC", Telemetria.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        log.debug("History query returned {} results", results.size());
        return results;
    }

    public long countAll() {
        return entityManager.createQuery("SELECT COUNT(t) FROM Telemetria t", Long.class)
                .getSingleResult();
    }
}
