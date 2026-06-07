package com.holonplatform.vaadin.flow.customer.ui.view;

import com.holonplatform.vaadin.flow.customer.service.CustomerService;
import com.holonplatform.vaadin.flow.customer.ui.CustomerDetailPanel;
import com.holonplatform.vaadin.flow.customer.ui.CustomerListPanel;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouteParameters;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerViewTest {

    @Test
    void listView_wrapsCustomerListPanel() {
        CustomerListView view = new CustomerListView(mock(CustomerService.class));

        assertEquals(1, view.getComponentCount());
        assertInstanceOf(CustomerListPanel.class, view.getComponentAt(0));
        assertEquals("100%", view.getWidth());
        assertEquals("100%", view.getHeight());
    }

    @Test
    void detailView_wrapsCustomerDetailPanel_andUsesSingularPageTitle() {
        CustomerDetailView view = new CustomerDetailView(mock(CustomerService.class));

        assertEquals(1, view.getComponentCount());
        assertInstanceOf(CustomerDetailPanel.class, view.getComponentAt(0));
        assertEquals("100%", view.getWidth());
        assertEquals("100%", view.getHeight());
        assertEquals("Customer", view.getPageTitle());
    }

    @Test
    void beforeEnter_storesRouteParameterForLaterSelection() throws Exception {
        CustomerDetailView view = new CustomerDetailView(mock(CustomerService.class));
        RouteParameters routeParameters = mock(RouteParameters.class);
        when(routeParameters.get("customerId")).thenReturn(Optional.of("42"));

        BeforeEnterEvent event = mock(BeforeEnterEvent.class);
        when(event.getRouteParameters()).thenReturn(routeParameters);

        view.beforeEnter(event);

        Field pendingIdField = CustomerDetailView.class.getDeclaredField("pendingId");
        pendingIdField.setAccessible(true);

        assertEquals("42", pendingIdField.get(view));
        assertNotNull(view.getChildren().findFirst().orElse(null));
    }
}

