package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.components.FilterOperator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestFilterOperator {

    @Test
    void testStringOperators() {
        var ops = FilterOperator.forType(String.class);
        assertTrue(ops.contains(FilterOperator.EQUALS));
        assertTrue(ops.contains(FilterOperator.CONTAINS));
        assertTrue(ops.contains(FilterOperator.IS_EMPTY));
        assertTrue(ops.contains(FilterOperator.IN));
        assertEquals(10, ops.size());
    }

    @Test
    void testBooleanOperators() {
        var ops = FilterOperator.forType(Boolean.class);
        assertEquals(List.of(FilterOperator.EQUALS, FilterOperator.NOT_EQUALS), ops);
    }

    @Test
    void testPrimitiveBooleanOperators() {
        var ops = FilterOperator.forType(boolean.class);
        assertEquals(2, ops.size());
    }

    @Test
    void testEnumOperators() {
        var ops = FilterOperator.forType(TestEnum.class);
        assertTrue(ops.contains(FilterOperator.EQUALS));
        assertTrue(ops.contains(FilterOperator.IN));
        assertEquals(4, ops.size());
    }

    @Test
    void testLocalDateOperators() {
        var ops = FilterOperator.forType(LocalDate.class);
        assertTrue(ops.contains(FilterOperator.BEFORE));
        assertTrue(ops.contains(FilterOperator.AFTER));
        assertTrue(ops.contains(FilterOperator.BETWEEN));
        assertEquals(7, ops.size());
    }

    @Test
    void testLocalDateTimeOperators() {
        var ops = FilterOperator.forType(LocalDateTime.class);
        assertTrue(ops.contains(FilterOperator.ON_OR_BEFORE));
        assertTrue(ops.contains(FilterOperator.BETWEEN));
        assertEquals(7, ops.size());
    }

    @Test
    void testIntegerOperators() {
        var ops = FilterOperator.forType(Integer.class);
        assertTrue(ops.contains(FilterOperator.GREATER_THAN));
        assertTrue(ops.contains(FilterOperator.LESS_THAN));
        assertTrue(ops.contains(FilterOperator.BETWEEN));
        assertEquals(7, ops.size());
    }

    @Test
    void testPrimitiveIntOperators() {
        var ops = FilterOperator.forType(int.class);
        assertEquals(7, ops.size());
        assertTrue(ops.contains(FilterOperator.GREATER_THAN));
    }

    @Test
    void testDoubleOperators() {
        var ops = FilterOperator.forType(double.class);
        assertEquals(7, ops.size());
    }

    @Test
    void testNullType() {
        var ops = FilterOperator.forType(null);
        assertTrue(ops.isEmpty());
    }

    @Test
    void testFallbackType() {
        var ops = FilterOperator.forType(Object.class);
        assertEquals(List.of(FilterOperator.EQUALS, FilterOperator.NOT_EQUALS), ops);
    }

    @Test
    void testIsMultiValue() {
        assertTrue(FilterOperator.IN.isMultiValue());
        assertTrue(FilterOperator.NOT_IN.isMultiValue());
        assertFalse(FilterOperator.EQUALS.isMultiValue());
    }

    @Test
    void testIsBetween() {
        assertTrue(FilterOperator.BETWEEN.isBetween());
        assertFalse(FilterOperator.EQUALS.isBetween());
    }

    @Test
    void testIsNullaryCheck() {
        assertTrue(FilterOperator.IS_EMPTY.isNullaryCheck());
        assertTrue(FilterOperator.IS_NOT_EMPTY.isNullaryCheck());
        assertFalse(FilterOperator.CONTAINS.isNullaryCheck());
    }

    @Test
    void testAllLabelsNotNull() {
        for (FilterOperator op : FilterOperator.values()) {
            assertNotNull(op.getLabel());
            assertFalse(op.getLabel().isBlank());
        }
    }

    enum TestEnum { A, B, C }
}
