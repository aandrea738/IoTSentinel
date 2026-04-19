package it.unibas.tav.iotsentinel.modello.sensore;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("TEMPERATURA")
@NoArgsConstructor
public class SensoreTemperatura extends SensoreBase {
}
