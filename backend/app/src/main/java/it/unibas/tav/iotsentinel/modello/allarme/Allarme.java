package it.unibas.tav.iotsentinel.modello.allarme;

import java.time.Instant;
import java.util.List;

import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Allarme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    @JoinTable(name = "allarme_sensori", joinColumns = @JoinColumn(name = "allarme_id"), inverseJoinColumns = @JoinColumn(name = "sensore_id"))
    private List<SensoreBase> sensori;

    @Enumerated(EnumType.STRING)
    private EGravita gravita;

    @Enumerated(EnumType.STRING)
    private EStatoAllarme stato;

    private Instant timestampInizio;
}
