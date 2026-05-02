package it.unibas.tav.iotsentinel.modello.regola;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import it.unibas.tav.iotsentinel.modello.eventi.Event;
import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegolaCorrelazione implements IRegola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Transient
    private IRegola regolaA;

    @Transient
    private IRegola regolaB;

    @Enumerated(EnumType.STRING)
    private ETipoMisurazione tipoA;

    private double sogliaA;

    @Enumerated(EnumType.STRING)
    private ETipoMisurazione tipoB;

    private double sogliaB;

    private Duration finestraCorrelazione = Duration.ofSeconds(30);

    @Enumerated(EnumType.STRING)
    private EGravita gravita = EGravita.ALTA;

    @Override
    public Event valuta(Misurazione misurazione, List<Misurazione> storicoRecente) {
        if (misurazione == null) {
            return null;
        }
        if (regolaA != null && regolaB != null) {
            Event eventoA = regolaA.valuta(misurazione, storicoRecente);
            Event eventoB = regolaB.valuta(misurazione, storicoRecente);
            if (eventoA != null && eventoB != null) {
                return new Event(getNome() + ": " + eventoA.getDescrizione() + " e " + eventoB.getDescrizione(),
                        Instant.now());
            }
        }
        if (tipoA == null || tipoB == null) {
            return null;
        }
        List<Misurazione> candidate = new ArrayList<>();
        if (storicoRecente != null) {
            candidate.addAll(storicoRecente);
        }
        candidate.add(misurazione);

        Instant riferimento = misurazione.getTimestamp() == null ? Instant.now() : misurazione.getTimestamp();
        Duration finestra = finestraCorrelazione == null ? Duration.ofSeconds(30) : finestraCorrelazione;
        boolean a = candidate.stream().anyMatch(m -> superaSogliaNellaFinestra(m, tipoA, sogliaA, riferimento, finestra));
        boolean b = candidate.stream().anyMatch(m -> superaSogliaNellaFinestra(m, tipoB, sogliaB, riferimento, finestra));
        if (!a || !b) {
            return null;
        }
        return new Event(getNome() + ": correlate " + tipoA + ">" + sogliaA + " e " + tipoB + ">" + sogliaB,
                Instant.now());
    }

    private boolean superaSogliaNellaFinestra(Misurazione misurazione, ETipoMisurazione tipo, double soglia,
            Instant riferimento, Duration finestra) {
        if (misurazione == null || misurazione.getTipoMisurazione() != tipo || misurazione.getValore() <= soglia) {
            return false;
        }
        Instant timestamp = misurazione.getTimestamp() == null ? riferimento : misurazione.getTimestamp();
        return !timestamp.isBefore(riferimento.minus(finestra)) && !timestamp.isAfter(riferimento.plus(finestra));
    }
}
