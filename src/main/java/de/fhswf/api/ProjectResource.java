package de.fhswf.api;

import de.fhswf.entities.Project;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import services.ProjectService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/project")
@RolesAllowed("time_administration")
public class ProjectResource {

    private static final Logger LOG = Logger.getLogger(TimetrackingResource.class);
    @Inject
    ProjectService projectService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjects() {

        LOG.info("Enter: getAllProjects()");
        Response response = projectService.getAllProjects();
        LOG.info("Leave: getAllProjects()");

        return response;
    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(Project project) {

        LOG.info("Enter: saveAndUpdate()");
        Response response = projectService.saveProject(project);
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
        Response response = projectService.deleteById(id);
        LOG.info("Leave: deleteById()");

        return response;
    }
}
