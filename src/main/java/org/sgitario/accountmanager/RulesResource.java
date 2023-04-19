package org.sgitario.accountmanager;

import java.net.URI;
import java.util.HashSet;
import java.util.Optional;

import org.jboss.resteasy.reactive.RestPath;
import org.sgitario.accountmanager.entities.Group;
import org.sgitario.accountmanager.entities.GroupRule;
import org.sgitario.accountmanager.requests.GroupRequest;
import org.sgitario.accountmanager.templates.Templates;

import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/rules")
@Blocking
@Produces(MediaType.TEXT_HTML)
public class RulesResource {

    @GET
    public TemplateInstance get() {
        return Templates.rules(Group.listAll());
    }

    @GET
    @Path("/new")
    public TemplateInstance newGroupForm() {
        return Templates.rulesForm(Optional.empty());
    }

    @Transactional
    @POST
    @Path("/new")
    public Response doNewGroupForm(GroupRequest request) {
        Group group = new Group();
        group.name = request.name;
        group.rules = new HashSet<>();
        if (request.expressions != null) {
            for (String expression : request.expressions) {
                GroupRule rule = new GroupRule();
                rule.expression = expression;
                rule.group = group;
            }
        }
        group.persist();

        return Response.created(URI.create("/groups/" + group.id)).entity("Created new group with ID: " + group.id).build();
    }

    @Transactional
    @DELETE
    @Path("/{id}")
    public TemplateInstance deleteGroup(@RestPath long id) {
        Group.deleteById(id);
        return get();
    }
}
