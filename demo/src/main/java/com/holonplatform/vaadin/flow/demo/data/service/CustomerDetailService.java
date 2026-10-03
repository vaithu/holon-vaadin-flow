package com.holonplatform.vaadin.flow.demo.data.service;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.beans.BeanDatastoreHelper;
import com.holonplatform.vaadin.flow.demo.data.entity.CustomerActivity;
import com.holonplatform.vaadin.flow.demo.data.entity.CustomerFile;
import com.holonplatform.vaadin.flow.demo.data.entity.CustomerInvoice;
import com.holonplatform.vaadin.flow.demo.data.entity.CustomerOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CustomerDetailService {

    private final BeanDatastoreHelper<CustomerOrder> orders;
    private final BeanDatastoreHelper<CustomerInvoice> invoices;
    private final BeanDatastoreHelper<CustomerFile> files;
    private final BeanDatastoreHelper<CustomerActivity> activity;

    public CustomerDetailService(Datastore datastore) {
        orders = BeanDatastoreHelper.of(datastore, CustomerOrder.class);
        invoices = BeanDatastoreHelper.of(datastore, CustomerInvoice.class);
        files = BeanDatastoreHelper.of(datastore, CustomerFile.class);
        activity = BeanDatastoreHelper.of(datastore, CustomerActivity.class);
    }

    @Transactional(readOnly = true)
    public Stream<Order> fetchOrders(long customerId, int offset, int limit) {
        return orders.findSlice(limit, offset, CustomerOrder.CUSTOMER_ID.eq(customerId),
                        CustomerOrder.ORDER_DATE.desc())
                .map(row -> new Order(row.getOrderNumber(), row.getOrderDate(),
                        row.getStatus(), row.getAmount())).toList().stream();
    }

    @Transactional(readOnly = true)
    public Stream<Invoice> fetchInvoices(long customerId, int offset, int limit) {
        return invoices.findSlice(limit, offset, CustomerInvoice.CUSTOMER_ID.eq(customerId),
                        CustomerInvoice.DUE_DATE.asc())
                .map(row -> new Invoice(row.getInvoiceNumber(), row.getDueDate(),
                        row.getStatus(), row.getBalance())).toList().stream();
    }

    @Transactional(readOnly = true)
    public Stream<File> fetchFiles(long customerId, int offset, int limit) {
        return files.findSlice(limit, offset, CustomerFile.CUSTOMER_ID.eq(customerId),
                        CustomerFile.UPLOADED.desc())
                .map(row -> new File(row.getFileName(), row.getFileType(),
                        row.getUploaded(), row.getFileSize())).toList().stream();
    }

    @Transactional(readOnly = true)
    public Stream<Activity> fetchActivity(long customerId, int offset, int limit) {
        return activity.findSlice(limit, offset, CustomerActivity.CUSTOMER_ID.eq(customerId),
                        CustomerActivity.ID.asc())
                .map(row -> new Activity(String.valueOf(row.getId()), row.getEventTime(),
                        row.getActor(), row.getDescription(), row.getSeverity(), row.getDetail()))
                .toList().stream();
    }

    @Deprecated
    public List<Order> findOpenOrders(com.holonplatform.vaadin.flow.demo.data.entity.Product customer) {
        return fetchOrders(customer.getId(), 0, Integer.MAX_VALUE).toList();
    }

    @Deprecated
    public List<Invoice> findInvoices(com.holonplatform.vaadin.flow.demo.data.entity.Product customer) {
        return fetchInvoices(customer.getId(), 0, Integer.MAX_VALUE).toList();
    }

    @Deprecated
    public List<File> findFiles(com.holonplatform.vaadin.flow.demo.data.entity.Product customer) {
        return fetchFiles(customer.getId(), 0, Integer.MAX_VALUE).toList();
    }

    @Deprecated
    public List<Activity> findActivity(com.holonplatform.vaadin.flow.demo.data.entity.Product customer) {
        return fetchActivity(customer.getId(), 0, Integer.MAX_VALUE).toList();
    }

    public record Order(String order, String date, String status, String amount) { }
    public record Invoice(String invoice, String dueDate, String status, String balance) { }
    public record File(String file, String type, String uploaded, String size) { }
    public record Activity(String id, String time, String actor, String description,
                           String severity, String detail) { }
}
