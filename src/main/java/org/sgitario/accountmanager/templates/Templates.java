package org.sgitario.accountmanager.templates;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sgitario.accountmanager.entities.Group;
import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.entities.Profile;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

public class Templates {

    @CheckedTemplate(basePath = "", requireTypeSafeExpressions = false)
    public static class Page {
        public static native TemplateInstance index();
        public static native TemplateInstance transactions(List<Profile> profiles, Map<String, Object> current);
        public static native TemplateInstance transactionsList(List<Movement> transactions);
        public static native TemplateInstance rules(List<Group> groups);
    }

    @CheckedTemplate(basePath = "fragments", requireTypeSafeExpressions = false)
    public static class Fragments {
        public static native TemplateInstance rulesForm(Optional<Group> group);
        public static native TemplateInstance transactionsForm(List<Profile> profiles, Map<String, Object> current);
        public static native TemplateInstance transactionsList(List<Movement> transactions);
    }
}
