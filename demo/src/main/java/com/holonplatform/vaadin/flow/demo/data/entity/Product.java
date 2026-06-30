package com.holonplatform.vaadin.flow.demo.data.entity;

import com.holonplatform.core.beans.Identifier;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Demo JPA entity: a catalogue product.
 *
 * <p>Annotated for Jakarta Persistence (schema + ORM) and Holon Platform bean
 * introspection ({@link Identifier} marks the PK so {@link com.holonplatform.core.datastore.beans.BeanDatastoreHelper} can
 * distinguish INSERT from UPDATE without relying on the value being zero/null).
 */
@Entity(name = "product")
@Table(name = "product")
public class Product {

    @Identifier
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Size(max = 80)
    @Column(name = "category", length = 80)
    private String category;

    @DecimalMin("0.00")
    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "created_date")
    private LocalDate createdDate;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Product() { /* JPA */ }

    public Product(String name, String category, BigDecimal price) {
        this.name        = name;
        this.category    = category;
        this.price       = price;
        this.active      = true;
        this.createdDate = LocalDate.now();
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public Long getId()                       { return id; }
    public void setId(Long id)               { this.id = id; }

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public String getCategory()              { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice()             { return price; }
    public void setPrice(BigDecimal price)   { this.price = price; }

    public boolean isActive()                { return active; }
    public void setActive(boolean active)    { this.active = active; }

    public LocalDate getCreatedDate()                  { return createdDate; }
    public void setCreatedDate(LocalDate createdDate)  { this.createdDate = createdDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "Product[id=" + id + ", name=" + name + "]";
    }
}





