package it.unibas.tav.iotsentinel.persistenza.sensore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import it.unibas.tav.iotsentinel.DAOException;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;

public class DAOSensoreInMemory implements IDAOSensore {

    private final List<SensoreBase> sensori = new CopyOnWriteArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public SensoreBase findById(Long id) throws DAOException {
        if (id == null) return null;
        return sensori.stream()
                .filter(s -> s.getId() == id.intValue())
                .findFirst()
                .orElse(null);
    }

    @Override
    public SensoreBase findBySeriale(String seriale) throws DAOException {
        if (seriale == null) return null;
        return sensori.stream()
                .filter(s -> seriale.equals(s.getSeriale()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<SensoreBase> findAll() throws DAOException {
        return new ArrayList<>(sensori);
    }

    @Override
    public List<SensoreBase> findPaginated(int page, int size, String seriale) throws DAOException {
        List<SensoreBase> all = findAll();
        if (seriale != null && !seriale.isBlank()) {
            String search = seriale.toLowerCase();
            all = all.stream()
                    .filter(s -> s.getSeriale() != null && s.getSeriale().toLowerCase().contains(search))
                    .toList();
        }
        int start = page * size;
        if (start >= all.size()) {
            return new java.util.ArrayList<>();
        }
        int end = Math.min(start + size, all.size());
        return all.subList(start, end);
    }

    @Override
    public SensoreBase makePersistent(SensoreBase entity) throws DAOException {
        if (entity == null) return null;
        if (entity.getId() == 0) {
            entity.setId(sequence.getAndIncrement());
            sensori.add(entity);
        } else {
            sensori.removeIf(s -> s.getId() == entity.getId());
            sensori.add(entity);
        }
        return entity;
    }

    @Override
    public void makeTransient(SensoreBase entity) throws DAOException {
        if (entity != null && entity.getId() > 0) {
            sensori.removeIf(s -> s.getId() == entity.getId());
        }
    }
}
