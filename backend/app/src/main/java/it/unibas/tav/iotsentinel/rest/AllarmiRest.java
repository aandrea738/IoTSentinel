package it.unibas.tav.iotsentinel.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unibas.tav.iotsentinel.app.IoTSentinelFacade;
import it.unibas.tav.iotsentinel.dto.AlarmStatsResponse;
import it.unibas.tav.iotsentinel.dto.AllarmeResponse;
import it.unibas.tav.iotsentinel.modello.allarme.Allarme;
import it.unibas.tav.iotsentinel.modello.allarme.EGravita;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/allarmi")
@Produces(MediaType.APPLICATION_JSON)
public class AllarmiRest {

    @Inject
    IoTSentinelFacade sentinel;

    @GET
    @Path("/attivi")
    public List<AllarmeResponse> getAttivi(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return sentinel.getAllarmiAttivi().stream()
                .sorted((a1, a2) -> a2.getTimestampInizio().compareTo(a1.getTimestampInizio()))
                .skip((long) page * size)
                .limit(size)
                .map(AllarmeResponse::from).toList();
    }

    @GET
    @Path("/stats")
    public AlarmStatsResponse getStats() {
        Map<EGravita, Long> stats = sentinel.getAllarmiAttivi().stream()
                .collect(Collectors.groupingBy(Allarme::getGravita, Collectors.counting()));
        return AlarmStatsResponse.of(stats);
    }

    @GET
    public List<AllarmeResponse> getStorico() {
        return sentinel.getStoricoAllarmi().stream().map(AllarmeResponse::from).toList();
    }
}
