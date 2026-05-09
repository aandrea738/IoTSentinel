package it.unibas.tav.iotsentinel.modello.regola;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import it.unibas.tav.iotsentinel.modello.eventi.Event;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = RegolaSoglia.class, name = "SOGLIA"),
    @JsonSubTypes.Type(value = RegolaTemporale.class, name = "TEMPORALE"),
    @JsonSubTypes.Type(value = RegolaCorrelazione.class, name = "CORRELAZIONE")
})
public interface IRegola {
    Event valuta(Misurazione misurazione, List<Misurazione> storicoRecente);

    default Event valuta() {
        return null;
    }

    default EGravita getGravita() {
        return EGravita.MEDIA;
    }

    default String getNome() {
        return getClass().getSimpleName();
    }
}
