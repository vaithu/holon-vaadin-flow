package com.holonplatform.vaadin.flow.components;

import com.vaadin.flow.component.Component;

public interface HasSplitLayoutView {

    Component masterContent();

    Component detailContent();
}
