package it.unibas.tav.iotsentinel.persistenza.allarme;

import java.util.List;
import java.util.Objects;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.allarme.EStatoAllarme;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class AllarmiSql implements DAOAllarmi {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Allarme makePersistent(Allarme allarme) {
        if (allarme == null) {
            return null;
        }
        if (allarme.getId() == 0) {
            entityManager.persist(allarme);
            return allarme;
        }
        return entityManager.merge(allarme);
    }

    @Override
    @Transactional
    public void makeTransient(Allarme allarme) {
        if (allarme != null) {
            Allarme managed = entityManager.contains(allarme) ? allarme : entityManager.merge(allarme);
            entityManager.remove(managed);
        }
    }

    @Override
    public Allarme findById(Long id) {
        return entityManager.find(Allarme.class, id);
    }

    @Override
    public List<Allarme> findAll() {
        return entityManager.createQuery("SELECT a FROM Allarme a ORDER BY a.timestampInizio DESC", Allarme.class)
                .getResultList();
    }

    @Override
    public List<Allarme> findByStato(EStatoAllarme stato) {
        return entityManager.createQuery("SELECT a FROM Allarme a WHERE a.stato = :stato ORDER BY a.timestampInizio DESC", Allarme.class)
                .setParameter("stato", stato)
                .getResultList();
    }
}
