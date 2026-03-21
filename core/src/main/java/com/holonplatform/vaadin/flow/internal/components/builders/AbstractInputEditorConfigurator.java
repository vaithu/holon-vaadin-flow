package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasInputEditorConfigurator;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractInputEditorConfigurator<C extends HasInputEditorConfigurator <C>>
        extends AbstractComponentConfigurator<HorizontalLayout,C>
        implements HasInputEditorConfigurator<C> {

    private final Button saveBtn ;
    private final Button editBtn ;
    private final Button cancelBtn ;

    /**
     * Constructor.
     *
     * @param horizontalLayout The component instance (not getConfigurator())
     */
    public AbstractInputEditorConfigurator(HorizontalLayout horizontalLayout) {
        super(horizontalLayout);
        saveBtn = ButtonBuilder.create()
                .tertiaryInline()
                .icon(smallIcon("lumo","checkmark",LumoUtility.TextColor.SUCCESS))
                .onClick(event -> showEditButtons(true))
                .build();

        editBtn = ButtonBuilder.create()
                .tertiaryInline()
                .icon(smallIcon("lumo", "edit",LumoUtility.TextColor.PRIMARY))
                .onClick(event -> showEditButtons(false))
                .build();

        cancelBtn = ButtonBuilder.create()
                .tertiaryInline()
                .icon(smallIcon("lumo", "cross",LumoUtility.TextColor.ERROR))
                .onClick(event -> showEditButtons(true))
                .build();

        horizontalLayout.add(saveBtn,editBtn,cancelBtn);
        showEditButtons(true);

    }

    private Icon smallIcon(String collection, String name,String color) {
        Icon icon = new Icon(collection, name);
        icon.addClassNames(LumoUtility.IconSize.SMALL,color);
        return icon;
    }

    private void showEditButtons(boolean visible) {
        saveBtn.setVisible(!visible);
        editBtn.setVisible(visible);
        cancelBtn.setVisible(!visible);
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


    @Override
    public C cancelBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(cancelBtn));
        return getConfigurator();
    }

    @Override
    public C saveBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(saveBtn));
        return getConfigurator();
    }

    @Override
    public C editBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(editBtn));
        return getConfigurator();
    }
}

