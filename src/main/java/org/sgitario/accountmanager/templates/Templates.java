package org.sgitario.accountmanager.templates;

import java.util.List;
import java.util.Optional;

import org.sgitario.accountmanager.entities.Group;
import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.entities.Profile;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate
public class Templates {
    public static native TemplateInstance index(List<Profile> profiles);
    public static native TemplateInstance dashboard(List<Profile> profiles, String errorMessage);
    public static native TemplateInstance dashboardReport(List<Movement> movements);
    public static native TemplateInstance rules(List<Group> groups);
    public static native TemplateInstance rulesForm(Optional<Group> group);
}
