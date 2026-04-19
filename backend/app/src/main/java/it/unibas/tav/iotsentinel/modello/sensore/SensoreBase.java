package it.unibas.tav.iotsentinel.modello.sensore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class SensoreBase implements ISensore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private EStatoSensore stato;
}
