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
import org.jboss.resteasy.reactive.RestPath;
import org.sgitario.accountmanager.entities.Group;
import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.entities.Profile;
import org.sgitario.accountmanager.services.GroupTransactionService;
import org.sgitario.accountmanager.templates.Templates;

import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Blocking
@Path("/transactions")
public class TransactionsResource {

    @Inject
    GroupTransactionService service;

    @GET
    @Path("/upload")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance uploadTransactionsForm() {
        return Templates.Page.transactions(Profile.listAll(), Collections.emptyMap());
    }

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getAllTransactions() {
        return Templates.Page.transactionsList(Movement.listAll(), Group.listAll());
    }

    @Transactional
    @DELETE
    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance deleteAllTransactions() {
        Movement.deleteAll();
        return Templates.Fragments.transactionsList(Movement.listAll(), Group.listAll());
    }

    @GET
    @Path("/pending")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getPendingTransactions() {
        return Templates.Page.transactionsList(Movement.listAllWithoutGroup(), Group.listAll());
    }

    @Transactional
    @DELETE
    @Path("/pending")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance deletePendingTransactions() {
        List<Movement> movements = Movement.listAllWithoutGroup();
        for (Movement movement : movements) {
            movement.delete();
        }

        return Templates.Fragments.transactionsList(Collections.emptyList(), Group.listAll());
    }

    @Transactional
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance doUploadTransactionsForm(@RestForm(value = "profile") long profileId,
            @RestForm @PartType(MediaType.APPLICATION_OCTET_STREAM) File file) {

        Profile profile = Profile.findById(profileId);
        List<Group> groups = Group.listAll();
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))) {
            var sheet = workbook.getSheetAt(0);
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
                movement.group = service.findGroupBySubject(subject, groups);
                movement.id = Objects.hash(subject, movement.quantity, movement.accountingDate, movement.valueDate);
                if (Movement.findByIdOptional(movement.id).isEmpty()) {
                    movement.persist();
                }

                index++;
            }

            return Templates.Fragments.transactionsList(Movement.listAllWithoutGroup(), Group.listAll());
        } catch (UnsupportedFileFormatException e) {
            return Templates.Fragments.transactionsForm(Profile.listAll(),
                    Map.of("profile", profileId, "errorMessage", "Unsupported file!"));
        } catch (Exception e) {
            return Templates.Fragments.transactionsForm(Profile.listAll(),
                    Map.of("profile", profileId, "errorMessage", "Error reading file: " + e.getMessage()));
        }
    }

    @Transactional
    @POST
    @Path("/{id}/group")
    public void transactionGroupForm(@RestPath long id, @FormParam("group") long groupId) {
        Movement transaction = Movement.findById(id);
        transaction.group = Group.findById(groupId);
        transaction.persist();
    }

    @Transactional
    @DELETE
    @Path("/{id}")
    public void deleteTransaction(@RestPath long id) {
        Movement.deleteById(id);
    }
}
