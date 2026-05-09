package it.unibas.tav.iotsentinel.modello.motore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import it.unibas.tav.iotsentinel.modello.allarme.EStatoAllarme;
import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.regola.RegolaCorrelazione;
import it.unibas.tav.iotsentinel.modello.sensore.EStatoSensore;
import it.unibas.tav.iotsentinel.modello.sensore.SensorePressione;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreTemperatura;

class MotoreAnalisiCorrelazioneTest {

    private static final Instant BASE_TIME = Instant.parse("2026-05-02T10:00:00Z");

    private MotoreAnalisi motore;
    private SensoreTemperatura sensoreTemperatura;
    private SensorePressione sensorePressione;

    @BeforeEach
    void setUp() {
        motore = new MotoreAnalisi();
        motore.aggiungiRegola(regolaTemperaturaPressione());

        sensoreTemperatura = new SensoreTemperatura();
        sensoreTemperatura.setId(1);
        sensoreTemperatura.setStato(EStatoSensore.ATTIVO);

        sensorePressione = new SensorePressione();
        sensorePressione.setId(2);
        sensorePressione.setStato(EStatoSensore.ATTIVO);
    }

    @Test
    void nonGeneraAllarmeConUnaSolaMisurazioneAnomala() {
        Allarme allarme = motore.analizza(temperatura(90.0, BASE_TIME));

        assertNull(allarme);
        assertTrue(motore.getAllarmiAttiviList().isEmpty());
        assertTrue(motore.getStoricoAllarmi().isEmpty());
    }

    @Test
    void generaAllarmeQuandoLeMisurazioniSonoCorrelateNellaFinestraTemporale() {
        motore.analizza(temperatura(90.0, BASE_TIME));

        Allarme allarme = motore.analizza(pressione(6.0, BASE_TIME.plusSeconds(5)));

        assertNotNull(allarme);
        assertEquals(EStatoAllarme.ATTIVO, allarme.getStato());
        assertEquals(EGravita.ALTA, allarme.getGravita());
        assertEquals(1, allarme.getOccorrenze());
        assertTrue(allarme.getDescrizione().contains("TEMPERATURA>80.0"));
        assertTrue(allarme.getDescrizione().contains("PRESSIONE>5.0"));
        assertEquals(1, motore.getAllarmiAttiviList().size());
        assertEquals(1, motore.getStoricoAllarmi().size());
    }

    @Test
    void nonGeneraAllarmeQuandoLeMisurazioniSonoFuoriFinestraTemporale() {
        motore.analizza(temperatura(90.0, BASE_TIME));

        Allarme allarme = motore.analizza(pressione(6.0, BASE_TIME.plusSeconds(20)));

        assertNull(allarme);
        assertTrue(motore.getAllarmiAttiviList().isEmpty());
        assertTrue(motore.getStoricoAllarmi().isEmpty());
    }

    @Test
    void aggregaAllarmiRipetutiDelloStessoSensoreSenzaDuplicareLoStorico() {
        motore.analizza(temperatura(90.0, BASE_TIME));
        Allarme primoAllarme = motore.analizza(pressione(6.0, BASE_TIME.plusSeconds(5)));

        Allarme secondoAllarme = motore.analizza(pressione(6.5, BASE_TIME.plusSeconds(6)));

        assertSame(primoAllarme, secondoAllarme);
        assertEquals(2, secondoAllarme.getOccorrenze());
        assertEquals(1, motore.getAllarmiAttiviList().size());
        assertEquals(1, motore.getStoricoAllarmi().size());
    }

    @Test
    void revocaAllarmeAttivoQuandoLaCorrelazioneNonESoddisfatta() {
        motore.analizza(temperatura(90.0, BASE_TIME));
        Allarme allarme = motore.analizza(pressione(6.0, BASE_TIME.plusSeconds(5)));

        Allarme esitoNormale = motore.analizza(pressione(4.0, BASE_TIME.plusSeconds(40)));

        assertNull(esitoNormale);
        assertEquals(EStatoAllarme.REVOCATO, allarme.getStato());
        assertNotNull(allarme.getTimestampFine());
        assertTrue(motore.getAllarmiAttiviList().isEmpty());
        assertEquals(1, motore.getStoricoAllarmi().size());
    }

    private RegolaCorrelazione regolaTemperaturaPressione() {
        RegolaCorrelazione regola = new RegolaCorrelazione();
        regola.setTipoA(ETipoMisurazione.TEMPERATURA);
        regola.setSogliaA(80.0);
        regola.setTipoB(ETipoMisurazione.PRESSIONE);
        regola.setSogliaB(5.0);
        regola.setFinestraCorrelazione(Duration.ofSeconds(10));
        regola.setGravita(EGravita.ALTA);
        return regola;
    }

    private Misurazione temperatura(double valore, Instant timestamp) {
        return new Misurazione(0L, sensoreTemperatura, valore, ETipoMisurazione.TEMPERATURA, timestamp);
    }

    private Misurazione pressione(double valore, Instant timestamp) {
        return new Misurazione(0L, sensorePressione, valore, ETipoMisurazione.PRESSIONE, timestamp);
    }
}
