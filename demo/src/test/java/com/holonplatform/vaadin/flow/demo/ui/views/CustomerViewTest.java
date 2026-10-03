package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.CustomerDetailService;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.test.AbstractViewSessionTest;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.QueryParameters;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CustomerViewTest extends AbstractViewSessionTest {

    private final ProductService productService = mock(ProductService.class);
    private final CustomerDetailService detailService = mock(CustomerDetailService.class);

    @Test
    void validDeepLinkPreloadsItemOnce() {
        Product product = mock(Product.class);
        when(productService.findById(42L)).thenReturn(Optional.of(product));
        CustomerView view = new CustomerView(productService, detailService);
        BeforeEnterEvent event = event("42");

        view.beforeEnter(event);

        verify(productService).findById(42L);
        verify(event, never()).rerouteToError(eq(NotFoundException.class), anyString());
    }

    @Test
    void missingItemReroutesToNotFound() {
        CustomerView view = new CustomerView(productService, detailService);
        BeforeEnterEvent event = event("42");

        view.beforeEnter(event);

        verify(event).rerouteToError(NotFoundException.class, "No customer with id 42");
    }

    @Test
    void malformedIdReroutesToNotFound() {
        CustomerView view = new CustomerView(productService, detailService);
        BeforeEnterEvent event = event("invalid");

        view.beforeEnter(event);

        verify(event).rerouteToError(NotFoundException.class, "Invalid customer id invalid");
    }

    @Test
    void noDeepLinkDoesNotLookUpAnItem() {
        CustomerView view = new CustomerView(productService, detailService);
        BeforeEnterEvent event = mock(BeforeEnterEvent.class);
        when(event.getLocation()).thenReturn(new Location("customers"));

        view.beforeEnter(event);

        verifyNoInteractions(productService);
    }

    private static BeforeEnterEvent event(String id) {
        BeforeEnterEvent event = mock(BeforeEnterEvent.class);
        when(event.getLocation()).thenReturn(new Location("customers", QueryParameters.of("id", id)));
        return event;
    }
}
