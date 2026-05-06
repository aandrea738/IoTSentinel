package it.unibas.tav.iotsentinel.app;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DashboardUI implements IAlarmObserver {

    private final List<Allarme> allarmiAttivi = new CopyOnWriteArrayList<>();
    private final List<Misurazione> telemetrieRecenti = new CopyOnWriteArrayList<>();

    @Override
    public void onAlarmTriggered(Allarme allarme) {
        if (allarme != null && !allarmiAttivi.contains(allarme)) {
            allarmiAttivi.add(allarme);
        }
    }

    @Override
    public void onAlarmRevoked(Allarme allarme) {
        allarmiAttivi.remove(allarme);
    }

    @Override
    public void onTelemetryReceived(Misurazione misurazione) {
        telemetrieRecenti.add(misurazione);
        while (telemetrieRecenti.size() > 500) {
            telemetrieRecenti.remove(0);
        }
    }

    public List<Allarme> getAllarmiAttivi() {
        return List.copyOf(allarmiAttivi);
    }

    public List<Misurazione> getTelemetrieRecenti() {
        return List.copyOf(telemetrieRecenti);
    }
}
