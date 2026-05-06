package it.unibas.tav.iotsentinel.modello.regola;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import it.unibas.tav.iotsentinel.modello.eventi.Event;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

    private String nome;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> idSensori = new ArrayList<>();

    @Transient
    private IRegola regolaA;

    @Transient
    private IRegola regolaB;

    private Duration finestraCorrelazione = Duration.ofSeconds(30);

    @Enumerated(EnumType.STRING)
    private EGravita gravita = EGravita.ALTA;

    @Override
    public Event valuta(Misurazione misurazione, List<Misurazione> storicoRecente) {
        if (misurazione == null || regolaA == null || regolaB == null) {
            return null;
        }

        List<Misurazione> candidate = new ArrayList<>();
        if (storicoRecente != null) {
            candidate.addAll(storicoRecente);
        }
        candidate.add(misurazione);

        Instant riferimento = misurazione.getTimestamp() == null ? Instant.now() : misurazione.getTimestamp();
        Duration finestra = finestraCorrelazione == null ? Duration.ofSeconds(30) : finestraCorrelazione;

        Event eventoA = null;
        Event eventoB = null;

        for (Misurazione m : candidate) {
            Instant timestamp = m.getTimestamp() == null ? riferimento : m.getTimestamp();
            if (!timestamp.isBefore(riferimento.minus(finestra)) && !timestamp.isAfter(riferimento.plus(finestra))) {
                if (eventoA == null) {
                    eventoA = regolaA.valuta(m, storicoRecente);
                }
                if (eventoB == null) {
                    eventoB = regolaB.valuta(m, storicoRecente);
                }
            }
        }

        if (eventoA != null && eventoB != null) {
            return new Event(getNome() + ": " + eventoA.getDescrizione() + " e " + eventoB.getDescrizione(),
                    Instant.now());
        }

        return null;
    }

    @Override
    public String getNome() {
        if (nome != null && !nome.isBlank()) {
            return nome;
        }
        String nomeA = regolaA != null ? regolaA.getNome() : "A";
        String nomeB = regolaB != null ? regolaB.getNome() : "B";
        return "Correlazione " + nomeA + "-" + nomeB;
    }
}
