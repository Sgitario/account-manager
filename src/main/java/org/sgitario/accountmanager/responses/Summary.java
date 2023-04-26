package org.sgitario.accountmanager.responses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sgitario.accountmanager.entities.Movement;

public class Summary {
    private final Map<String, Result> resultByGroup = new HashMap<>();
    private final Result rest = new Result();

    public Map<String, Result> getResultByGroup() {
        return resultByGroup;
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
