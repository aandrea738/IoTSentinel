package it.unibas.tav.iotsentinel.modello.sensore;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CO2")
@NoArgsConstructor
public class SensoreCO2 extends SensoreBase {
}
