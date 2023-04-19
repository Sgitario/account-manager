package org.sgitario.accountmanager;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.sgitario.accountmanager.templates.Templates;

import io.quarkus.qute.TemplateInstance;

@Path("/")
public class IndexResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return Templates.index();
    }

}
