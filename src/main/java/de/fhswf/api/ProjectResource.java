package de.fhswf.api;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/workingpackage")
public class ProjectResource {

    private static final Logger LOG = Logger.getLogger(TimetrackingResource.class);

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("hellosicher")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("time_administration")
    public String share() {
        return "Hello productlist share";
    }
}
