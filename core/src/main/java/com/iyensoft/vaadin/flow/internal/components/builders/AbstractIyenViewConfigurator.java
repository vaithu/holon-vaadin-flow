package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenViewConfigurator;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;

public abstract class AbstractIyenViewConfigurator<C extends IyenViewConfigurator<C>>
    extends AbstractComponentConfigurator<Layout,C>
        implements IyenViewConfigurator<C> {

    /**
     * Constructor.
     *
     * @param layout The layout instance (not null)
     */
    public AbstractIyenViewConfigurator(Layout layout) {
        super(layout);
    }

    public AbstractIyenViewConfigurator() {
        super(new Layout());
        configureLayout();
    }

    private void configureLayout() {
        getComponent().setId("Root Layout");
        getComponent().setSizeFull();
        getComponent().addClassNames(LumoUtility.Padding.SMALL, LumoUtility.FlexDirection.ROW);
    }

    @Override
    public C mobile(IyenMasterBuilder master) {
        getComponent().addComponentAsFirst(master.build());
        return getConfigurator();
    }

    @Override
    public C desktop(IyenMasterBuilder master, IyenDetailBuilder detail) {
        getComponent().addComponentAsFirst(master.build());
        getComponent().add(detail.build());
        return getConfigurator();
    }

    @Override
    public C separator() {
        return separator(SeparatorColor.ERROR);
    }

    @Override
    public C separator(SeparatorColor  separatorColor) {
        if (getComponent().getComponentCount() > 2) {
            getComponent().addComponentAtIndex(1,UIUtils.separator(separatorColor));
        } else {
            getComponent().add(UIUtils.separator(separatorColor));
        }

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
