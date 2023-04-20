package org.sgitario.accountmanager.services;

import java.util.List;

import org.sgitario.accountmanager.entities.Group;
import org.sgitario.accountmanager.entities.GroupRule;
import org.sgitario.accountmanager.entities.Movement;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GroupTransactionService {

    public Group findGroupBySubject(String subject, List<Group> groups) {
        for (Group group : groups) {
            if (matchesGroupWithSubject(group, subject)) {
                return group;
            }
        }

        return null;
    }

    public void updatePendingTransactionsWithGroup(Group group) {
        for (Movement movement : Movement.listAllWithoutGroup()) {
            if (matchesGroupWithSubject(group, movement.subject)) {
                movement.group = group;
                movement.persist();
            }
        }
    }

    private boolean matchesGroupWithSubject(Group group, String subject) {
        for (GroupRule rule : group.rules) {
            if (subject.contains(rule.expression) || subject.matches(rule.expression)) {
                return true;
            }
        }

        return false;
    }
}
