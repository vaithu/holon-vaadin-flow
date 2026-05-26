package com.holonplatform.vaadin.flow.customer.ui.view;

import com.holonplatform.vaadin.flow.customer.service.CustomerService;
import com.holonplatform.vaadin.flow.customer.ui.CustomerDetailPanel;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

/**
 * Route wrapper for {@link CustomerDetailPanel}.
 *
 * <p>If you need to embed the customer master-detail view inside an existing view
 * instead of navigating to a dedicated URL, use {@link CustomerDetailPanel} directly:</p>
 * <pre>{@code
 * // Full master-detail, auto-selects first customer
 * add(new CustomerDetailPanel(customerService));
 *
 * // Pre-select a specific customer
 * add(new CustomerDetailPanel(customerService, customerId));
 * }</pre>
 */
@org.springframework.stereotype.Component
@Scope("prototype")
@Route("customers/:customerId")
public class CustomerDetailView extends Div
        implements BeforeEnterObserver, HasDynamicTitle {

    private static final String PARAM_ID = "customerId";

    private final CustomerDetailPanel panel;
    private String pendingId;

    public CustomerDetailView(CustomerService customerService) {
        this.panel = new CustomerDetailPanel(customerService);
        setSizeFull();
        add(panel);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getRouteParameters().get(PARAM_ID).ifPresent(id -> this.pendingId = id);
    }

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        if (pendingId != null) {
            try {
                panel.selectCustomer(Long.parseLong(pendingId));
            } catch (NumberFormatException ignored) { }
            pendingId = null;
        }
    }

    @Override
    public String getPageTitle() {
        return "Customer";
    }

    /** Navigates to the detail view for the given customer ID. */
    public static void show(Long customerId) {
        UI.getCurrent().navigate("customers/" + customerId);
    }
}
