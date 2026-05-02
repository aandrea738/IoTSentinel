package it.unibas.tav.iotsentinel.persistenza.sensore;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class DAOSensoreFactory {

    @ConfigProperty(name = "iotsentinel.dao.sensore.strategy", defaultValue = "sql")
    String strategy;

    @Inject
    EntityManager em;

    @Produces
    @ApplicationScoped
    public IDAOSensore createDAOSensore() {
        log.info("Configurazione DAO Sensore strategy: {}", strategy);
        if ("memory".equalsIgnoreCase(strategy)) {
            return new DAOSensoreInMemory();
        } else {
            return new DAOSensoreSql(em);
        }
    }
}
