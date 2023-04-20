package org.sgitario.accountmanager;

import java.net.URI;

import org.jboss.resteasy.reactive.RestPath;
import org.sgitario.accountmanager.entities.Group;
import org.sgitario.accountmanager.entities.GroupRule;
import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.requests.GroupRequest;
import org.sgitario.accountmanager.services.GroupTransactionService;
import org.sgitario.accountmanager.templates.Templates;

import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/rules")
@Blocking
@Produces(MediaType.TEXT_HTML)
public class RulesResource {

    @Inject
    GroupTransactionService service;

    @GET
    public TemplateInstance get() {
        return Templates.Page.rules(Group.listAll());
    }

    @GET
    @Path("/new")
    public TemplateInstance newGroupForm(@QueryParam("with") String with, @QueryParam("assignTo") Long assignTo) {
        Templates.RulesFormOptional optional = new Templates.RulesFormOptional();
        optional.with = with;
        optional.assignTo = assignTo;
        return Templates.Fragments.rulesForm(optional);
    }

    @GET
    @Path("/{id}")
    public TemplateInstance editGroupForm(@RestPath long id) {
        Templates.RulesFormOptional optional = new Templates.RulesFormOptional();
        optional.group = Group.findById(id);
        return Templates.Fragments.rulesForm(optional);
    }

    @Transactional
    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response doNewGroupForm(GroupRequest request, @QueryParam("assignTo") Long assignTo) {
        Group group = new Group();
        saveOrUpdateGroup(request, group);

        if (assignTo != null) {
            Movement movement = Movement.findById(assignTo);
            movement.group = group;
            movement.persist();
        }

        service.updatePendingTransactionsWithGroup(group);

        return Response.created(URI.create("/groups/" + group.id)).entity("Created new group with ID: " + group.id).build();
    }

    @Transactional
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{id}")
    public Response doEditGroupForm(@RestPath long id, GroupRequest request) {
        Group group = Group.findById(id);
        saveOrUpdateGroup(request, group);
        service.updatePendingTransactionsWithGroup(group);

        return Response.ok(URI.create("/groups/" + group.id)).entity("Updated group with ID: " + group.id).build();
    }

    @Transactional
    @DELETE
    @Path("/{id}")
    public TemplateInstance deleteGroup(@RestPath long id) {
        Group group = Group.findById(id);
        for (Movement movement : Movement.listAllWithGroup(group)) {
            movement.group = null;
            movement.persist();
        }
        group.delete();
        return get();
    }

    private void saveOrUpdateGroup(GroupRequest request, Group group) {
        group.name = request.name;
        group.rules.clear();
        if (request.expressions != null) {
            for (String expression : request.expressions) {
                if (expression != null && !expression.isEmpty()) {
                    GroupRule rule = new GroupRule();
                    rule.expression = expression;
                    rule.group = group;
                    group.rules.add(rule);
                }
            }
        }

        group.persist();
    }
}
