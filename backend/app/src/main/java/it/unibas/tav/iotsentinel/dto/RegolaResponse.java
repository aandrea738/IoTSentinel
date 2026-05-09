package it.unibas.tav.iotsentinel.dto;

import java.util.List;

import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;
import it.unibas.tav.iotsentinel.modello.regola.IRegola;
import it.unibas.tav.iotsentinel.modello.regola.RegolaCorrelazione;
import it.unibas.tav.iotsentinel.modello.regola.RegolaSoglia;
import it.unibas.tav.iotsentinel.modello.regola.RegolaTemporale;

public record RegolaResponse(long id, String nome, String tipo, List<Integer> idSensori, ETipoMisurazione tipoMisurazione,
        Double soglia, RegolaResponse regolaA, RegolaResponse regolaB,
        Long durataMinimaSecondi, Long finestraCorrelazioneSecondi, EGravita gravita) {

    public static RegolaResponse from(IRegola regola) {
        if (regola instanceof RegolaSoglia soglia) {
            return new RegolaResponse(soglia.getId(), soglia.getNome(), "SOGLIA", soglia.getIdSensori(),
                    soglia.getTipoMisurazione(), soglia.getSoglia(), null, null, null, null,
                    soglia.getGravita());
        }
        if (regola instanceof RegolaTemporale temporale) {
            Long durataMinimaSecondi = temporale.getDurataMinima() == null
                    ? null
                    : temporale.getDurataMinima().toSeconds();
            return new RegolaResponse(temporale.getId(), temporale.getNome(), "TEMPORALE", temporale.getIdSensori(),
                    temporale.getTipoMisurazione(), temporale.getSoglia(), null, null,
                    durataMinimaSecondi, null, temporale.getGravita());
        }
        if (regola instanceof RegolaCorrelazione correlazione) {
            Long finestraCorrelazioneSecondi = correlazione.getFinestraCorrelazione() == null
                    ? null
                    : correlazione.getFinestraCorrelazione().toSeconds();
            
            RegolaResponse resA = correlazione.getRegolaA() != null ? RegolaResponse.from(correlazione.getRegolaA()) : null;
            RegolaResponse resB = correlazione.getRegolaB() != null ? RegolaResponse.from(correlazione.getRegolaB()) : null;

            return new RegolaResponse(correlazione.getId(), correlazione.getNome(), "CORRELAZIONE", correlazione.getIdSensori(),
                    null, null, resA, resB, null, finestraCorrelazioneSecondi, correlazione.getGravita());
        }
        return null;
    }
}
