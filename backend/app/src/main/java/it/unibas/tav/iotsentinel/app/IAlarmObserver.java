package it.unibas.tav.iotsentinel.app;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;

public interface IAlarmObserver {

    void onAlarmTriggered(Allarme allarme);

    void onAlarmRevoked(Allarme allarme);

    void onTelemetryReceived(Misurazione misurazione);
}
