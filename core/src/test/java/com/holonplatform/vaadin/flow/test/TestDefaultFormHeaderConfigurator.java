package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFormHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDefaultFormHeaderConfigurator {

    @Test
    public void testTitle() {

        DefaultFormHeader header = new DefaultFormHeader("Hello");
        assertThat(header).isNotNull();

        LabelBuilder<H4> title = LabelBuilder.h4().text("Hello");
        header.title(title);

        assertThat(header.getTitle()).isEqualTo(title);

        assertThat(header.build()).isInstanceOf( HorizontalLayout.class);
    }

    @Test
    void setPrefix() {
    }

    @Test
    void setBreadcrumb() {
    }

    @Test
    void setHeading() {
    }

    @Test
    void testSetHeading() {
    }

    @Test
    void setHeadingFontSize() {
    }

    @Test
    void setHeadingFontWeight() {
    }

    @Test
    void setHeadingId() {
    }

    @Test
    void setHeadingLineHeight() {
    }

    @Test
    void setHeadingTextColor() {
    }

    @Test
    void setDetails() {
    }

    @Test
    void addActions() {
    }

    @Test
    void setActions() {
    }

    @Test
    void getRowLayout() {
    }

    @Test
    void getColumnLayout() {
    }

    @Test
    void getTabs() {
    }

    @Test
    void setTabs() {
        Tab tab = new Tab("Overview");
        Tabs tabs = Components.tabs()
                .add(tab)
                .build();

        final Header header = Components.header("Test")
                .tabs(tabs)
                .build();

        assertThat(header).isNotNull();
        assertThat(header.getTabs().getTabCount()).isEqualTo(1);
    }

    @Test
    void testSetTabs() {
    }
}
