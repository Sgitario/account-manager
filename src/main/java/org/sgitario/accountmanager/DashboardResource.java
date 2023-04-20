package org.sgitario.accountmanager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.sgitario.accountmanager.entities.Group;
import org.sgitario.accountmanager.entities.GroupRule;
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
        return Templates.Page.index();
    }

    @GET
    @Path("/transactions/upload")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance uploadTransactionsForm() {
        return Templates.Page.transactions(Profile.listAll(), Collections.emptyMap());
    }

    @GET
    @Path("/transactions/pending")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getPendingTransactions() {
        return Templates.Page.transactionsList(Movement.find("group is NULL").list());
    }

    @Transactional
    @POST
    @Path("/transactions/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance doUploadTransactionsForm(@RestForm(value = "profile") long profileId,
            @RestForm @PartType(MediaType.APPLICATION_OCTET_STREAM) File file) {

        Profile profile = Profile.findById(profileId);
        List<Group> groups = Group.listAll();
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))) {
            var sheet = workbook.getSheetAt(0);
            boolean anyMovementWithoutGroup = false;
            int index = 1;
            while (index < sheet.getPhysicalNumberOfRows()) {
                var row = sheet.getRow(index);
                String subject = row.getCell(profile.columnSubject).getStringCellValue();
                if (subject == null || subject.isEmpty()) {
                    // there are no more rows, finishing.
                    break;
                }

                Movement movement = new Movement();
                movement.subject = subject;
                movement.accountingDate = row.getCell(profile.columnAccountingDate).getDateCellValue();
                movement.valueDate = row.getCell(profile.columnValueDate).getDateCellValue();
                movement.quantity = row.getCell(profile.columnQuantity).getNumericCellValue();
                movement.profile = profile;
                movement.group = findGroupBySubject(subject, groups);
                movement.id = Objects.hash(subject, movement.quantity, movement.accountingDate, movement.valueDate);
                if (Movement.findByIdOptional(movement.id).isEmpty()) {
                    movement.persist();
                }

                if (movement.group == null) {
                    anyMovementWithoutGroup = true;
                }
                index++;
            }

            return Templates.Fragments.transactionsList(Movement.find("group is NULL").list());
        } catch (UnsupportedFileFormatException e) {
            return Templates.Fragments.transactionsForm(Profile.listAll(),
                    Map.of("profile", profileId, "errorMessage", "Unsupported file!"));
        } catch (Exception e) {
            return Templates.Fragments.transactionsForm(Profile.listAll(),
                    Map.of("profile", profileId, "errorMessage", "Error reading file: " + e.getMessage()));
        }
    }

    private Group findGroupBySubject(String subject, List<Group> groups) {
        for (Group group : groups) {
            for (GroupRule rule : group.rules) {
                if (subject.contains(rule.expression) || subject.matches(rule.expression)) {
                    return group;
                }
            }
        }

        return null;
    }

}
