package it.unibas.tav.iotsentinel.app;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.persistenza.allarme.DAOAllarmi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class AlarmDatabasePersister implements IAlarmObserver {

    @Inject
    DAOAllarmi daoAllarmi;

    @Override
    public void onAlarmTriggered(Allarme allarme) {
        if (allarme != null) {
            try {
                daoAllarmi.makePersistent(allarme);
                log.debug("Allarme triggerato aggiornato su DB. ID: {}, Occorrenze: {}", allarme.getId(), allarme.getOccorrenze());
            } catch (Exception e) {
                log.error("Errore durante il salvataggio dell'allarme su DB", e);
            }
        }
    }

    @Override
    public void onAlarmRevoked(Allarme allarme) {
        if (allarme != null) {
            try {
                daoAllarmi.makePersistent(allarme);
                log.debug("Allarme revocato aggiornato su DB. ID: {}", allarme.getId());
            } catch (Exception e) {
                log.error("Errore durante l'aggiornamento dell'allarme revocato su DB", e);
            }
        }
    }

    @Override
    public void onTelemetryReceived(Misurazione misurazione) {
        // NOP: le telemetrie vengono già salvate nella Facade durante la fase di acquisizione.
    }
}
