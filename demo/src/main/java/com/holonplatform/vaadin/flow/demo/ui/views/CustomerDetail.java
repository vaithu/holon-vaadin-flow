package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.CustomerDetailService;
import com.iyensoft.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.iyensoft.vaadin.flow.components.Empty;
import com.iyensoft.vaadin.flow.components.EntityFormPanel;
import com.iyensoft.vaadin.flow.components.ListingBundle;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.vaadin.flow.component.html.Div;

import java.io.Serializable;

/**
 * Customer detail content composed from existing framework components.
 *
 * <p>Each related tab is created on demand and fetches one SQL page at a time. Selecting another
 * customer refreshes only tabs that have already been opened.</p>
 */
public final class CustomerDetail extends Div implements DetailSyncAware<Product> {

    private final CustomerDetailService service;
    private final long[] customerId = {-1L};
    private final EntityFormPanel<Product> form;
    private ListingBundle<OrderRow> orders;
    private ListingBundle<InvoiceRow> invoices;
    private ListingBundle<FileRow> files;
    private ListingBundle<ActivityRow> activity;

    public CustomerDetail(CustomerDetailService service) {
        this.service = service;
        this.form = EntityFormPanel.<Product>bean(Product.class)
                .readOnly()
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .properties("name", "category", "price", "active", "createdDate")
                .autoLabels(true)
                .build();

        Div tabs = Components.div().fullWidth().build();
        LazyTabsBuilder.create(tabs)
                .withEagerTab("Overview",
                        Components.detailPanel("Account & terms")
                                .content(form)
                                .build())
                .withLazyTab("Orders", this::orders)
                .withLazyTab("Invoices", this::invoices)
                .withLazyTab("Files", this::files)
                .withLazyTab("Activity", this::activity);

        Components.configure(this).fullWidth().add(tabs);
    }

    @Override
    public void onItemSelected(Product product) {
        customerId[0] = product.getId();
        form.setBean(product);
        refresh(orders);
        refresh(invoices);
        refresh(files);
        refresh(activity);
    }

    private Div orders() {
        orders = Components.listing(OrderRow.class)
                .columns("order", "date", "status", "amount")
                .paginated()
                .pageSizes(25, 50, 100)
                .defaultPageSize(25)
                .emptyState(relatedItems("No open orders", "This customer has no open orders."))
                .fetch((query, text, sort) ->
                        service.fetchOrders(customerId[0], query.getOffset(), query.getLimit())
                                .map(row -> new OrderRow(row.order(), row.date(), row.status(), row.amount())))
                .build();
        return orders;
    }

    private Div invoices() {
        invoices = Components.listing(InvoiceRow.class)
                .columns("invoice", "dueDate", "status", "balance")
                .paginated()
                .pageSizes(25, 50, 100)
                .defaultPageSize(25)
                .emptyState(relatedItems("No invoices", "This customer has no invoices."))
                .fetch((query, text, sort) ->
                        service.fetchInvoices(customerId[0], query.getOffset(), query.getLimit())
                                .map(row -> new InvoiceRow(row.invoice(), row.dueDate(), row.status(), row.balance())))
                .build();
        return invoices;
    }

    private Div files() {
        files = Components.listing(FileRow.class)
                .columns("file", "type", "uploaded", "size")
                .paginated()
                .pageSizes(25, 50, 100)
                .defaultPageSize(25)
                .emptyState(relatedItems("No files uploaded",
                        "Upload a file to keep customer documents together."))
                .fetch((query, text, sort) ->
                        service.fetchFiles(customerId[0], query.getOffset(), query.getLimit())
                                .map(row -> new FileRow(row.file(), row.type(), row.uploaded(), row.size())))
                .build();
        return files;
    }

    private Div activity() {
        activity = Components.listing(ActivityRow.class)
                .columns("time", "actor", "description", "severity")
                .paginated()
                .pageSizes(25, 50, 100)
                .defaultPageSize(25)
                .emptyState(relatedItems("No activity recorded",
                        "There is no activity for this customer."))
                .fetch((query, text, sort) ->
                        service.fetchActivity(customerId[0], query.getOffset(), query.getLimit())
                                .map(row -> new ActivityRow(row.time(), row.actor(),
                                        row.description(), row.severity())))
                .build();
        return activity;
    }

    private static void refresh(ListingBundle<?> bundle) {
        if (bundle != null) {
            bundle.grid().getDataProvider().refreshAll();
        }
    }

    private static Empty relatedItems(String title, String description) {
        return Empty.builder()
                .title(title)
                .description(description)
                .build();
    }

    // Holon bean introspection resolves listing columns from JavaBean getters, not record
    // accessors, so each row exposes getX() methods matching the configured column names.

    public record OrderRow(String order, String date, String status, String amount)
            implements Serializable {
        public String getOrder() { return order; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
        public String getAmount() { return amount; }
    }

    public record InvoiceRow(String invoice, String dueDate, String status, String balance)
            implements Serializable {
        public String getInvoice() { return invoice; }
        public String getDueDate() { return dueDate; }
        public String getStatus() { return status; }
        public String getBalance() { return balance; }
    }

    public record FileRow(String file, String type, String uploaded, String size)
            implements Serializable {
        public String getFile() { return file; }
        public String getType() { return type; }
        public String getUploaded() { return uploaded; }
        public String getSize() { return size; }
    }

    public record ActivityRow(String time, String actor, String description, String severity)
            implements Serializable {
        public String getTime() { return time; }
        public String getActor() { return actor; }
        public String getDescription() { return description; }
        public String getSeverity() { return severity; }
    }
}
