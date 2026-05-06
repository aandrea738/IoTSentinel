package it.unibas.tav.iotsentinel.app;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import it.unibas.tav.iotsentinel.DAOException;
import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.misurazione.Telemetria;
import it.unibas.tav.iotsentinel.modello.motore.MotoreAnalisi;
import it.unibas.tav.iotsentinel.modello.regola.IRegola;
import it.unibas.tav.iotsentinel.modello.sensore.EStatoSensore;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import it.unibas.tav.iotsentinel.persistenza.codamisurazioni.ICodaMisurazioni;
import it.unibas.tav.iotsentinel.persistenza.misurazione.IDAOTelemetrie;
import it.unibas.tav.iotsentinel.persistenza.regola.DAORegole;
import it.unibas.tav.iotsentinel.persistenza.sensore.IDAOSensore;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class IoTSentinelFacade {

    @Inject
    IDAOSensore daoSensore;

    @Inject
    ICodaMisurazioni codaMisurazioni;

    @Inject
    DAORegole daoRegole;

    @Inject
    IDAOTelemetrie daoTelemetrie;

    @Inject
    Instance<IAlarmObserver> observerInstances;

    private final MotoreAnalisi motoreAnalisi = new MotoreAnalisi();
    private final List<IAlarmObserver> observers = new CopyOnWriteArrayList<>();

    @PostConstruct
    void init() {
        observerInstances.forEach(this::addObserver);
        motoreAnalisi.setRegole(new CopyOnWriteArrayList<>(daoRegole.findAll()));
    }

    public SensoreBase addSensor(SensoreBase sensore) throws DAOException {
        if (sensore != null) {
            if (sensore.getSeriale() != null && !sensore.getSeriale().isBlank()) {
                SensoreBase esistente = daoSensore.findBySeriale(sensore.getSeriale());
                if (esistente != null && esistente.getId() != sensore.getId()) {
                    throw new DAOException("Seriale già esistente: " + sensore.getSeriale());
                }
            }
            if (sensore.getStato() == null) {
                sensore.setStato(EStatoSensore.ATTIVO);
            }
        }
        return daoSensore.makePersistent(sensore);
    }

    public void removeSensor(long id) throws DAOException {
        SensoreBase sensore = daoSensore.findById(id);
        if (sensore != null) {
            daoSensore.makeTransient(sensore);
        }
    }

    public List<SensoreBase> getSensori() throws DAOException {
        return daoSensore.findAll();
    }

    public SensoreBase getSensore(long id) throws DAOException {
        return daoSensore.findById(id);
    }

    public SensoreBase getSensoreBySeriale(String seriale) throws DAOException {
        return daoSensore.findBySeriale(seriale);
    }

    @Transactional
    public Misurazione acquisisci(Misurazione misurazione) throws DAOException {
        if (misurazione == null) {
            return null;
        }
        if (misurazione.getTimestamp() == null) {
            misurazione.setTimestamp(Instant.now());
        }
        log.info("Acquiring telemetry for sensor: {}", misurazione.getSensore() != null ? misurazione.getSensore().getSeriale() : "NULL");
        
        // Save history first
        Telemetria t = Telemetria.from(misurazione);
        daoTelemetrie.save(t);
        log.info("Saved telemetry to history with ID: {}", t.getId());
        
        // Add to queue for processing
        codaMisurazioni.push(misurazione);
        
        observers.forEach(observer -> observer.onTelemetryReceived(misurazione));
        return misurazione;
    }

    @Transactional
    public void process(Misurazione misurazione) throws DAOException {
        if (misurazione == null) return;
        Allarme allarme = motoreAnalisi.analizza(misurazione);
        aggiornaStatoSensore(misurazione, allarme);
        if (allarme != null) {
            observers.forEach(observer -> observer.onAlarmTriggered(allarme));
        }
    }

    public IRegola addRule(IRegola regola) {
        IRegola regolaPersistita = daoRegole.makePersistent(regola);
        motoreAnalisi.aggiungiRegola(regolaPersistita);
        return regolaPersistita;
    }

    public List<IRegola> getRegole() {
        return new ArrayList<>(motoreAnalisi.getRegole());
    }

    public void removeRule(int index) {
        IRegola regolaRimossa = motoreAnalisi.rimuoviRegola(index);
        if (regolaRimossa != null) {
            daoRegole.makeTransient(regolaRimossa);
        }
    }

    public List<Allarme> getAllarmiAttivi() {
        return motoreAnalisi.getAllarmiAttiviList();
    }

    public List<Allarme> getStoricoAllarmi() {
        return motoreAnalisi.getStoricoAllarmi();
    }

    public List<Telemetria> getTelemetriePaginate(int page, int size) {
        return daoTelemetrie.findPaginated(page, size);
    }

    public void addObserver(IAlarmObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    private void aggiornaStatoSensore(Misurazione misurazione, Allarme allarme) throws DAOException {
        // Sensors stay ACTIVE even if they have alarms. 
        // Logic removed to satisfy requirement: "Un sensore, anche se in allarme rimane comunque attivo."
    }
}
