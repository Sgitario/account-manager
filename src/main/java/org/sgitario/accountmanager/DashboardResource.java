package org.sgitario.accountmanager;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.entities.Profile;
import org.sgitario.accountmanager.templates.Templates;

import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Blocking
@Path("/")
public class DashboardResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return Templates.index(Profile.listAll());
    }

    @Transactional
    @POST
    @Path("/dashboard/load")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance loadFile(@RestForm(value = "profile") long profileId,
            @RestForm @PartType(MediaType.APPLICATION_OCTET_STREAM) File file) {

        Profile profile = Profile.findById(profileId);
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))) {
            var sheet = workbook.getSheetAt(0);
            List<Movement> movements = new ArrayList<>();
            int index = 1;
            while (index < sheet.getPhysicalNumberOfRows()) {
                var row = sheet.getRow(index);
                String subject = row.getCell(profile.columnSubject).getStringCellValue();
                if (subject == null || subject.isEmpty()) {
                    // we're done!
                    break;
                }

                Movement movement = new Movement();
                movement.subject = subject;
                movement.accountingDate = row.getCell(profile.columnAccountingDate).getDateCellValue();
                movement.valueDate = row.getCell(profile.columnValueDate).getDateCellValue();
                movement.quantity = row.getCell(profile.columnQuantity).getNumericCellValue();
                movement.profile = profile;
                movement.id = Objects.hash(subject, movement.quantity, movement.accountingDate, movement.valueDate);
                if (Movement.findByIdOptional(movement.id).isEmpty()) {
                    movement.persist();
                }

                movements.add(movement);
                index++;
            }

            return Templates.dashboardReport(movements);
        } catch (UnsupportedFileFormatException e) {
            return Templates.dashboard(Profile.listAll(), "Unsupported file!");
        } catch (Exception e) {
            return Templates.dashboard(Profile.listAll(), "Error reading file: " + e.getMessage());
        }
    }

}
