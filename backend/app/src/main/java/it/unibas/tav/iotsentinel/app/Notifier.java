package it.unibas.tav.iotsentinel.app;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class Notifier implements IAlarmObserver {

    @Override
    public void onAlarmTriggered(Allarme allarme) {
        if (allarme != null) {
            log.warn("Allarme {}: {} (occorrenze: {})", allarme.getGravita(), allarme.getDescrizione(),
                    allarme.getOccorrenze());
        }
    }

    @Override
    public void onAlarmRevoked(Allarme allarme) {
        if (allarme != null) {
            log.info("Allarme revocato: {}", allarme.getDescrizione());
        }
    }

    @Override
    public void onTelemetryReceived(Misurazione misurazione) {
        log.debug("Telemetria ricevuta: {}", misurazione);
    }
}
