package it.unibas.tav.iotsentinel.rest;

import it.unibas.tav.iotsentinel.app.IoTSentinelFacade;
import it.unibas.tav.iotsentinel.dto.ApiErrorResponse;
import it.unibas.tav.iotsentinel.dto.TelemetriaRequest;
import it.unibas.tav.iotsentinel.modello.misurazione.Misurazione;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/ingestione")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IngestioneRest {

    @Inject
    IoTSentinelFacade sentinel;

    @POST
    public Response acquisisci(TelemetriaRequest request) {
        try {
            SensoreBase sensore = null;
            if (request.getSerialeSensore() != null && !request.getSerialeSensore().isBlank()) {
                sensore = sentinel.getSensoreBySeriale(request.getSerialeSensore());
            } else if (request.getIdSensore() > 0) {
                sensore = sentinel.getSensore(request.getIdSensore());
            }

            if (sensore == null) {
                String identifier = request.getSerialeSensore() != null ? request.getSerialeSensore() : String.valueOf(request.getIdSensore());
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity(ApiErrorResponse.of("Sensore non trovato: " + identifier))
                                .build());
            }
            Misurazione misurazione = new Misurazione();
            misurazione.setSensore(sensore);
            misurazione.setValore(request.getValore());
            misurazione.setTipoMisurazione(request.getTipoMisurazione());
            misurazione.setTimestamp(request.getTimestamp());

            sentinel.acquisisci(misurazione);

            return Response.status(Response.Status.CREATED).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Errore durante l'acquisizione della telemetria", e);
            throw new WebApplicationException(
                    Response.serverError()
                            .entity(ApiErrorResponse.of("Errore interno del server: " + e.getMessage()))
                            .build());
        }
    }
}
