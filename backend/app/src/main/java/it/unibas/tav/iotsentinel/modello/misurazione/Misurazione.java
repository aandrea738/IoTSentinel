package it.unibas.tav.iotsentinel.modello.misurazione;

import java.time.Instant;

import it.unibas.tav.iotsentinel.modello.sensore.ISensore;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Misurazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(targetEntity = SensoreBase.class)
    @JoinColumn(name = "sensore_id")
    private ISensore sensore;

    private double valore;

    @Enumerated(EnumType.STRING)
    private ETipoMisurazione tipoMisurazione;

    private Instant timestamp;
}
