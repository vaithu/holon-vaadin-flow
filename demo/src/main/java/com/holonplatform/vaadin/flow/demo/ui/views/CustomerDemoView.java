package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.customer.service.CustomerService;
import com.holonplatform.vaadin.flow.customer.ui.CustomerDetailPanel;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo view for the Customer module — embeds {@link CustomerDetailPanel} directly.
 *
 * <h3>How to use in your own application:</h3>
 * <pre>{@code
 * // Full master-detail CRUD — just add the panel anywhere
 * add(new CustomerDetailPanel(customerService));
 *
 * // Listing-only variant
 * add(new CustomerListPanel(customerService));
 *
 * // Pre-select a specific customer
 * add(new CustomerDetailPanel(customerService, customerId));
 * }</pre>
 */
@PageTitle("Customer Module – Holon Demo")
@Route(value = "customer-module", layout = DemoMainLayout.class)
public class CustomerDemoView extends Div {

    public CustomerDemoView(CustomerService customerService) {
        setSizeFull();
        addClassName("customer-demo-view");
        add(new CustomerDetailPanel(customerService));
    }
}
