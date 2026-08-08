package com.holonplatform.vaadin.flow.internal;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
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
        startDatePicker.setPlaceholder(LocalizationProvider.localize("Start date", "date_range.start_placeholder"));

        endDatePicker = new DatePicker();
        endDatePicker.setPlaceholder(LocalizationProvider.localize("End date", "date_range.end_placeholder"));

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