package it.unibas.tav.iotsentinel.modello.misurazione;

import java.time.Instant;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "telemetria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Telemetria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "sensore_id")
    private SensoreBase sensore;

    private double valore;

    @Enumerated(EnumType.STRING)
    private ETipoMisurazione tipoMisurazione;

    private Instant timestamp;

    public static Telemetria from(Misurazione m) {
        if (m == null) return null;
        Telemetria t = new Telemetria();
        t.setSensore(m.getSensore());
        t.setValore(m.getValore());
        t.setTipoMisurazione(m.getTipoMisurazione());
        t.setTimestamp(m.getTimestamp());
        return t;
    }
}
