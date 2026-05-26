package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ZohoViewBuilder;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public class DefaultZohoViewBuilder
        extends AbstractZohoViewConfigurator<ZohoViewBuilder>
        implements ZohoViewBuilder {

    private final Layout rootLayout;


    /**
     * Constructor.
     *
     * @param rootLayout The rootLayout (not null)
     */
    public DefaultZohoViewBuilder(Layout rootLayout) {
        super(rootLayout);
        this.rootLayout = rootLayout;
    }


    @Override
    public ZohoMasterBuilder<ZohoViewBuilder> master() {
        DefaultZohoMasterBuilder<ZohoViewBuilder> masterBuilder = new DefaultZohoMasterBuilder<>(this);
        rootLayout.addComponentAsFirst(masterBuilder.build().getLayout());
        return masterBuilder;
    }



    @Override
    public ZohoDetailBuilder<ZohoViewBuilder> detail() {
        DefaultZohoDetailsBuilder<ZohoViewBuilder> detailsBuilder = new DefaultZohoDetailsBuilder<>(this);
        rootLayout.add(detailsBuilder.build().getLayout());
        return detailsBuilder;
    }



    protected DefaultZohoViewBuilder getConfigurator() {
        return this;
    }

    @Override
    public ZohoViewBuilder separator() {
        return  separator(SeparatorColor.CONTRAST_10);
    }

    @Override
    public ZohoViewBuilder separator(SeparatorColor  separatorColor) {
        rootLayout.add(
                UIUtils.separator(separatorColor)
        );

        return this;
    }

    @Override
    public Layout build() {
        return rootLayout;
    }


}
