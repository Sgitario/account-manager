package org.sgitario.accountmanager.responses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.sgitario.accountmanager.entities.Movement;

public class Result {

    private static long UUID = 0;

    private final long id;
    private final List<Movement> movements = new ArrayList<>();
    private double sum = 0;

    public Result() {
        id = ++UUID;
    }

    public long getId() {
        return id;
    }

    public void add(Movement movement) {
        movements.add(movement);
        sum += movement.quantity;
    }

    public double getSum() {
        return sum;
    }

    public List<Movement> getMovements() {
        return movements.stream().sorted(Comparator.comparing(Movement::getSubject)).collect(Collectors.toList());
    }

    public boolean hasMovements() {
        return !movements.isEmpty();
    }
}
