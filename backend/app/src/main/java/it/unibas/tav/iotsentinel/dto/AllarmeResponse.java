package it.unibas.tav.iotsentinel.dto;

import java.time.Instant;
import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import it.unibas.tav.iotsentinel.modello.allarme.EStatoAllarme;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;

public record AllarmeResponse(long id, List<Integer> idSensori, EGravita gravita, EStatoAllarme stato,
        Instant timestampInizio, Instant timestampFine, String descrizione, long occorrenze) {

    public static AllarmeResponse from(Allarme allarme) {
        if (allarme == null) {
            return null;
        }
        List<Integer> idSensori = allarme.getSensori() == null
                ? List.of()
                : allarme.getSensori().stream().map(SensoreBase::getId).toList();
        return new AllarmeResponse(allarme.getId(), idSensori, allarme.getGravita(), allarme.getStato(),
                allarme.getTimestampInizio(), allarme.getTimestampFine(), allarme.getDescrizione(),
                allarme.getOccorrenze());
    }
}
