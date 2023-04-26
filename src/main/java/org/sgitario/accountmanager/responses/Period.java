package org.sgitario.accountmanager.responses;

import java.util.Objects;

public class Period {

    private final int year;
    private final int month;

    public Period(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public String getId() {
        return getMonthName() + year;
    }

    public String getName() {
        return getMonthName() + " " + year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Period))
            return false;
        Period period = (Period) o;
        return year == period.year && month == period.month;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month);
    }

    private String getMonthName() {
        switch (month) {
            case 0:
                return "Enero";
            case 1:
                return "Febrero";
            case 2:
                return "Marzo";
            case 3:
                return "Abril";
            case 4:
                return "Mayo";
            case 5:
                return "Junio";
            case 6:
                return "Julio";
            case 7:
                return "Agosto";
            case 8:
                return "Septiembre";
            case 9:
                return "Octubre";
            case 10:
                return "Noviembre";
            case 11:
                return "Diciembre";
        }

        throw new RuntimeException("Unrecognised month with value '" + month + "'");
    }
}
