package com.holonplatform.vaadin.flow.demo.data.service;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.beans.BeanDatastoreHelper;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private static final PathProperty<java.math.BigDecimal> PRICE_PROP =
            PathProperty.create("price", java.math.BigDecimal.class);

    // ── CRUD delegate ─────────────────────────────────────────────────────────
    private final BeanDatastoreHelper<Product> helper;

    public ProductService(Datastore datastore) {
        this.helper = BeanDatastoreHelper.of(datastore, Product.class);
    }

    // ── Read operations ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return helper.findOne(ID_PROP.eq(id));
    }

    /**
     * Returns the first product in the listing's default sort order (name ascending), for
     * detail-panel pre-population.
     *
     * <p>The sort must match {@link #fetch} — the grid renders only a window of rows, so an
     * unsorted {@code LIMIT 1} would return an arbitrary product that may fall outside the
     * rendered window and appear unselected.</p>
     */
    @Transactional(readOnly = true)
    public Optional<Product> findFirst() {
        return helper.getDatastore()
                .query(TARGET)
                .sort(NAME_PROP.asc())
                .restrict(1, 0)
                .stream(BeanProjection.of(Product.class))
                .findFirst();
    }

    /**
     * Returns the zero-based row index of {@code product} in the listing, i.e. the number of
     * rows that precede it under the <em>currently applied</em> sort, search text and filter.
     *
     * <p>Used as the Grid's {@code ItemIndexProvider} so a deep-linked row further down the
     * list is scrolled into view. It must mirror {@link #fetch} exactly — counting against
     * different criteria would scroll to the wrong row — so the same text/filter the last
     * fetch used are passed back in, and the sort is read from the Grid's own query.</p>
     *
     * <p>Returns empty when the position cannot be expressed as a single range count
     * (composite sort, a sort on a column with no ordering predicate, or a null sort value).
     * The caller then simply skips the scroll rather than jumping somewhere wrong.</p>
     *
     * @param product     the item to locate
     * @param text        the search text active on the listing, or {@code null}
     * @param filter      the filter-panel filter active on the listing, or {@code null}
     * @param sortOrders  the Grid's current sort orders; empty means the default name-ascending
     */
    @Transactional(readOnly = true)
    public Optional<Integer> indexOf(Product product, String text, QueryFilter filter,
                                     List<QuerySortOrder> sortOrders) {
        if (product == null) {
            return Optional.empty();
        }
        Optional<QueryFilter> preceding = precedingFilter(product, sortOrders);
        if (preceding.isEmpty()) {
            return Optional.empty();
        }
        var q = helper.getDatastore().query(TARGET).filter(preceding.get());
        if (text != null && !text.isBlank()) {
            q = q.filter(nameOrCategoryFilter(text));
        }
        if (filter != null) {
            q = q.filter(filter);
        }
        return Optional.of((int) q.count());
    }

    /**
     * Builds the "sorts before {@code product}" predicate for the active sort order, so the
     * row index is a single {@code COUNT}. Empty when the sort cannot be expressed that way.
     *
     * <p>Package-private rather than private so the sort-branch matrix can be unit tested
     * without a database: an error here scrolls the grid to the wrong row, which is hard to
     * notice by eye but trivial to assert.</p>
     */
    static Optional<QueryFilter> precedingFilter(Product product,
                                                         List<QuerySortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) {
            // Matches the default sort applied by fetch().
            return comparison(NAME_PROP, product.getName(), true);
        }
        if (sortOrders.size() > 1) {
            return Optional.empty();  // composite sort: a single range count cannot express it
        }
        QuerySortOrder order = sortOrders.get(0);
        boolean asc = order.getDirection() != SortDirection.DESCENDING;
        return switch (order.getSorted()) {
            case "name" -> comparison(NAME_PROP, product.getName(), asc);
            case "category" -> comparison(CATEGORY_PROP, product.getCategory(), asc);
            case "id" -> comparison(ID_PROP, product.getId(), asc);
            case "price" -> comparison(PRICE_PROP, product.getPrice(), asc);
            default -> Optional.empty();  // e.g. a boolean column: no useful ordering predicate
        };
    }

    private static <V> Optional<QueryFilter> comparison(PathProperty<V> property, V value, boolean asc) {
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(asc ? property.lt(value) : property.gt(value));
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
        return count(text, null);
    }

    public long count(String text, QueryFilter filter) {
        var q = helper.getDatastore().query(TARGET);
        if (text != null && !text.isBlank()) {
            q = q.filter(nameOrCategoryFilter(text));
        }
        if (filter != null) {
            q = q.filter(filter);
        }
        return q.count();
    }

    /**
     * Paginated stream filtered by both a text search and an arbitrary {@link QueryFilter}
     * (e.g. the one produced by {@link com.iyensoft.vaadin.flow.components.DynamicFilterPanel}).
     * Both filters are AND-combined when both are non-null/non-blank.
     * Applies the given {@link QuerySort} if non-null, otherwise falls back to name ascending.
     */
    @Transactional(readOnly = true)
    public Stream<Product> fetch(int offset, int limit, String text, QueryFilter filter, QuerySort sort) {
        var q = helper.getDatastore()
                .query(TARGET)
                .restrict(limit, offset);
        if (sort != null) {
            q = q.sort(sort);
        } else {
            q = q.sort(NAME_PROP.asc());
        }
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static QueryFilter nameOrCategoryFilter(String text) {
        return NAME_PROP.containsIgnoreCase(text)
                .or(CATEGORY_PROP.containsIgnoreCase(text));
    }

    /**
     * Column-aware paginated stream for {@link com.iyensoft.vaadin.flow.components.ListingBundleBuilder.ColumnAwareFilteredFetchCallback}.
     *
     * <p>The {@code columns} list carries the visible column property names configured
     * via {@code .columns("name", "price", ...)} in the listing builder. Use it to
     * skip fetching heavy fields (e.g. binary blobs, large text) that are not displayed.
     * When the list is <strong>empty</strong> (columns not explicitly configured) all
     * fields are fetched — safe fallback.</p>
     *
     * <p>For simple entities like {@link Product} whose fields are all lightweight,
     * the canonical approach is to check the list and fall back to a full fetch when
     * it is empty. If you want true SQL projection, build a {@code PropertySet} from
     * the column names and use a {@code PropertyBox} stream instead.</p>
     *
     * @param offset  first row index (0-based)
     * @param limit   max rows
     * @param text    optional name/category text filter
     * @param filter  optional structured filter from DynamicFilterPanel (may be null)
     * @param sort    optional sort from grid column headers (null → name asc)
     * @param columns visible column names; empty means "all columns"
     */
    public Stream<Product> fetch(int offset, int limit, String text,
                                 QueryFilter filter, QuerySort sort, List<String> columns) {
        // columns is empty when .columns(…) was not called — fetch everything
        // For a real projection use-case you would build a PropertySet from the
        // column names here and use a PropertyBox stream instead of BeanProjection.
        QueryFilter textFilter = null;
        if (text != null && !text.isBlank()) {
            textFilter = nameOrCategoryFilter(text);
        }

        if (filter != null && textFilter != null) {
            filter = filter.and(textFilter);
        }

        return helper.findSlice(offset, limit, filter, sort,columns);
    }
}
