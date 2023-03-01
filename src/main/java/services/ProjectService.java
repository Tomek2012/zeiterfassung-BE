package services;

import de.fhswf.api.TimetrackingResource;
import de.fhswf.entities.Project;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
public class ProjectService {

    private static final Logger LOG = Logger.getLogger(TimetrackingResource.class);

    public Response getAllProjects() {
        try {
            List<Project> projects = Project.listAll();
            return Response.ok(projects).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @Transactional
    public Response saveProject(Project project) {
        try {
            Project.persist(project);
            return Response.ok(project).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @Transactional
    public Response deleteById(Long id) {
        boolean deleted = Project.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
