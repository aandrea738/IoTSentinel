package it.unibas.tav.iotsentinel.app;

import java.util.List;
import java.util.stream.Collectors;

import io.quarkus.scheduler.Scheduled;
import it.unibas.tav.iotsentinel.DAOException;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.persistenza.codamisurazioni.ICodaMisurazioni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class AnalisiWorker {

    @Inject
    ICodaMisurazioni coda;

    @Inject
    IoTSentinelFacade facade;

    @Scheduled(every = "1s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void analyze() {
        log.debug("Inizio analisi...");
        try {
            int batchSize = 500;
            List<Misurazione> batch = coda.popBatch(batchSize);

            if (batch != null && !batch.isEmpty()) {
                var misurazioniPerSensore = batch.stream()
                        .filter(m -> m.getSensore() != null)
                        .collect(Collectors.groupingBy(m -> m.getSensore().getId()));

                misurazioniPerSensore.values().forEach(misurazioniSensore -> {
                    for (Misurazione m : misurazioniSensore) {
                        try {
                            facade.process(m);
                        } catch (DAOException e) {
                            log.error("Error during async analysis for measurement {}", m.getId(), e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            log.error("Errore durante l'analisi", e);
        }
    }
}
