package com.holonplatform.vaadin.flow.demo.data.entity;

import com.holonplatform.core.beans.Identifier;
import com.holonplatform.core.property.PathProperty;
import jakarta.persistence.*;

@Entity(name = "customerinvoice")
@Table(name = "customer_invoice")
public class CustomerInvoice {
    public static final PathProperty<Long> CUSTOMER_ID = PathProperty.create("customerId", Long.class);
    public static final PathProperty<String> DUE_DATE = PathProperty.create("dueDate", String.class);
    @Identifier @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private long customerId;
    private String invoiceNumber;
    private String dueDate;
    private String status;
    private String balance;
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public String getBalance() { return balance; }
}
