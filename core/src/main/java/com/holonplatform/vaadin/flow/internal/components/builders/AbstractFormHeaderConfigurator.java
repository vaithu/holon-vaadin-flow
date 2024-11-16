package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.FormHeaderConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractFormHeaderConfigurator<C extends FormHeaderConfigurator<C>>
        extends AbstractComponentConfigurator<HorizontalLayout, C>
        implements FormHeaderConfigurator<C> {

    private LabelBuilder<?> title;
    private final FlexLayout rightSide = new FlexLayout();


    
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractFormHeaderConfigurator(HorizontalLayout component) {
        super(component);
        rightSide.addClassNames(LumoUtility.FlexDirection.ROW, LumoUtility.Overflow.HIDDEN, LumoUtility.Gap.MEDIUM);

        Components.configure(getComponent())
                .spacing()
                .alignItems(FlexComponent.Alignment.BASELINE)
                .fullWidth()
                .add(rightSide)

                .justifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
    }

    @Override
    public LabelBuilder<?> getTitle() {
        ObjectUtils.argumentNotNull(title, "Title is null here because it is not already set");
        return title;
    }

    @Override
    public C title(LabelBuilder<?> title) {
        this.title = title;
//        this.title.styleNames( LumoUtility.Padding.SMALL);
        getComponent().addComponentAsFirst(this.title.build());
        return getConfigurator();
    }

    @Override
    public C title(String title) {
        return title(LabelBuilder.h4().text(title));
    }

    @Override
    public C editBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        if (configurator != null) {
            Button button = Components.button()
                    .icon("lumo", "edit")
                    .tooltip("Edit")
                    .build();
            configurator.accept(ButtonConfigurator.configure(button));
            rightSide.add(button);
        }
        return getConfigurator();
    }

    @Override
    public C closeBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        if (configurator != null) {
            Button button = Components.button()
                    .icon("lumo", "cross")
                    .tooltip("Close")
                    .styleNames(LumoUtility.Margin.Left.AUTO)
                    .tertiaryInline()
                    .build();
            configurator.accept(ButtonConfigurator.configure(button));
            rightSide.add(button);
        }
        return getConfigurator();
    }

    @Override
    public C optionsBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        if (configurator != null) {
            Button button = Components.button()
                    .icon(VaadinIcon.ELLIPSIS_DOTS_V)
                    .tooltip("Options")
                    .tertiaryInline()
                    .build();
            configurator.accept(ButtonConfigurator.configure(button));
            rightSide.add(button);
        }
        return getConfigurator();
    }

    @Override
    public C additionalItems(Component... components) {
        rightSide.add(components);
        return getConfigurator();
    }

    @Override
    public C horizontalRule() {
        getComponent().addClassNames(LumoUtility.Border.BOTTOM, LumoUtility.BorderColor.CONTRAST_30);
        return getConfigurator();
    }

    @Override
    public C title(Component component) {
        getComponent().addComponentAsFirst(component);
        return getConfigurator();
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

    
}
