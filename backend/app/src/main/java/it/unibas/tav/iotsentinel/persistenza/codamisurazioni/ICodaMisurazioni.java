package it.unibas.tav.iotsentinel.persistenza.codamisurazioni;

import java.util.List;

import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;

public interface ICodaMisurazioni {
    void push(Misurazione misurazione);

    Misurazione pop();

    List<Misurazione> popBatch(int maxResults);

    List<Misurazione> findByIdSensore(int idSensore);
}
