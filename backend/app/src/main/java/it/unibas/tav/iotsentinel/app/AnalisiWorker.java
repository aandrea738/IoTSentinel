package it.unibas.tav.iotsentinel.app;

import io.quarkus.scheduler.Scheduled;
import it.unibas.tav.iotsentinel.DAOException;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.persistenza.codamisurazioni.CodaMisurazioniSql;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class AnalisiWorker {

    @Inject
    CodaMisurazioniSql coda;

    @Inject
    IoTSentinelFacade facade;

    @Scheduled(every = "1s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void analyze() {
        log.debug("Worker starting analysis batch...");
        try {
            Misurazione m;
            while ((m = coda.pop()) != null) {
                facade.process(m);
            }
        } catch (DAOException e) {
            log.error("Error during async analysis", e);
        }
    }
}
