package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.enums.Timeline;
import com.holonplatform.vaadin.flow.internal.DateRange;
import com.holonplatform.vaadin.flow.internal.DateRangeField;
import com.vaadin.flow.component.Component;

public class DefaultTimeline {

    private final SingleSelect<Timeline> timelineInput;
    private DateRange dateRange;

    public DefaultTimeline() {
        timelineInput = Components.input
                .enumSelect(Timeline.class)
                .itemCaptionGenerator(Timeline::getDisplayValue)
                .withValueChangeListener(event -> decideDateRange(event.getValue()))
                .allowCustomValue(false)
                .build();

    }

    public Component getTimeline() {
        return timelineInput.getComponent();
    }

    private void decideDateRange(Timeline timeline) {
        if (timeline.equals(Timeline.CUSTOM) || timeline.getDisplayValue().equalsIgnoreCase(Timeline.CUSTOM.getDisplayValue())) {
            DateRangeField dateRangeField = new DateRangeField();

            Components.dialog
                    .question(confirmSelected -> {
                        if (confirmSelected) {
                            setDateRange(dateRangeField.getValue());
                        }
                    })
                    .withComponent(dateRangeField)
                    .open();
        } else {
            setDateRange(timeline.getDateRange());
        }
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }
}
