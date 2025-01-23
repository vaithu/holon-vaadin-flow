package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.FormFooter;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;
import java.util.function.Consumer;

public class DefaultFormFooter extends AbstractComponentConfigurator<Layout,DefaultFormFooter> implements FormFooter<DefaultFormFooter> {

    private final Button saveBtn;
    private final Button saveAndNewBtn;
    private final Button discardBtn;
    private final Button updateBtn;

    public DefaultFormFooter() {
        this(new Layout());
    }

    public DefaultFormFooter(Layout component) {
        super(component);
        getComponent().setId("FormFooter");
        getComponent().addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.FlexDirection.Breakpoint.Medium.ROW, LumoUtility.JustifyContent.BETWEEN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.AlignItems.STRETCH);

        saveBtn = Components.button()
                .text("Save")
                .primary()
//                .onClick(event -> saveBtnAction())
                .visible(true)
                .build();

        saveAndNewBtn = Components.button()
                .text("Save&New")
                .primary()
//                .onClick(event -> saveBtnAction())
                .visible(true)
                .build();

        discardBtn = Components.button()
                .text("Cancel")
//                .borderPrimary()
//                .onClick(event -> discardBtnAction())
                .visible(true)
                .build();

        updateBtn = Components.button()
                .text("Update")
                .primary()
//                .onClick(event -> updateBtnAction())
                .visible(false)
                .build();

    }

    @Override
    public DefaultFormFooter addAdditionalComponent(Component component) {
        component.getStyle().set("margin-left", "auto");
        getComponent().addComponentAtIndex(getComponent().getComponentCount() , component);
        return this;
    }

    @Override
    public Layout build() {
        return getComponent();
    }

    @Override
    public DefaultFormFooter saveBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        updateBtn.setVisible(false);
        saveBtn.setVisible(true);
        configurator.accept(ButtonConfigurator.configure(saveBtn));
        getComponent().addComponentAsFirst(saveBtn);
        return this;
    }

    @Override
    public DefaultFormFooter saveAndNewBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        updateBtn.setVisible(false);
        saveBtn.setVisible(true);
        configurator.accept(ButtonConfigurator.configure(saveAndNewBtn));
        getComponent().addComponentAsFirst(saveAndNewBtn);
        return this;
    }

    @Override
    public DefaultFormFooter discardBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        configurator.accept(ButtonConfigurator.configure(discardBtn));
        getComponent().addComponentAtIndex(getComponent().getComponentCount(),discardBtn);
        return this;
    }

    @Override
    public DefaultFormFooter updateBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        saveBtn.setVisible(false);
        updateBtn.setVisible(true);
        configurator.accept(ButtonConfigurator.configure(updateBtn));
        getComponent().addComponentAsFirst(updateBtn);
        return this;
    }

    /**
     * If the component supports {@link HasSize}, return the component as {@link HasSize}.
     *
     * @return Optional component as {@link HasSize}, if supported
     */
    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasStyle}, return the component as {@link HasStyle}.
     *
     * @return Optional component as {@link HasStyle}, if supported
     */
    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasEnabled}, return the component as {@link HasEnabled}.
     *
     * @return Optional component as {@link HasEnabled}, if supported
     */
    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasTooltip}, return the component as {@link HasTooltip}.
     *
     * @return Optional component as {@link HasTooltip}, if supported
     */
    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected DefaultFormFooter getConfigurator() {
        return this;
    }
}
