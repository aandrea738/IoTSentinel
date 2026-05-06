package it.unibas.tav.iotsentinel.persistenza.sensore;

import java.util.List;

import it.unibas.tav.iotsentinel.DAOException;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DAOSensoreSql implements IDAOSensore {

    private final EntityManager em;

    public DAOSensoreSql(EntityManager em) {
        this.em = em;
    }

    @Override
    public SensoreBase findById(Long id) throws DAOException {
        try {
            return em.find(SensoreBase.class, id.intValue());
        } catch (Exception ex) {
            log.error("Error finding sensor by ID", ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public SensoreBase findBySeriale(String seriale) throws DAOException {
        try {
            List<SensoreBase> results = em.createQuery("SELECT s FROM SensoreBase s WHERE s.seriale = :seriale", SensoreBase.class)
                    .setParameter("seriale", seriale)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception ex) {
            log.error("Error finding sensor by serial", ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<SensoreBase> findAll() throws DAOException {
        try {
            return em.createQuery("SELECT s FROM SensoreBase s", SensoreBase.class).getResultList();
        } catch (Exception ex) {
            log.error("Error finding all sensors", ex);
            throw new DAOException(ex);
        }
    }

    @Override
    @Transactional
    public SensoreBase makePersistent(SensoreBase entity) throws DAOException {
        try {
            if (entity.getId() == 0) {
                em.persist(entity);
            } else {
                entity = em.merge(entity);
            }
            return entity;
        } catch (Exception ex) {
            log.error("Error persisting sensor", ex);
            throw new DAOException(ex);
        }
    }

    @Override
    @Transactional
    public void makeTransient(SensoreBase entity) throws DAOException {
        try {
            if (entity != null) {
                SensoreBase attached = em.find(SensoreBase.class, entity.getId());
                if (attached != null) {
                    em.remove(attached);
                }
            }
        } catch (Exception ex) {
            log.error("Error removing sensor", ex);
            throw new DAOException(ex);
        }
    }
}
