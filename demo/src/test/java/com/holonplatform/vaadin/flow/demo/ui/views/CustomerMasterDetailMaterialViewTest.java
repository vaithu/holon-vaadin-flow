package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.CustomerDetailService;
import com.holonplatform.vaadin.flow.demo.test.AbstractViewSessionTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerMasterDetailMaterialViewTest extends AbstractViewSessionTest {

    @Test
    void detailTabs_acceptSelectionAndPopulateRows() {
        Product product = product(10L, "Acme");
        CustomerMasterDetailMaterialView.OverviewTab overview =
                new CustomerMasterDetailMaterialView.OverviewTab();
        CustomerDetailService service = mock(CustomerDetailService.class);
        when(service.findOpenOrders(any())).thenReturn(java.util.List.of(
                new CustomerDetailService.Order("SO-1", "14 Sep 2026", "Confirmed", "€100.00"),
                new CustomerDetailService.Order("SO-2", "02 Sep 2026", "Confirmed", "€50.00")));
        when(service.findInvoices(any())).thenReturn(java.util.List.of(
                new CustomerDetailService.Invoice("INV-1", "28 Sep 2026", "Open", "€100.00"),
                new CustomerDetailService.Invoice("INV-2", "12 Oct 2026", "Scheduled", "€50.00")));
        when(service.findFiles(any())).thenReturn(java.util.List.of(
                new CustomerDetailService.File("agreement.pdf", "PDF", "12 Sep 2026", "412 KB"),
                new CustomerDetailService.File("nda.pdf", "PDF", "08 Sep 2026", "2.1 MB"),
                new CustomerDetailService.File("forecast.xlsx", "XLSX", "01 Sep 2026", "84 KB")));
        when(service.findActivity(any())).thenReturn(java.util.List.of());
        var orders = CustomerMasterDetailMaterialView.ordersList(service);
        var invoices = CustomerMasterDetailMaterialView.invoicesList(service);
        CustomerMasterDetailMaterialView.ActivityTab activity =
                new CustomerMasterDetailMaterialView.ActivityTab(service);
        var files = CustomerMasterDetailMaterialView.filesList(service);

        assertThatCode(() -> {
            overview.onItemSelected(product);
            orders.onItemSelected(product);
            invoices.onItemSelected(product);
            activity.onItemSelected(product);
            files.onItemSelected(product);
        }).doesNotThrowAnyException();

        assertThat(orders.getListing().getGrid().getListDataView().getItemCount()).isEqualTo(2);
        assertThat(invoices.getListing().getGrid().getListDataView().getItemCount()).isEqualTo(2);
        assertThat(files.getListing().getGrid().getListDataView().getItemCount()).isEqualTo(3);
        assertThat(orders.getListing().getGrid().getColumns()).hasSize(4);
    }

    private static Product product(long id, String name) {
        Product product = new Product(name, "DACH", BigDecimal.valueOf(125));
        product.setId(id);
        return product;
    }
}
