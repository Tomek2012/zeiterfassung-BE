package de.fhswf.api;

import de.fhswf.entities.Timetracking;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.DateFormat;


import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import java.util.Date;
import java.util.List;

@Path("/time")
@Authenticated
public class TimetrackingResource {

    private static final Logger LOG = Logger.getLogger(TimetrackingResource.class);

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTimetrackings(@QueryParam("date") String date) {
        LOG.info(date);
        String test = jwt.getClaim(Claims.sub);
        List<Timetracking> times = Timetracking.list("userId", test);
        return Response.ok(times).build();
    }

    @POST
    @Path("/save")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveAndUpdate(List<Timetracking> timetracking) {
        try {
            for (int i = 0; i < timetracking.size();
                 i++) {
                if (timetracking.get(i).id == null) {
                    timetracking.get(i).userId = jwt.getClaim(Claims.sub);
                    Timetracking.persist(timetracking.get(i));
                } else {
                    Timetracking entity = Timetracking.findById(timetracking.get(i).id);
                    entity.fromTime = timetracking.get(i).fromTime;
                    entity.toTime = timetracking.get(i).toTime;
                    entity.workingpackage = timetracking.get(i).workingpackage;
                    entity.description = timetracking.get(i).description;
                }
            }
            return Response.ok(Timetracking.listAll()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @DELETE
    @Transactional
    @Path("{id}")
    public Response deleteById(@PathParam("id") Long id) {
        boolean deleted = Timetracking.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // Nur zum Hinzufuegen neuer Zeiterfassungen (Testzwecke) - FE benutzt saveAndUpdate()
    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveTime(Timetracking timetracking) {
        timetracking.userId = jwt.getClaim(Claims.sub);
        Timetracking.persist(timetracking);
        if (timetracking.isPersistent()) {
            return Response.created(URI.create("/time" + timetracking.id)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}