package org.sgitario.accountmanager.templates;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sgitario.accountmanager.entities.Group;
import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.entities.Profile;
import org.sgitario.accountmanager.responses.Report;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

public class Templates {

    @CheckedTemplate(basePath = "", requireTypeSafeExpressions = false)
    public static class Page {
        public static native TemplateInstance reports(Report report);
        public static native TemplateInstance transactions(List<Profile> profiles,
                List<Movement> transactions,
                List<Group> groups,
                TransactionsOptional optional);
        public static native TemplateInstance rules(List<Group> groups);
    }

    @CheckedTemplate(basePath = "fragments", requireTypeSafeExpressions = false)
    public static class Fragments {
        public static native TemplateInstance rulesForm(RulesFormOptional optional);
        public static native TemplateInstance transactionsForm(List<Profile> profiles, TransactionsOptional optional);
        public static native TemplateInstance transactionsList(List<Movement> transactions, List<Group> groups, TransactionsOptional optional);
    }

    public static class RulesFormOptional {
        public Group group;
        public String with;
        public Long assignTo;
    }

    public static class TransactionsOptional {
        public boolean seeOnlyPending;
        public Long profile;
        public String errorMessage;

        public static TransactionsOptional listPending() {
            TransactionsOptional optional = new TransactionsOptional();
            optional.seeOnlyPending = true;
            return optional;
        }

        public static TransactionsOptional listAll() {
            TransactionsOptional optional = new TransactionsOptional();
            optional.seeOnlyPending = false;
            return optional;
        }

        public static TransactionsOptional form(Long profile, String errorMessage) {
            TransactionsOptional optional = new TransactionsOptional();
            optional.errorMessage = errorMessage;
            optional.profile = profile;
            return optional;
        }
    }
}
