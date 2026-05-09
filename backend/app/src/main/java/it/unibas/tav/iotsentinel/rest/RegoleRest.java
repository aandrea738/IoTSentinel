package it.unibas.tav.iotsentinel.rest;

import java.util.List;

import it.unibas.tav.iotsentinel.app.IoTSentinelFacade;
import it.unibas.tav.iotsentinel.dto.ApiMessageResponse;
import it.unibas.tav.iotsentinel.dto.RegolaResponse;
import it.unibas.tav.iotsentinel.modello.regola.RegolaCorrelazione;
import it.unibas.tav.iotsentinel.modello.regola.RegolaSoglia;
import it.unibas.tav.iotsentinel.modello.regola.RegolaTemporale;
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
import jakarta.ws.rs.core.MediaType;

@Path("/regole")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegoleRest {

    @Inject
    IoTSentinelFacade sentinel;

    @GET
    public List<RegolaResponse> getRegole(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size, @QueryParam("nome") String nome) {
        return sentinel.getRegolePaginate(page, size, nome).stream().map(RegolaResponse::from).toList();
    }

    @POST
    @Path("/soglia")
    public RegolaResponse addRegolaSoglia(RegolaSoglia regola) {
        return RegolaResponse.from(sentinel.addRule(regola));
    }

    @POST
    @Path("/temporale")
    public RegolaResponse addRegolaTemporale(RegolaTemporale regola) {
        return RegolaResponse.from(sentinel.addRule(regola));
    }

    @POST
    @Path("/correlazione")
    public RegolaResponse addRegolaCorrelazione(RegolaCorrelazione regola) {
        return RegolaResponse.from(sentinel.addRule(regola));
    }

    @DELETE
    @Path("/{index}")
    public ApiMessageResponse removeRegola(@PathParam("index") int index) {
        sentinel.removeRule(index);
        return ApiMessageResponse.of("Regola rimossa all'indice: " + index);
    }
}
