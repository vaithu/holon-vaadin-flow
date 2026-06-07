package com.holonplatform.vaadin.flow.customer.ui.view;

import com.holonplatform.vaadin.flow.customer.service.CustomerService;
import com.holonplatform.vaadin.flow.customer.ui.CustomerListPanel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Route wrapper for {@link CustomerListPanel}.
 *
 * <p>If you need to embed the customer listing inside an existing view
 * instead of navigating to a dedicated URL, use {@link CustomerListPanel} directly:</p>
 * <pre>{@code
 * content(new CustomerListPanel(customerService));
 * }</pre>
 */
@Component
@Scope("prototype")
@PageTitle("Customers")
@Route("customers")
public class CustomerListView extends Div {

    public CustomerListView(CustomerService customerService) {
        setSizeFull();
        add(new CustomerListPanel(customerService));
    }

    /** Navigates to this view. */
    public static void show() {
        UI.getCurrent().navigate(CustomerListView.class);
    }
}
