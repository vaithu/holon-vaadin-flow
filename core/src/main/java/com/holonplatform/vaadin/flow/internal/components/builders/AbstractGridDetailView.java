package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.GridDetailView;
import com.holonplatform.vaadin.flow.components.builders.FormHeaderBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.vaadin.flow.component.html.H3;

public abstract class AbstractGridDetailView<T> implements GridDetailView<T> {

    private LabelBuilder<H3> formHeaderLabelBuilder;
//    private final VerticalLayout detailMenuBody;

    public AbstractGridDetailView() {
     /*   detailMenuBody = Components.vl()
                .id("detail-body")
                .build();*/
    }

    @Override
    public void updateDetailHeaderLabel(String label) {
        formHeaderLabelBuilder.text(label);
    }

    @Override
    public LabelBuilder<H3> createFormHeaderLabelBuilder() {
        return Components.h3();
    }

    @Override
    public LabelBuilder<H3> getDetailHeaderLabel() {
        ObjectUtils.argumentNotNull(formHeaderLabelBuilder, "FormHeader is not already set so it is null here");
        return formHeaderLabelBuilder;
    }

    /*@Override
    public VerticalLayout createDetailContent() {
        return Components.vl()
                .id("DetailContent")
                .fullWidth()
                .addComponentAsFirst(createDetailMenuBar())
                .content(getDetailMenuBody())
                .withoutPadding()
                .withoutSpacing()
                .build();
    }*/

   /* @Override
    public VerticalLayout getDetailMenuBody() {
        detailMenuBody.removeAll();
        return detailMenuBody;
    }*/

    @Override
    public FormHeaderBuilder createDetailHeader() {
        return Components.formHeader()
                .styleNames("padding-none")
                .title(formHeaderLabelBuilder = createFormHeaderLabelBuilder())
                .additionalItems(addAdditionalOptionsToDetailHeader())
                .editBtnConfigurator(baseButtonConfigurator -> {
                    baseButtonConfigurator.text(Localizable.of("Edit", "edit.code"));
                    baseButtonConfigurator.onClick(buttonClickEvent -> {
                        edit();
                    });
                })
                .horizontalRule()
                .closeBtnConfigurator(baseButtonConfigurator -> {
                    close();
                });

    }


}
