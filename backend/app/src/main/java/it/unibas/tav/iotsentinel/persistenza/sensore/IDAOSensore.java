package it.unibas.tav.iotsentinel.persistenza.sensore;

import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import it.unibas.tav.iotsentinel.persistenza.IDAOGenerico;

public interface IDAOSensore extends IDAOGenerico<SensoreBase> {
    SensoreBase findBySeriale(String seriale) throws it.unibas.tav.iotsentinel.DAOException;

    java.util.List<SensoreBase> findPaginated(int page, int size, String seriale) throws it.unibas.tav.iotsentinel.DAOException;
}
