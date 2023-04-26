package org.sgitario.accountmanager;

import java.util.List;

import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.responses.Report;
import org.sgitario.accountmanager.templates.Templates;

import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Blocking
@Path("/")
public class DashboardResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        Report report = new Report();
        List<Movement> movements = Movement.listAll();
        for (Movement movement : movements) {
            report.addToMonth(movement);
        }

        return Templates.Page.reports(report);
    }
}
