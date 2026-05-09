package it.unibas.tav.iotsentinel.persistenza.misurazione;

import java.util.List;

import it.unibas.tav.iotsentinel.modello.misurazione.Telemetria;

public interface IDAOTelemetrie {
    void save(Telemetria t);
    List<Telemetria> findPaginated(int page, int size);
    long countAll();
}