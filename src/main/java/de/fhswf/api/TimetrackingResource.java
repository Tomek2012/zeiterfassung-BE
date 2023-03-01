package de.fhswf.api;

import de.fhswf.entities.Timetracking;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import services.TimetrackingService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.text.ParseException;
import java.util.List;

@Path("/time")
@Authenticated
public class TimetrackingResource {

    private static final Logger LOG = Logger.getLogger(TimetrackingResource.class);

    @Inject
    TimetrackingService timetrackingService;

    @Inject
    JsonWebToken jwt;


    /**
     * Hole alle Zeiterfassungen aus der DB - Abgleich Datum und UserId (aus dem Token)
     */
    @GET
    @Path("{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTimetrackings(@PathParam("date") String date) throws ParseException {

        LOG.info("Enter: getAllTimetrackings()");
        Response response = timetrackingService.getAllTimetrackings(date);
        LOG.info("Leave: getAllTimetrackings()");

        return response;
    }

    /**
     * Speicherung und Update aller Entities die vom Frontend uebergeben werden
     */
    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveAndUpdate(List<Timetracking> timetracking) {

        LOG.info("Enter: saveAndUpdate()");
        Response response = timetrackingService.saveAndUpdate(timetracking);
        LOG.info("Leave: saveAndUpdate()");

        return response;
    }

    /**
     * Loesche Zeiterfassungen beziehungsweise dessen Entity
     */
    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") Long id) {

        LOG.info("Enter: deleteById()");
        Response response = timetrackingService.deleteById(id);
        LOG.info("Leave: deleteById()");

        return response;
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