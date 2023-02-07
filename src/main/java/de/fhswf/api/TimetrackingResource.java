package de.fhswf.api;

import de.fhswf.entities.Timetracking;
import io.quarkus.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api")
public class TimetrackingResource {

    @GET
    @Path("time")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response getAll() {
        List<Timetracking> times = Timetracking.listAll();
        return Response.ok(times).build();
    }

    @POST
    @Path("time")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTime(Timetracking timetrackings) {
        Timetracking.persist(timetrackings);
        if (timetrackings.isPersistent()) {
            return Response.created(URI.create("/time" + timetrackings.id)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Transactional
    @Path("time/{id}")
    public Response deleteById(@PathParam("id") Long id) {
        boolean deleted = Timetracking.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("hellosicher")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("time_administration")
    public String share() {
        return "Hello productlist share";
    }
}