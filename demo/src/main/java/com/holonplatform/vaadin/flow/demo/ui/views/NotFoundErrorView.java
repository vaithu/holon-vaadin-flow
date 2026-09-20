package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.NotFoundPage;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.server.HttpStatusCode;

/**
 * Application-wide 404 screen, built on the {@link NotFoundPage} component.
 *
 * <p>Registering it as the {@link NotFoundException} error handler makes it the target for
 * <em>every</em> unresolved navigation — an unknown route, and any view that explicitly
 * rejects its own parameters with
 * {@code BeforeEnterEvent.rerouteToError(NotFoundException.class, ...)}. The latter is how
 * {@link CustomerMasterDetailMaterialView} handles a {@code ?id=} deep link pointing at a
 * product that no longer exists.</p>
 *
 * <p>Error views must not declare a {@code @Route}; Vaadin resolves them by the exception
 * type instead. The HTTP status code returned here is what the browser (and any crawler or
 * monitoring probe) actually sees.</p>
 */
@ParentLayout(DemoMainLayout.class)
public class NotFoundErrorView extends NotFoundPage implements HasErrorParameter<NotFoundException> {

    public NotFoundErrorView() {
        setHomeNavigationTarget(IndexView.class);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        if (parameter.hasCustomMessage()) {
            setMessage(parameter.getCustomMessage());
        }
        return HttpStatusCode.NOT_FOUND.getCode();
    }
}
