package org.sgitario.accountmanager.templates;

import java.util.List;
import java.util.Optional;

import org.sgitario.accountmanager.entities.Group;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate
public class Templates {
    public static native TemplateInstance index();
    public static native TemplateInstance rules(List<Group> groups);
    public static native TemplateInstance rulesForm(Optional<Group> group);
}
