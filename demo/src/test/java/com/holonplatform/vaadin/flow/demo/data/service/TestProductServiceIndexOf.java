package com.holonplatform.vaadin.flow.demo.data.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.internal.query.filter.OperationQueryFilter;
import com.holonplatform.core.internal.query.filter.OperationQueryFilter.FilterOperator;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

/**
 * Unit tests for {@link ProductService#precedingFilter(Product, List)} — the predicate that
 * turns "where does this row sit in the listing?" into a single range {@code COUNT}.
 *
 * <p>This is the Grid's {@code ItemIndexProvider} input: a wrong operator or a wrong column
 * here scrolls a deep link to the wrong row, which is easy to miss by eye and impossible to
 * catch without asserting the predicate directly. The predicate must mirror
 * {@code ProductService.fetch} exactly — same column, and a strict comparison in the same
 * direction as the sort.</p>
 */
class TestProductServiceIndexOf {

    private static Product product() {
        Product p = new Product();
        p.setId(42L);
        p.setName("Widget");
        p.setCategory("Tools");
        p.setPrice(new BigDecimal("19.99"));
        p.setActive(true);
        return p;
    }

    private static List<QuerySortOrder> sort(String column, SortDirection direction) {
        return List.of(new QuerySortOrder(column, direction));
    }

    /** Asserts the filter is a strict comparison on {@code path} against {@code value}. */
    private static void assertComparison(Optional<QueryFilter> filter, String path,
                                         Object value, boolean ascending) {
        assertTrue(filter.isPresent(), "Expected a range-count predicate for this sort");
        OperationQueryFilter<?> op = assertInstanceOf(OperationQueryFilter.class, filter.get());
        assertEquals(ascending ? FilterOperator.LESS_THAN
                               : FilterOperator.GREATER_THAN,
                op.getOperator(),
                "Ascending counts rows that sort before the item; descending counts rows after it");
        assertEquals(path, pathOf(op));
        assertEquals(value, valueOf(op));
    }

    private static String pathOf(OperationQueryFilter<?> op) {
        return ((com.holonplatform.core.Path<?>) op.getLeftOperand()).getName();
    }

    private static Object valueOf(OperationQueryFilter<?> op) {
        return op.getRightOperand()
                .map(expr -> ((com.holonplatform.core.ConstantConverterExpression<?, ?>) expr).getValue())
                .orElse(null);
    }

    // ── default sort (no explicit sort orders) ─────────────────────────────────

    @Test
    void noSortOrders_countsByNameAscending_matchingTheListingDefault() {
        assertComparison(ProductService.precedingFilter(product(), List.of()),
                "name", "Widget", true);
    }

    @Test
    void nullSortOrders_countsByNameAscending() {
        assertComparison(ProductService.precedingFilter(product(), null),
                "name", "Widget", true);
    }

    // ── single-column sorts, both directions ───────────────────────────────────

    @Test
    void nameAscending() {
        assertComparison(ProductService.precedingFilter(product(), sort("name", SortDirection.ASCENDING)),
                "name", "Widget", true);
    }

    @Test
    void nameDescending() {
        assertComparison(ProductService.precedingFilter(product(), sort("name", SortDirection.DESCENDING)),
                "name", "Widget", false);
    }

    @Test
    void categoryAscending() {
        assertComparison(ProductService.precedingFilter(product(), sort("category", SortDirection.ASCENDING)),
                "category", "Tools", true);
    }

    @Test
    void categoryDescending() {
        assertComparison(ProductService.precedingFilter(product(), sort("category", SortDirection.DESCENDING)),
                "category", "Tools", false);
    }

    @Test
    void idAscending() {
        assertComparison(ProductService.precedingFilter(product(), sort("id", SortDirection.ASCENDING)),
                "id", 42L, true);
    }

    @Test
    void idDescending() {
        assertComparison(ProductService.precedingFilter(product(), sort("id", SortDirection.DESCENDING)),
                "id", 42L, false);
    }

    @Test
    void priceAscending() {
        assertComparison(ProductService.precedingFilter(product(), sort("price", SortDirection.ASCENDING)),
                "price", new BigDecimal("19.99"), true);
    }

    @Test
    void priceDescending() {
        assertComparison(ProductService.precedingFilter(product(), sort("price", SortDirection.DESCENDING)),
                "price", new BigDecimal("19.99"), false);
    }

    // ── cases that cannot be expressed as a single range count ─────────────────

    @Test
    void compositeSort_isNotExpressibleAsARangeCount() {
        List<QuerySortOrder> composite = List.of(
                new QuerySortOrder("category", SortDirection.ASCENDING),
                new QuerySortOrder("name", SortDirection.ASCENDING));

        assertTrue(ProductService.precedingFilter(product(), composite).isEmpty(),
                "A two-column sort needs a compound predicate; the caller must skip the scroll instead");
    }

    @Test
    void unsupportedColumn_yieldsNoPredicate() {
        // 'active' is a boolean: there is no useful strict ordering predicate for it.
        assertTrue(ProductService.precedingFilter(product(), sort("active", SortDirection.ASCENDING)).isEmpty());
    }

    @Test
    void unknownColumn_yieldsNoPredicate() {
        assertTrue(ProductService.precedingFilter(product(), sort("createdDate", SortDirection.ASCENDING)).isEmpty());
    }

    @Test
    void nullSortValue_yieldsNoPredicate() {
        // Counting rows "before null" is meaningless and would silently return 0.
        Product noName = product();
        noName.setName(null);

        assertTrue(ProductService.precedingFilter(noName, sort("name", SortDirection.ASCENDING)).isEmpty());
    }

    @Test
    void nullPriceValue_yieldsNoPredicate() {
        Product noPrice = product();
        noPrice.setPrice(null);

        assertTrue(ProductService.precedingFilter(noPrice, sort("price", SortDirection.ASCENDING)).isEmpty());
    }
}
