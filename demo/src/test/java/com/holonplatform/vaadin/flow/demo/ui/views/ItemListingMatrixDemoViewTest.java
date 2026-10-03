package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.ItemListingMatrix;
import com.vaadin.flow.data.provider.SortDirection;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ItemListingMatrixDemoViewTest {

    @Test
    void matrixIsPartOfListingAndPagesLazyData() {
        BeanListing<BeanListingDemoView.Product> listing = ItemListingMatrixDemoView.createExample();
        ItemListingMatrix<BeanListingDemoView.Product, String> matrix = listing.getMatrixView().orElseThrow();
        assertSame(matrix, listing.getComponent());
        assertFalse(matrix.getModeSwitch().getValue());
        matrix.getModeSwitch().setValue(true);
        assertFalse(listing.getGrid().isVisible());
        assertEquals(6, matrix.getTable().getHeaderRows().getFirst().getHeaderCells().size());
        assertEquals("Product 1", matrix.getTable().getHeaderRows().getFirst().getHeaderCells().get(1).getText());
        matrix.goToNextPage();
        assertEquals(2, matrix.getCurrentPage());
        assertEquals("Product 6", matrix.getTable().getHeaderRows().getFirst().getHeaderCells().get(1).getText());
        IntStream.range(0, 3).forEach(i -> matrix.goToNextPage());
        assertEquals(5, matrix.getCurrentPage());
        assertEquals(4, matrix.getTable().getHeaderRows().getFirst().getHeaderCells().size());
        matrix.goToNextPage();
        assertEquals(5, matrix.getCurrentPage());
        matrix.getModeSwitch().setValue(false);
        assertTrue(listing.getGrid().isVisible());
        assertFalse(matrix.getPagination().isVisible());
    }

    @Test
    void refreshesWhenProviderChangesAndLimitsFetch() {
        BeanListing<BeanListingDemoView.Product> listing = ItemListingMatrixDemoView.createExample();
        ItemListingMatrix<BeanListingDemoView.Product, String> matrix = listing.getMatrixView().orElseThrow();
        AtomicInteger maxRequested = new AtomicInteger();
        listing.setItems(query -> {
            maxRequested.accumulateAndGet(query.getLimit(), Math::max);
            return IntStream.range(query.getOffset(), Math.min(100_000, query.getOffset() + query.getLimit()))
                    .mapToObj(i -> new BeanListingDemoView.Product(i + 1, "Item " + (i + 1),
                            "Equipment", 12.5, true, BeanListingDemoView.Status.AVAILABLE,
                            LocalDate.of(2026, 6, 1)));
        });
        matrix.getModeSwitch().setValue(true);
        matrix.goToNextPage();
        assertEquals("Item 6", matrix.getTable().getHeaderRows().getFirst().getHeaderCells().get(1).getText());
        assertTrue(maxRequested.get() <= 6);
        listing.getGrid().getDataProvider().refreshAll();
        assertEquals(1, matrix.getCurrentPage());
    }

    @Test
    void gridOnlyListingStaysUnchanged() {
        BeanListing<BeanListingDemoView.Product> listing =
                BeanListing.builder(BeanListingDemoView.Product.class, true).build();
        assertSame(listing.getGrid(), listing.getComponent());
        assertTrue(listing.getMatrixView().isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> BeanListing.builder(BeanListingDemoView.Product.class, true).matrixView(11));
    }

    @Test
    void matrixUsesInMemoryFilteringAndRefreshesToFirstPage() {
        BeanListing<BeanListingDemoView.Product> listing = ItemListingMatrixDemoView.createExample();
        var dataView = listing.getGrid().setItems(IntStream.rangeClosed(1, 12)
                .mapToObj(i -> new BeanListingDemoView.Product(i, "Local " + i,
                        "Equipment", i, true, BeanListingDemoView.Status.AVAILABLE,
                        LocalDate.of(2026, 6, 1)))
                .toList());
        ItemListingMatrix<BeanListingDemoView.Product, String> matrix = listing.getMatrixView().orElseThrow();
        matrix.getModeSwitch().setValue(true);
        matrix.goToNextPage();
        dataView.setFilter(product -> product.getId() > 10);
        assertEquals(1, matrix.getCurrentPage());
        assertEquals(3, matrix.getTable().getHeaderRows().getFirst().getHeaderCells().size());
        assertEquals("Local 11", matrix.getTable().getHeaderRows().getFirst().getHeaderCells().get(1).getText());
        dataView.setSortOrder(BeanListingDemoView.Product::getId, SortDirection.DESCENDING);
        assertEquals("Local 12", matrix.getTable().getHeaderRows().getFirst().getHeaderCells().get(1).getText());
    }
}
