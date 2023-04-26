package org.sgitario.accountmanager.responses;

import java.util.ArrayList;
import java.util.List;

import org.sgitario.accountmanager.entities.Movement;

public class Result {
    private final List<Movement> movements = new ArrayList<>();
    private double sum = 0;

    public void add(Movement movement) {
        movements.add(movement);
        sum += movement.quantity * -1;
    }

    public double getSum() {
        return sum;
    }

    public boolean hasMovements() {
        return !movements.isEmpty();
    }
}
