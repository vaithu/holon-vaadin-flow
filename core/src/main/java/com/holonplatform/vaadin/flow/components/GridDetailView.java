package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.FormHeaderBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public interface GridDetailView<T> {

    void updateDetailHeaderLabel(T beanInstance);
    void updateDetailHeaderLabel(String label);

    LabelBuilder<H3> getDetailHeaderLabel();

    FormHeaderBuilder createDetailHeader();

    VerticalLayout createDetailContent();

    void setDetailContentMenuBody(String label);

//    VerticalLayout getDetailMenuBody();
    Component createDetailMenuBar();
    LabelBuilder<?> createFormHeaderLabelBuilder();

    Component addAdditionalOptionsToDetailHeader();
    Component createOverview();
    void edit();
    void close();

}
