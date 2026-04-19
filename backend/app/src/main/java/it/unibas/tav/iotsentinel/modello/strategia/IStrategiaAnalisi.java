package it.unibas.tav.iotsentinel.modello.strategia;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;

public interface IStrategiaAnalisi {
    Allarme valuta(Misurazione misurazione);
}
