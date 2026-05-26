package com.holonplatform.vaadin.flow.internal;

import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;

public class DateRangeField extends CustomField<DateRange> {

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    public DateRangeField(String label) {
        this();
        setLabel(label);
    }

    public DateRangeField() {
        startDatePicker = new DatePicker();
        startDatePicker.setPlaceholder("Start date");

        endDatePicker = new DatePicker();
        endDatePicker.setPlaceholder("End date");

        add(startDatePicker, Components.span().text(" – ").build(), endDatePicker);
    }

    @Override
    protected DateRange generateModelValue() {
        return new DateRange(startDatePicker.getValue(), endDatePicker.getValue());
    }

    @Override
    protected void setPresentationValue(DateRange dateRange) {
        startDatePicker.setValue(dateRange.start());
        endDatePicker.setValue(dateRange.end());
    }
}