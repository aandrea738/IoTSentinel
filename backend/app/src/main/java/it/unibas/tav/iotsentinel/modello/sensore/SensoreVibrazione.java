package it.unibas.tav.iotsentinel.modello.sensore;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("VIBRAZIONE")
@NoArgsConstructor
public class SensoreVibrazione extends SensoreBase {
}
