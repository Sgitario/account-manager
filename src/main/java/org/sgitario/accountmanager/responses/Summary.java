package org.sgitario.accountmanager.responses;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.sgitario.accountmanager.entities.Movement;

public class Summary {
    private final Map<String, Result> resultByGroup = new HashMap<>();
    private final Result rest = new Result();

    public Collection<String> getGroups() {
        return resultByGroup.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Result getResultByGroup(String groupName) {
        return resultByGroup.get(groupName);
    }

    public Result getRest() {
        return rest;
    }

    public void addMovement(Movement movement) {
        if (movement.group == null) {
            rest.add(movement);
        } else {
            Result result = resultByGroup.get(movement.group.name);
            if (result == null) {
                 result = new Result();
                 resultByGroup.put(movement.group.name, result);
            }

            result.add(movement);
        }
    }
}
