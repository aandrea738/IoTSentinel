package it.unibas.tav.iotsentinel.persistenza.codamisurazioni;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.sensore.EStatoSensore;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreTemperatura;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@QuarkusTest
@Slf4j
class CodaMisurazioniSqlTest {

    @Inject
    CodaMisurazioniSql codaMisurazioniSql;

    @BeforeEach
    @Transactional
    void setUp() {
        codaMisurazioniSql.deleteAll();
    }

    @Test
    @Transactional
    void testPushAndPop() {
        SensoreTemperatura sensore = new SensoreTemperatura();
        sensore.setStato(EStatoSensore.ATTIVO);

        Misurazione misurazione = new Misurazione();
        misurazione.setValore(25.5);
        misurazione.setTipoMisurazione(ETipoMisurazione.TEMPERATURA);
        misurazione.setTimestamp(Instant.now());
        codaMisurazioniSql.push(misurazione);
        assertEquals(1, codaMisurazioniSql.count());
        Misurazione popped = codaMisurazioniSql.pop();
        assertEquals(misurazione.getValore(), popped.getValore());
        assertEquals(0, codaMisurazioniSql.count());
    }

    @Test
    @Transactional
    void testPushNullDoesNotAdd() {
        long initialCount = codaMisurazioniSql.count();
        codaMisurazioniSql.push(null);
        assertEquals(initialCount, codaMisurazioniSql.count());
    }

    @Test
    @Transactional
    void testPopFromEmptyQueue() {
        Misurazione popped = codaMisurazioniSql.pop();
        assertNull(popped);
    }
}
