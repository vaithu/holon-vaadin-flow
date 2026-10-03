package com.holonplatform.vaadin.flow.demo.data.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;

/**
 * Integration tests for the column-aware {@link ProductService#fetch(int, int, String,
 * com.holonplatform.core.query.QueryFilter, com.holonplatform.core.query.QuerySort, List)}
 * against the seeded demo database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class TestProductServiceFetch {

    @Autowired
    ProductService service;

    @Test
    void pagesWithOffsetAndLimitInTheRightOrder() {
        List<Product> all = service.fetch(0, 100, null, null, null, List.of()).toList();
        List<Product> page = service.fetch(2, 3, null, null, null, List.of()).toList();

        assertEquals(3, page.size());
        assertEquals(all.subList(2, 5).stream().map(Product::getId).toList(),
                page.stream().map(Product::getId).toList());
    }

    @Test
    void appliesSearchTextWithoutStructuredFilter() {
        List<Product> rows = service.fetch(0, 100, "furniture", null, null, List.of()).toList();

        assertEquals(service.count("furniture"), rows.size());
        assertTrue(rows.stream().allMatch(p ->
                p.getCategory().equalsIgnoreCase("furniture")
                        || p.getName().toLowerCase().contains("furniture")));
    }

    @Test
    void combinesSearchTextWithStructuredFilter() {
        var active = PathProperty.create("active", Boolean.class).eq(true);

        List<Product> rows = service.fetch(0, 100, "furniture", active, null, List.of()).toList();

        assertEquals(service.count("furniture", active), rows.size());
        assertTrue(rows.stream().allMatch(Product::isActive));
    }

    @Test
    void defaultsToNameOrderAndSupportsColumnProjection() {
        List<Product> rows = service.fetch(0, 100, null, null, null, List.of("id", "name")).toList();
        List<String> names = rows.stream().map(Product::getName).toList();

        assertEquals(names.stream().sorted().toList(), names);
    }
}
