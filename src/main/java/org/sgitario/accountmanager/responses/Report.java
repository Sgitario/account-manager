package org.sgitario.accountmanager.responses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sgitario.accountmanager.entities.Movement;

public class Report {
    private final Set<Integer> years = new HashSet<>();
    private final Set<Integer> months = new HashSet<>();
    private final Map<Period, Summary> summaryByMonth = new HashMap<>();

    public void addToMonth(Movement movement) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(movement.valueDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        Period period = new Period(year, month);

        Summary summary = summaryByMonth.get(period);
        if (summary == null) {
            summary = new Summary();
            summaryByMonth.put(period, summary);
        }

        summary.addMovement(movement);

        addYearAndMonthInOrder(year, month);
    }

    public Summary getSummaryByPeriod(Period period) {
        return summaryByMonth.get(period);
    }

    public Collection<Period> getPeriods() {
        List<Integer> allYears = new ArrayList<>(years);
        Collections.sort(allYears);

        List<Integer> allMonths = new ArrayList<>(months);
        Collections.sort(allMonths);

        List<Period> periodsInOrder = new ArrayList<>();
        for (Integer year : allYears) {
            for (Integer month : allMonths) {
                Period period = new Period(year, month);
                if (summaryByMonth.containsKey(period)) {
                    periodsInOrder.add(period);
                }
            }
        }

        return periodsInOrder;
    }

    private void addYearAndMonthInOrder(int year, int month) {
        years.add(year);
        months.add(month);
    }
}
