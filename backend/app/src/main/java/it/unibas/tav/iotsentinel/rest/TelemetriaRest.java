package it.unibas.tav.iotsentinel.rest;

import java.util.List;

import it.unibas.tav.iotsentinel.app.IoTSentinelFacade;
import it.unibas.tav.iotsentinel.dto.MisurazioneResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/telemetrie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TelemetriaRest {

    @Inject
    IoTSentinelFacade sentinel;

    @GET
    public List<MisurazioneResponse> getTelemetrie(
            @jakarta.ws.rs.QueryParam("page") @jakarta.ws.rs.DefaultValue("0") int page,
            @jakarta.ws.rs.QueryParam("size") @jakarta.ws.rs.DefaultValue("15") int size) {
        return sentinel.getTelemetriePaginate(page, size).stream().map(MisurazioneResponse::from).toList();
    }
}
