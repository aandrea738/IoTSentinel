package it.unibas.tav.iotsentinel.persistenza.regola;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.unibas.tav.iotsentinel.modello.regola.IRegola;
import it.unibas.tav.iotsentinel.modello.regola.RegolaCorrelazione;
import it.unibas.tav.iotsentinel.modello.regola.RegolaSoglia;
import it.unibas.tav.iotsentinel.modello.regola.RegolaTemporale;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DAORegoleSql implements DAORegole {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public IRegola makePersistent(IRegola regola) {
        if (regola == null) {
            return null;
        }
        if (isNuova(regola)) {
            entityManager.persist(regola);
            return regola;
        }
        return entityManager.merge(regola);
    }

    @Override
    @Transactional
    public void makeTransient(IRegola regola) {
        if (regola == null) {
            return;
        }
        IRegola managed = entityManager.contains(regola) ? regola : entityManager.merge(regola);
        entityManager.remove(managed);
    }

    @Override
    public List<IRegola> findAll() {
        List<IRegola> regole = new ArrayList<>();
        regole.addAll(entityManager.createQuery("SELECT r FROM RegolaSoglia r", RegolaSoglia.class)
                .getResultList());
        regole.addAll(entityManager.createQuery("SELECT r FROM RegolaTemporale r", RegolaTemporale.class)
                .getResultList());
        regole.addAll(entityManager.createQuery("SELECT r FROM RegolaCorrelazione r", RegolaCorrelazione.class)
                .getResultList());
        regole.sort(Comparator.comparing(IRegola::getNome).thenComparingLong(this::idRegola));
        return regole;
    }

    @Override
    public List<IRegola> findPaginated(int page, int size, String nome) {
        List<IRegola> all = findAll();
        if (nome != null && !nome.isBlank()) {
            String search = nome.toLowerCase();
            all = all.stream()
                    .filter(r -> r.getNome() != null && r.getNome().toLowerCase().contains(search))
                    .toList();
        }
        int start = page * size;
        if (start >= all.size()) {
            return new ArrayList<>();
        }
        int end = Math.min(start + size, all.size());
        return all.subList(start, end);
    }

    private boolean isNuova(IRegola regola) {
        return idRegola(regola) == 0;
    }

    private long idRegola(IRegola regola) {
        if (regola instanceof RegolaSoglia soglia) {
            return soglia.getId();
        }
        if (regola instanceof RegolaTemporale temporale) {
            return temporale.getId();
        }
        if (regola instanceof RegolaCorrelazione correlazione) {
            return correlazione.getId();
        }
        return 0;
    }
}
