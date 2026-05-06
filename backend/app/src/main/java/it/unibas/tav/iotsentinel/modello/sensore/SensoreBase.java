package it.unibas.tav.iotsentinel.modello.sensore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SensoreTemperatura.class, name = "TEMPERATURA"),
    @JsonSubTypes.Type(value = SensorePressione.class, name = "PRESSIONE"),
    @JsonSubTypes.Type(value = SensoreVibrazione.class, name = "VIBRAZIONE"),
    @JsonSubTypes.Type(value = SensoreCO2.class, name = "CO2")
})
public abstract class SensoreBase implements ISensore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @jakarta.persistence.Column(unique = true)
    private String seriale;

    private EStatoSensore stato;
}
