package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FooterTest {

    @Test
    void footer_builderSetsSemanticRoleAndSections() {
        Footer footer = Components.footer()
                .prefix(new Span("Brand"))
                .details(new Span("About"), new Span("Help"))
                .actions(new Button("Contact"))
                .meta(new Span("© 2026"))
                .legal(new Span("Privacy"))
                .background(Color.Background.CONTRAST_10)
                .build();

        assertThat(footer.getElement().getAttribute("role")).isEqualTo("contentinfo");
        assertThat(footer.getPrefixComponents()).hasSize(1);
        assertThat(footer.getDetailsComponents()).hasSize(2);
        assertThat(footer.getActionsComponents()).hasSize(1);
        assertThat(footer.getMetaComponents()).hasSize(1);
        assertThat(footer.getLegalComponents()).hasSize(1);
        assertThat(footer.getClassNames()).contains(Color.Background.CONTRAST_10.getClassName());
    }

    @Test
    void footer_canToggleBorderStyle() {
        Footer footer = new Footer();

        footer.withoutBorder();

        assertThat(footer.getClassNames()).contains("footer--no-border");
        assertThat(footer.getClassNames()).doesNotContain("iyen-footer--bordered");
    }
}