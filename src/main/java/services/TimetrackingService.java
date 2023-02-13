package services;

import de.fhswf.api.TimetrackingResource;
import de.fhswf.entities.Timetracking;
import de.fhswf.response.TimetrackingResponse;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class TimetrackingService {

    private static final Logger LOG = Logger.getLogger(TimetrackingResource.class);

    @Inject
    JsonWebToken jwt;


    public Response getAllTimetrackings(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date searchdate = formatter.parse(date);
            String userId = jwt.getClaim(Claims.sub);
            List<Timetracking> times = Timetracking.list("userId = ?1 and to_char(timestamp,'dd-MM-yyyy') = ?2 order by fromTime", userId, date);
            TimetrackingResponse responseObject = new TimetrackingResponse();
            responseObject.total = calculateTotal(times);
            responseObject.timetrackings = times;
            return Response.ok(responseObject).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @Transactional
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
                    entity.timestamp = timetracking.get(i).timestamp;
                }
            }
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @Transactional
    public Response deleteById(Long id) {
        boolean deleted = Timetracking.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    public String calculateTotal(List<Timetracking> timetrackings) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        long diff = 0;
        for (int i = 0; i < timetrackings.size(); i++) {
            Date from = formatter.parse(timetrackings.get(i).fromTime);
            Date to = formatter.parse(timetrackings.get(i).toTime);

            diff += to.getTime() - from.getTime();
            LOG.info(diff);
        }

        return formatDiffFromMilliseconds(diff);
    }

    public String formatDiffFromMilliseconds(long diff) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(diff),
                TimeUnit.MILLISECONDS.toMinutes(diff) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(diff) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
    }
}
