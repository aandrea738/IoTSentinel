package it.unibas.tav.iotsentinel.rest;

import java.util.List;

import it.unibas.tav.iotsentinel.app.IoTSentinelFacade;
import it.unibas.tav.iotsentinel.dto.ApiErrorResponse;
import it.unibas.tav.iotsentinel.dto.ApiMessageResponse;
import it.unibas.tav.iotsentinel.dto.SensoreResponse;
import it.unibas.tav.iotsentinel.modello.sensore.SensoreBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/sensori")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensoreRest {

    @Inject
    IoTSentinelFacade sentinel;

    @GET
    public List<SensoreResponse> getAllSensori(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size, @QueryParam("seriale") String seriale) {
        try {
            List<SensoreBase> sensori = sentinel.getSensoriPaginate(page, size, seriale);
            return sensori.stream().map(SensoreResponse::from).toList();
        } catch (Exception e) {
            log.error("Errore durante il recupero dei sensori", e);
            throw new WebApplicationException(
                    Response.serverError().entity(ApiErrorResponse.of("Errore interno del server: " + e.getMessage()))
                            .build());
        }
    }

    @POST
    public SensoreResponse addSensore(SensoreBase sensore) {
        try {
            SensoreBase saved = sentinel.addSensor(sensore);
            return SensoreResponse.from(saved);
        } catch (Exception e) {
            log.error("Errore durante l'inserimento del sensore", e);
            throw new WebApplicationException(
                    Response.serverError().entity(ApiErrorResponse.of("Errore interno del server: " + e.getMessage()))
                            .build());
        }
    }

    @DELETE
    @Path("/{id}")
    public ApiMessageResponse removeSensore(@PathParam("id") long id) {
        try {
            sentinel.removeSensor(id);
            return ApiMessageResponse.of("Sensore rimosso: " + id);
        } catch (Exception e) {
            log.error("Errore durante la rimozione del sensore", e);
            throw new WebApplicationException(
                    Response.serverError().entity(ApiErrorResponse.of("Errore interno del server: " + e.getMessage()))
                            .build());
        }
    }
}
