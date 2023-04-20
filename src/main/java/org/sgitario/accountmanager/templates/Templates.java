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
        public static native TemplateInstance transactions(List<Profile> profiles, Map<String, Object> optional);
        public static native TemplateInstance transactionsList(List<Movement> transactions, List<Group> groups);
        public static native TemplateInstance rules(List<Group> groups);
    }

    @CheckedTemplate(basePath = "fragments", requireTypeSafeExpressions = false)
    public static class Fragments {
        public static native TemplateInstance rulesForm(RulesFormOptional optional);
        public static native TemplateInstance transactionsForm(List<Profile> profiles, Map<String, Object> optional);
        public static native TemplateInstance transactionsList(List<Movement> transactions, List<Group> groups);
    }

    public static class RulesFormOptional {
        public Group group;
        public String with;
        public Long assignTo;
    }
}
