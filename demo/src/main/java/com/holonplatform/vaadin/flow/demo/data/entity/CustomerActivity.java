package com.holonplatform.vaadin.flow.demo.data.entity;

import com.holonplatform.core.beans.Identifier;
import com.holonplatform.core.property.PathProperty;
import jakarta.persistence.*;

@Entity(name = "customeractivity")
@Table(name = "customer_activity")
public class CustomerActivity {
    public static final PathProperty<Long> ID = PathProperty.create("id", Long.class);
    public static final PathProperty<Long> CUSTOMER_ID = PathProperty.create("customerId", Long.class);
    @Identifier @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private long customerId;
    private String eventTime;
    private String actor;
    private String description;
    private String severity;
    private String detail;
    public Long getId() { return id; }
    public String getEventTime() { return eventTime; }
    public String getActor() { return actor; }
    public String getDescription() { return description; }
    public String getSeverity() { return severity; }
    public String getDetail() { return detail; }
}
