package it.unibas.tav.iotsentinel.persistenza.codamisurazioni;

import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.sensore.EStatoSensore;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreTemperatura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CodaMisurazioniInMemoryTest {

    private CodaMisurazioniInMemory codaMisurazioni;

    @BeforeEach
    void setUp() {
        codaMisurazioni = new CodaMisurazioniInMemory();
    }

    @Test
    void testPushEmptyQueue() {
        SensoreTemperatura sensore = createSensore(1);
        Misurazione m1 = new Misurazione(1L, sensore, 25.5, ETipoMisurazione.TEMPERATURA, Instant.now());

        codaMisurazioni.push(m1);

        assertEquals(1, codaMisurazioni.getMisurazioni().size());
        assertEquals(m1, codaMisurazioni.getMisurazioni().get(0));
    }

    @Test
    void testPushMultipleItems() {
        SensoreTemperatura sensore = createSensore(1);
        Misurazione m1 = new Misurazione(1L, sensore, 25.5, ETipoMisurazione.TEMPERATURA, Instant.now());
        Misurazione m2 = new Misurazione(2L, sensore, 26.0, ETipoMisurazione.TEMPERATURA, Instant.now());

        codaMisurazioni.push(m1);
        codaMisurazioni.push(m2);

        assertEquals(2, codaMisurazioni.getMisurazioni().size());
        assertEquals(m1, codaMisurazioni.getMisurazioni().get(0));
        assertEquals(m2, codaMisurazioni.getMisurazioni().get(1));
    }

    @Test
    void testPushNullDoesNotAdd() {
        codaMisurazioni.push(null);

        assertTrue(codaMisurazioni.getMisurazioni().isEmpty());
    }

    @Test
    void testPopFromEmptyQueue() {
        Misurazione popped = codaMisurazioni.pop();

        assertNull(popped);
    }

    @Test
    void testPopRemovesAndReturnsFirstItem() {
        SensoreTemperatura sensore = createSensore(1);
        Misurazione m1 = new Misurazione(1L, sensore, 25.5, ETipoMisurazione.TEMPERATURA, Instant.now());
        Misurazione m2 = new Misurazione(2L, sensore, 26.0, ETipoMisurazione.TEMPERATURA, Instant.now());

        codaMisurazioni.push(m1);
        codaMisurazioni.push(m2);

        Misurazione popped = codaMisurazioni.pop();

        assertEquals(m1, popped, "The popped measurement should be the first one added (FIFO).");
        assertEquals(1, codaMisurazioni.getMisurazioni().size(), "There should be 1 item left in the queue.");
        assertEquals(m2, codaMisurazioni.getMisurazioni().get(0));
    }

    private SensoreTemperatura createSensore(int id) {
        SensoreTemperatura sensore = new SensoreTemperatura();
        sensore.setId(id);
        sensore.setStato(EStatoSensore.ATTIVO);
        return sensore;
    }
}
