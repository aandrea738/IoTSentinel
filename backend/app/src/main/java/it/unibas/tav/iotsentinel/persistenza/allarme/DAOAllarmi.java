package it.unibas.tav.iotsentinel.persistenza.allarme;

import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.allarme.EStatoAllarme;

public interface DAOAllarmi {
    Allarme findById(int id);

    List<Allarme> findAll();

    List<Allarme> findByStato(EStatoAllarme stato);
}
