package it.unibas.tav.iotsentinel.modello.regola;

import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import it.unibas.tav.iotsentinel.modello.eventi.Event;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;

public interface IRegola {
    Event valuta(Misurazione misurazione, List<Misurazione> storicoRecente);

    default Event valuta() {
        return null;
    }

    default EGravita getGravita() {
        return EGravita.MEDIA;
    }

    default String getNome() {
        return getClass().getSimpleName();
    }
}
