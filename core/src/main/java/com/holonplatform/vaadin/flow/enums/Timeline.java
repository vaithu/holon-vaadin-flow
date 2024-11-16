package com.holonplatform.vaadin.flow.enums;

import com.holonplatform.vaadin.flow.internal.DateRange;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

public enum Timeline {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    THIS_QUARTER("This Quarter"),
    THIS_YEAR("This Year"),
    PREVIOUS_WEEK("Previous Week"),
    PREVIOUS_MONTH("Previous Month"),
    PREVIOUS_QUARTER("Previous Quarter"),
    PREVIOUS_YEAR("Previous Year"),
    CUSTOM("Custom");

    private final String displayValue;

    Timeline(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public DateRange getDateRange() {
        LocalDate start, end;

        end = switch (this) {
            case TODAY -> {
                start = LocalDate.now();
                yield start;
            }
            case THIS_WEEK -> {
                start = LocalDate.now().with(ChronoField.DAY_OF_WEEK, 1);
                yield LocalDate.now().with(ChronoField.DAY_OF_WEEK, 7);
            }
            case THIS_MONTH -> {
                start = LocalDate.now().withDayOfMonth(1);
                yield LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            }
            case THIS_QUARTER -> {
                start = LocalDate.now().minusMonths(LocalDate.now().getMonthValue() % 3).withDayOfMonth(1);
                yield start.plusMonths(2).withDayOfMonth(start.lengthOfMonth());
            }
            case THIS_YEAR -> {
                start = LocalDate.now().withDayOfYear(1);
                yield LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
            }
            case PREVIOUS_WEEK -> {
                start = LocalDate.now().with(ChronoField.DAY_OF_WEEK, 1).minusWeeks(1);
                yield LocalDate.now().with(ChronoField.DAY_OF_WEEK, 7).minusWeeks(1);
            }
            case PREVIOUS_MONTH -> {
                start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                yield LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth());
            }
            case PREVIOUS_QUARTER -> {
                start = LocalDate.now().minusMonths(LocalDate.now().getMonthValue() % 3 + 3).withDayOfMonth(1);
                yield start.plusMonths(2).withDayOfMonth(start.lengthOfMonth());
            }
            case PREVIOUS_YEAR -> {
                start = LocalDate.now().withDayOfYear(1).minusYears(1);
                yield LocalDate.now().withDayOfYear(1).minusDays(1);
            }
            case CUSTOM -> {
                // Implement custom date calculation logic here
                start = null;
                yield null;
            }
            default -> {
                start = null;
                yield null;
            }
        };

        return new DateRange(start, end);
    }

/*

    public static void main(String[] args) {
        Timeline selectedTimeline = Timeline.THIS_WEEK;
        DateRange dateRange = selectedTimeline.getDateRange();

        System.out.println("Selected Timeline: " + selectedTimeline.getDisplayValue());
        System.out.println("Start Date: " + dateRange.start());
        System.out.println("End Date: " + dateRange.end());
    }*/
}
