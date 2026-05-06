package it.unibas.tav.iotsentinel.persistenza.codamisurazioni;

import java.util.List;

import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.persistenza.IDAOGenerico;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CodaMisurazioniSql implements IDAOGenerico<Misurazione>, ICodaMisurazioni {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Misurazione makePersistent(Misurazione misurazione) {
        if (misurazione == null) {
            return null;
        }
        entityManager.persist(misurazione);
        return misurazione;
    }

    @Override
    @Transactional
    public void makeTransient(Misurazione misurazione) {
        if (misurazione != null) {
            // Use native delete or direct query to avoid stale object issues
            entityManager.createQuery("DELETE FROM Misurazione m WHERE m.id = :id")
                    .setParameter("id", misurazione.getId())
                    .executeUpdate();
        }
    }

    @Override
    @Transactional
    public Misurazione findById(Long id) {
        return entityManager.find(Misurazione.class, id);
    }

    @Override
    public List<Misurazione> findAll() {
        return entityManager.createQuery(
                "SELECT m FROM Misurazione m ORDER BY m.timestamp ASC", Misurazione.class)
                .getResultList();
    }

    @Override
    @Transactional
    public void push(Misurazione misurazione) {
        if (misurazione != null) {
            makePersistent(misurazione);
            entityManager.flush();
        }
    }

    @Override
    @Transactional
    public Misurazione pop() {
        // Use a lock if necessary, but for now just get the first and delete it
        List<Misurazione> misurazioni = entityManager.createQuery(
                "SELECT m FROM Misurazione m ORDER BY m.timestamp ASC", Misurazione.class)
                .setMaxResults(1)
                .getResultList();
        
        if (misurazioni.isEmpty()) {
            return null;
        }
        
        Misurazione m = misurazioni.get(0);
        // We delete by ID directly to be more robust against OptimisticLockException
        int deleted = entityManager.createQuery("DELETE FROM Misurazione WHERE id = :id")
                .setParameter("id", m.getId())
                .executeUpdate();
        
        if (deleted > 0) {
            log.debug("Popped and deleted misurazione ID: {}", m.getId());
            return m;
        }
        return null;
    }

    @Override
    public List<Misurazione> findByIdSensore(int idSensore) {
        return entityManager.createQuery(
                "SELECT m FROM Misurazione m WHERE m.sensore.id = :idSensore ORDER BY m.timestamp ASC",
                Misurazione.class)
                .setParameter("idSensore", idSensore)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Misurazione> popBatch(int maxResults) {
        List<Misurazione> batch = entityManager.createQuery(
                "SELECT m FROM Misurazione m ORDER BY m.timestamp ASC", Misurazione.class)
                .setMaxResults(maxResults)
                .getResultList();
        
        if (batch.isEmpty()) {
            return batch;
        }
        
        List<Long> ids = batch.stream().map(Misurazione::getId).toList();
        int deleted = entityManager.createQuery("DELETE FROM Misurazione m WHERE m.id IN :ids")
                .setParameter("ids", ids)
                .executeUpdate();
        
        log.debug("Popped and deleted {} misurazioni", deleted);
        return batch;
    }
}
