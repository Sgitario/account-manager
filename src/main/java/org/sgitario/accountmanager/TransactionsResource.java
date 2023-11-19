package org.sgitario.accountmanager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.sgitario.accountmanager.entities.Group;
import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.entities.Profile;
import org.sgitario.accountmanager.services.GroupTransactionService;
import org.sgitario.accountmanager.services.MovementsFileReader;
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
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance transactionsPage() {
        return Templates.Page.transactions(Profile.listAll(),
                Movement.listAllWithoutGroup(),
                Group.listAll(),
                Templates.TransactionsOptional.listPending());
    }

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getAllTransactions() {
        return Templates.Page.transactions(Profile.listAll(),
                Movement.listAll(),
                Group.listAll(),
                Templates.TransactionsOptional.listAll());
    }

    @GET
    @Path("/pending")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getPendingTransactions() {
        return Templates.Page.transactions(Profile.listAll(),
                Movement.listAllWithoutGroup(),
                Group.listAll(),
                Templates.TransactionsOptional.listPending());
    }

    @Transactional
    @DELETE
    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance deleteAllTransactions() {
        Movement.deleteAll();
        return Templates.Fragments.transactionsList(Movement.listAll(), Group.listAll(),
                Templates.TransactionsOptional.listAll());
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

        return Templates.Fragments.transactionsList(Collections.emptyList(), Group.listAll(),
                Templates.TransactionsOptional.listPending());
    }

    @Transactional
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance doUploadTransactionsForm(@RestForm(value = "profile") long profileId,
            @RestForm FileUpload file) {

        Profile profile = Profile.findById(profileId);
        List<Group> groups = Group.listAll();
        try (var reader = MovementsFileReader.readerFor(file, profile)) {
            while (reader.hasNext()) {
                Movement movement = reader.next();
                movement.group = service.findGroupBySubject(movement.subject, groups);
                movement.id = Objects.hash(movement.subject, movement.quantity, movement.accountingDate, movement.valueDate);
                if (Movement.findByIdOptional(movement.id).isEmpty()) {
                    movement.persist();
                }
            }

            return Templates.Fragments.transactionsList(Movement.listAllWithoutGroup(), Group.listAll(),
                    Templates.TransactionsOptional.listPending());
        } catch (UnsupportedFileFormatException e) {
            return Templates.Fragments.transactionsForm(Profile.listAll(),
                    Templates.TransactionsOptional.form(profileId, "Unsupported file!"));
        } catch (Exception e) {
            return Templates.Fragments.transactionsForm(Profile.listAll(),
                    Templates.TransactionsOptional.form(profileId, "Error reading file: " + e.getMessage()));
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
