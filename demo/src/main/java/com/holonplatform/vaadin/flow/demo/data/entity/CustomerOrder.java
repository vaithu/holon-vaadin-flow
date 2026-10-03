package com.holonplatform.vaadin.flow.demo.data.entity;

import com.holonplatform.core.beans.Identifier;
import com.holonplatform.core.property.PathProperty;
import jakarta.persistence.*;

@Entity(name = "customerorder")

@Table(name = "customer_order")
public class CustomerOrder {
    public static final PathProperty<Long> CUSTOMER_ID = PathProperty.create("customerId", Long.class);
    public static final PathProperty<String> ORDER_DATE = PathProperty.create("orderDate", String.class);
    @Identifier @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private long customerId;
    private String orderNumber;
    private String orderDate;
    private String status;
    private String amount;
    public Long getId() { return id; }
    public long getCustomerId() { return customerId; }
    public String getOrderNumber() { return orderNumber; }
    public String getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public String getAmount() { return amount; }
}
