package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;

public interface IyenViewConfigurator<C extends IyenViewConfigurator<C>> extends
        ComponentConfigurator<C>,
        HasStyleConfigurator<C>, HasSizeConfigurator<C> {

    C mobile(IyenMasterBuilder master);

    C desktop(IyenMasterBuilder master, IyenDetailBuilder  detail);

    C separator();

    C separator(SeparatorColor  separatorColor);

}
