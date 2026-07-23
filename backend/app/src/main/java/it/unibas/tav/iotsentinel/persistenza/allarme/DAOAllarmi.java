package it.unibas.tav.iotsentinel.persistenza.allarme;

import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.allarme.EStatoAllarme;
import it.unibas.tav.iotsentinel.persistenza.IDAOGenerico;

public interface DAOAllarmi extends IDAOGenerico<Allarme> {
    Allarme findById(Long id);

    List<Allarme> findAll();

    List<Allarme> findByStato(EStatoAllarme stato);
}
