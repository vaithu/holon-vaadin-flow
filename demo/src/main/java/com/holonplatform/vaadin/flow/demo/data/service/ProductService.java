package com.holonplatform.vaadin.flow.demo.data.service;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.beans.BeanDatastoreHelper;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Service layer for {@link Product} CRUD operations.
 *
 * <p>Delegates all persistence to {@link BeanDatastoreHelper}, which wraps the Holon
 * {@link com.holonplatform.core.datastore.beans.BeanDatastore} — no manual
 * {@code PropertyBox} conversions. The underlying JPA Datastore is
 * auto-configured via {@code holon-datastore-jpa-spring-boot}.
 */
@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    // ── Holon property definitions (used for raw query filters / sorts) ───────
    private static final DataTarget<?>         TARGET        = DataTarget.named("product");
    private static final StringProperty        NAME_PROP     = StringProperty.create("name");
    private static final StringProperty        CATEGORY_PROP = StringProperty.create("category");
    private static final PathProperty<Long>    ID_PROP       = PathProperty.create("id", Long.class);
    private static final PathProperty<Boolean> ACTIVE_PROP   = PathProperty.create("active", Boolean.class);

    // ── CRUD delegate ─────────────────────────────────────────────────────────
    private final BeanDatastoreHelper<Product> helper;

    public ProductService(Datastore datastore) {
        this.helper = BeanDatastoreHelper.of(datastore, Product.class);
    }

    // ── Read operations ───────────────────────────────────────────────────────

    public Optional<Product> findById(Long id) {
        return helper.findOne(ID_PROP.eq(id));
    }

    public List<Product> findAll() {
        return helper.getDatastore()
                .query(TARGET)
                .sort(NAME_PROP.asc())
                .stream(BeanProjection.of(Product.class))
                .toList();
    }

    public List<Product> findActive() {
        try (Stream<Product> s = helper.findAll(ACTIVE_PROP.eq(true))) {
            return s.toList();
        }
    }

    public List<Product> search(String text) {
        if (text == null || text.isBlank()) return findAll();
        try (Stream<Product> s = helper.findAll(nameOrCategoryFilter(text))) {
            return s.toList();
        }
    }

    /**
     * Paginated stream used by the Listing bundle fetch callback.
     *
     * @param offset first row index (0-based)
     * @param limit  max number of rows
     * @param text   optional search filter (name or category)
     */
    public Stream<Product> fetch(int offset, int limit, String text) {
        var q = helper.getDatastore()
                .query(TARGET)
                .restrict(limit, offset)
                .sort(NAME_PROP.asc());
        if (text != null && !text.isBlank()) {
            q = q.filter(nameOrCategoryFilter(text));
        }
        return q.stream(BeanProjection.of(Product.class));
    }

    public long count(String text) {
        var q = helper.getDatastore().query(TARGET);
        if (text != null && !text.isBlank()) {
            q = q.filter(nameOrCategoryFilter(text));
        }
        return q.count();
    }

    /**
     * Paginated stream filtered by both a text search and an arbitrary {@link QueryFilter}
     * (e.g. the one produced by {@link com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel}).
     * Both filters are AND-combined when both are non-null/non-blank.
     */
    public Stream<Product> fetch(int offset, int limit, String text, QueryFilter filter) {
        var q = helper.getDatastore()
                .query(TARGET)
                .restrict(limit, offset)
                .sort(NAME_PROP.asc());
        if (text != null && !text.isBlank()) {
            q = q.filter(nameOrCategoryFilter(text));
        }
        if (filter != null) {
            q = q.filter(filter);
        }
        return q.stream(BeanProjection.of(Product.class));
    }

    // ── Write operations ──────────────────────────────────────────────────────

    @Transactional
    public Product save(Product product) {
        log.info("Saving product: {}", product.getName());
        return helper.save(product).getResult().orElse(product);
    }

    @Transactional
    public void delete(Product product) {
        log.info("Deleting product id={}", product.getId());
        helper.delete(product);
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting product id={}", id);
        findById(id).ifPresent(helper::delete);
    }

    // ── Seed data ────────────────────────────────────────────────────────────

    /**
     * Inserts sample data when the database is empty.
     * Called from {@link com.holonplatform.vaadin.flow.demo.data.DemoDataInitializer}.
     */
    @Transactional
    public void seedIfEmpty() {
        if (helper.getDatastore().query(TARGET).count() > 0) return;

        List<Product> seed = List.of(
            new Product("Ergonomic Desk Chair",     "Furniture",   new BigDecimal("549.00")),
            new Product("Standing Desk",            "Furniture",   new BigDecimal("899.00")),
            new Product("27\" 4K Monitor",          "Electronics", new BigDecimal("749.00")),
            new Product("Mechanical Keyboard",      "Electronics", new BigDecimal("149.00")),
            new Product("USB-C Hub 10-in-1",        "Electronics", new BigDecimal("79.00")),
            new Product("Webcam 4K Pro",            "Electronics", new BigDecimal("129.00")),
            new Product("Noise-Cancelling Headset", "Audio",       new BigDecimal("299.00")),
            new Product("Desk Lamp LED",            "Lighting",    new BigDecimal("89.00")),
            new Product("Cable Management Kit",     "Accessories", new BigDecimal("29.00")),
            new Product("Monitor Arm",              "Accessories", new BigDecimal("69.00")),
            new Product("Wireless Mouse",           "Electronics", new BigDecimal("59.00")),
            new Product("Laptop Stand",             "Accessories", new BigDecimal("45.00"))
        );

        helper.bulkInsert(seed);
        log.info("Seeded {} demo products", seed.size());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static QueryFilter nameOrCategoryFilter(String text) {
        return NAME_PROP.containsIgnoreCase(text)
                .or(CATEGORY_PROP.containsIgnoreCase(text));
    }
}
