/*
 * Copyright 2016-2017 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.*;
import com.holonplatform.vaadin.flow.internal.components.RangeInputField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link FilterInput}, {@link FilterInputGroup}, and {@link FilterInputForm}.
 *
 * @since 10.0.0
 */
public class TestFilterInput {

    private static final StringProperty NAME = StringProperty.create("name");
    private static final NumericProperty<Integer> AGE = NumericProperty.integerType("age");

    // -----------------------------------------------------------------------
    // FilterInput.from() — custom factory
    // -----------------------------------------------------------------------

    @Test
    public void testCustomFilterInput_inactiveWhenNoValue() {
        FilterInput<String> fi = FilterInput.from(
                Input.string().build(),
                v -> (v != null && !v.isBlank())
                        ? Optional.of(QueryFilter.isNotNull(NAME))
                        : Optional.empty()
        );
        assertNotNull(fi);
        assertFalse(fi.isActive(), "Filter should be inactive when no value is set");
        assertTrue(fi.getQueryFilter().isEmpty(), "QueryFilter should be empty when inactive");
        assertNotNull(fi.getComponent(), "Component must not be null");
        assertNotNull(fi.getInput(), "Backing input must not be null");
    }

    @Test
    public void testCustomFilterInput_activeAfterSetValue() {
        Input<String> input = Input.string().build();
        FilterInput<String> fi = FilterInput.from(
                input,
                v -> (v != null && !v.isBlank())
                        ? Optional.of(QueryFilter.isNotNull(NAME))
                        : Optional.empty()
        );
        input.setValue("hello");
        assertTrue(fi.isActive(), "Filter should be active after setting a value");
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testCustomFilterInput_resetClearsFilter() {
        Input<String> input = Input.string().build();
        FilterInput<String> fi = FilterInput.from(
                input,
                v -> (v != null && !v.isBlank())
                        ? Optional.of(QueryFilter.isNotNull(NAME))
                        : Optional.empty()
        );
        input.setValue("hello");
        assertTrue(fi.isActive());

        fi.reset();
        assertFalse(fi.isActive(), "Filter should be inactive after reset");
        assertTrue(fi.getQueryFilter().isEmpty());
    }

    // -----------------------------------------------------------------------
    // FilterInput.string()
    // -----------------------------------------------------------------------

    @Test
    public void testStringFilter_inactive() {
        FilterInput<String> fi = FilterInput.string(NAME);
        assertFalse(fi.isActive());
        assertTrue(fi.getQueryFilter().isEmpty());
    }

    @Test
    public void testStringFilter_activeAfterValue() {
        FilterInput<String> fi = FilterInput.string(NAME);
        fi.getInput().setValue("John");
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testStringFilter_blankBecomesInactive() {
        FilterInput<String> fi = FilterInput.string(NAME);
        fi.getInput().setValue("John");
        fi.getInput().setValue("   ");
        assertFalse(fi.isActive(), "Blank string should result in no filter");
    }

    @Test
    public void testStringFilter_ignoreCase_default() {
        FilterInput<String> fi = FilterInput.string(NAME); // default: ignoreCase=true
        fi.getInput().setValue("test");
        assertTrue(fi.isActive());
    }

    @Test
    public void testStringFilter_caseSensitive() {
        FilterInput<String> fi = FilterInput.string(NAME, false);
        fi.getInput().setValue("test");
        assertTrue(fi.isActive());
    }

    // -----------------------------------------------------------------------
    // FilterInput.number()
    // -----------------------------------------------------------------------

    @Test
    public void testNumberFilter_inactive() {
        FilterInput<Integer> fi = FilterInput.number(AGE, Integer.class);
        assertFalse(fi.isActive());
        assertTrue(fi.getQueryFilter().isEmpty());
    }

    @Test
    public void testNumberFilter_activeAfterValue() {
        FilterInput<Integer> fi = FilterInput.number(AGE, Integer.class);
        fi.getInput().setValue(42);
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testNumberFilter_resetMakesInactive() {
        FilterInput<Integer> fi = FilterInput.number(AGE, Integer.class);
        fi.getInput().setValue(42);
        fi.reset();
        assertFalse(fi.isActive());
    }

    @Test
    public void testNumberRangeFilter_inactiveWhenEmpty() {
        FilterInput<FilterInput.Range<Integer>> fi = FilterInput.numberRange(AGE, Integer.class);
        assertFalse(fi.isActive());
        assertTrue(fi.getQueryFilter().isEmpty());
    }

    @Test
    public void testNumberRangeFilter_fromOnly() {
        FilterInput<FilterInput.Range<Integer>> fi = FilterInput.numberRange(AGE, Integer.class);
        fi.getInput().setValue(new FilterInput.Range<>(18, null));
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testNumberRangeFilter_toOnly() {
        FilterInput<FilterInput.Range<Integer>> fi = FilterInput.numberRange(AGE, Integer.class);
        fi.getInput().setValue(new FilterInput.Range<>(null, 65));
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testNumberRangeFilter_between() {
        FilterInput<FilterInput.Range<Integer>> fi = FilterInput.numberRange(AGE, Integer.class);
        fi.getInput().setValue(new FilterInput.Range<>(18, 65));
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testNumberRangeFilter_invalidOrderBecomesInactive() {
        FilterInput<FilterInput.Range<Integer>> fi = FilterInput.numberRange(AGE, Integer.class);
        fi.getInput().setValue(new FilterInput.Range<>(65, 18));
        assertFalse(fi.isActive());
        assertTrue(fi.getQueryFilter().isEmpty());
    }

    @Test
    public void testRangeValueHelpers() {
        FilterInput.Range<Integer> empty = FilterInput.Range.empty();
        assertTrue(empty.isEmpty());
        assertTrue(empty.isValid());

        FilterInput.Range<Integer> valid = new FilterInput.Range<>(1, 2);
        assertTrue(valid.hasFrom());
        assertTrue(valid.hasTo());
        assertTrue(valid.isValid());

        FilterInput.Range<Integer> invalid = new FilterInput.Range<>(2, 1);
        assertFalse(invalid.isValid());
    }

    @Test
    public void testRangeInputField_defaultUx() {
        RangeInputField<Integer> field = new RangeInputField<>(
                Input.number(Integer.class).build(),
                Input.number(Integer.class).build());

        assertTrue(field.getClassNames().contains("range-input-field"));
        assertTrue(field.getFromInput().getComponent().getClassNames().contains("range-input-field-from"));
        assertTrue(field.getToInput().getComponent().getClassNames().contains("range-input-field-to"));

        assertEquals("From", field.getFromInput().hasPlaceholder().map(p -> p.getPlaceholder()).orElse(null));
        assertEquals("To", field.getToInput().hasPlaceholder().map(p -> p.getPlaceholder()).orElse(null));

        assertEquals(3, field.getChildren().count());
        assertEquals("-", field.getChildren().skip(1).findFirst().map(c -> ((Span) c).getText()).orElse(null));
    }

    @Test
    public void testRangeInputField_customUx() {
        RangeInputField<Integer> field = new RangeInputField<>(
                Input.number(Integer.class).build(),
                Input.number(Integer.class).build(),
                "Min",
                "Max",
                "to");

        assertEquals("Min", field.getFromInput().hasPlaceholder().map(p -> p.getPlaceholder()).orElse(null));
        assertEquals("Max", field.getToInput().hasPlaceholder().map(p -> p.getPlaceholder()).orElse(null));
        assertEquals("to", field.getChildren().skip(1).findFirst().map(c -> ((Span) c).getText()).orElse(null));
    }

    // -----------------------------------------------------------------------
    // FilterInput.localDate()
    // -----------------------------------------------------------------------

    @Test
    public void testLocalDateFilter_inactive() {
        com.holonplatform.core.property.Property<LocalDate> BIRTH =
                com.holonplatform.core.property.PathProperty.create("birth", LocalDate.class);
        FilterInput<LocalDate> fi = FilterInput.localDate(BIRTH);
        assertFalse(fi.isActive());
        assertTrue(fi.getQueryFilter().isEmpty());
    }

    @Test
    public void testLocalDateFilter_active() {
        com.holonplatform.core.property.Property<LocalDate> BIRTH =
                com.holonplatform.core.property.PathProperty.create("birth", LocalDate.class);
        FilterInput<LocalDate> fi = FilterInput.localDate(BIRTH);
        fi.getInput().setValue(LocalDate.of(2000, 1, 1));
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testLocalDateRangeFilter_active() {
        com.holonplatform.core.property.Property<LocalDate> BIRTH =
                com.holonplatform.core.property.PathProperty.create("birth", LocalDate.class);
        FilterInput<FilterInput.Range<LocalDate>> fi = FilterInput.localDateRange(BIRTH);
        fi.getInput().setValue(new FilterInput.Range<>(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testLocalDateTimeRangeFilter_active() {
        com.holonplatform.core.property.Property<LocalDateTime> CREATED =
                com.holonplatform.core.property.PathProperty.create("created", LocalDateTime.class);
        FilterInput<FilterInput.Range<LocalDateTime>> fi = FilterInput.localDateTimeRange(CREATED);
        fi.getInput().setValue(new FilterInput.Range<>(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 1, 31, 23, 59)));
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    // -----------------------------------------------------------------------
    // FilterInput.bool()
    // -----------------------------------------------------------------------

    @Test
    public void testBoolFilter_inactive() {
        com.holonplatform.core.property.Property<Boolean> ACTIVE =
                com.holonplatform.core.property.PathProperty.create("active", Boolean.class);
        FilterInput<Boolean> fi = FilterInput.bool(ACTIVE);
        assertFalse(fi.isActive());
        assertTrue(fi.getQueryFilter().isEmpty());
    }

    @Test
    public void testBoolFilter_activeTrue() {
        com.holonplatform.core.property.Property<Boolean> ACTIVE =
                com.holonplatform.core.property.PathProperty.create("active", Boolean.class);
        FilterInput<Boolean> fi = FilterInput.bool(ACTIVE);
        fi.getInput().setValue(Boolean.TRUE);
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testBoolFilter_activeFalse() {
        com.holonplatform.core.property.Property<Boolean> ACTIVE =
                com.holonplatform.core.property.PathProperty.create("active", Boolean.class);
        FilterInput<Boolean> fi = FilterInput.bool(ACTIVE);
        fi.getInput().setValue(Boolean.FALSE);
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testBoolFilter_nullMeansInactive() {
        com.holonplatform.core.property.Property<Boolean> ACTIVE =
                com.holonplatform.core.property.PathProperty.create("active", Boolean.class);
        FilterInput<Boolean> fi = FilterInput.bool(ACTIVE);
        fi.getInput().setValue(Boolean.TRUE);
        fi.reset();
        assertFalse(fi.isActive(), "Null value should deactivate bool filter");
    }

    // -----------------------------------------------------------------------
    // FilterInput.enumeration()
    // -----------------------------------------------------------------------

    enum Status { ACTIVE, INACTIVE, PENDING }

    @Test
    public void testEnumFilter_inactive() {
        com.holonplatform.core.property.Property<Status> STATUS =
                com.holonplatform.core.property.PathProperty.create("status", Status.class);
        FilterInput<Status> fi = FilterInput.enumeration(STATUS, Status.class);
        assertFalse(fi.isActive());
        assertTrue(fi.getQueryFilter().isEmpty());
    }

    @Test
    public void testEnumFilter_active() {
        com.holonplatform.core.property.Property<Status> STATUS =
                com.holonplatform.core.property.PathProperty.create("status", Status.class);
        FilterInput<Status> fi = FilterInput.enumeration(STATUS, Status.class);
        fi.getInput().setValue(Status.ACTIVE);
        assertTrue(fi.isActive());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testFilterInput_of_infersSupportedTypes() {
        com.holonplatform.core.property.Property<Boolean> ACTIVE =
                com.holonplatform.core.property.PathProperty.create("active", Boolean.class);
        com.holonplatform.core.property.Property<LocalDate> BIRTH =
                com.holonplatform.core.property.PathProperty.create("birth", LocalDate.class);
        com.holonplatform.core.property.Property<Status> STATUS =
                com.holonplatform.core.property.PathProperty.create("status", Status.class);

        FilterInput<String> stringFilter = FilterInput.of(NAME);
        FilterInput<Integer> numberFilter = FilterInput.of(AGE);
        FilterInput<Boolean> boolFilter = FilterInput.of(ACTIVE);
        FilterInput<LocalDate> dateFilter = FilterInput.of(BIRTH);
        FilterInput<Status> enumFilter = FilterInput.of(STATUS);

        stringFilter.getInput().setValue("john");
        numberFilter.getInput().setValue(7);
        boolFilter.getInput().setValue(Boolean.TRUE);
        dateFilter.getInput().setValue(LocalDate.now());
        enumFilter.getInput().setValue(Status.ACTIVE);

        assertTrue(stringFilter.getQueryFilter().isPresent());
        assertTrue(numberFilter.getQueryFilter().isPresent());
        assertTrue(boolFilter.getQueryFilter().isPresent());
        assertTrue(dateFilter.getQueryFilter().isPresent());
        assertTrue(enumFilter.getQueryFilter().isPresent());
    }

    @Test
    public void testFilterInput_of_unsupportedTypeThrows() {
        com.holonplatform.core.property.Property<Object> UNSUPPORTED =
                com.holonplatform.core.property.PathProperty.create("unsupported", Object.class);
        assertThrows(IllegalArgumentException.class, () -> FilterInput.of(UNSUPPORTED));
    }

    // -----------------------------------------------------------------------
    // FilterChangeListener
    // -----------------------------------------------------------------------

    @Test
    public void testFilterChangeListener_firedOnValueChange() {
        AtomicInteger callCount = new AtomicInteger(0);
        FilterInput<String> fi = FilterInput.string(NAME);

        fi.addFilterChangeListener(event -> {
            callCount.incrementAndGet();
            assertNotNull(event);
            assertNotNull(event.getSource());
            assertSame(fi, event.getSource());
        });

        fi.getInput().setValue("John");
        assertEquals(1, callCount.get(), "Listener must fire once on value change");

        fi.getInput().setValue("Jane");
        assertEquals(2, callCount.get(), "Listener must fire on each value change");
    }

    @Test
    public void testFilterChangeListener_firedOnReset() {
        AtomicBoolean filterActiveDuringChange = new AtomicBoolean(true);
        FilterInput<String> fi = FilterInput.string(NAME);
        fi.getInput().setValue("initial");

        fi.addFilterChangeListener(event -> filterActiveDuringChange.set(event.isFilterActive()));

        fi.reset();
        assertFalse(filterActiveDuringChange.get(),
                "After reset, filter change event should report inactive");
    }

    @Test
    public void testFilterChangeListener_registration_remove() {
        AtomicInteger callCount = new AtomicInteger(0);
        FilterInput<String> fi = FilterInput.string(NAME);

        com.holonplatform.core.Registration reg =
                fi.addFilterChangeListener(event -> callCount.incrementAndGet());

        fi.getInput().setValue("A");
        assertEquals(1, callCount.get());

        reg.remove();
        fi.getInput().setValue("B");
        assertEquals(1, callCount.get(), "Listener should not fire after removal");
    }

    // -----------------------------------------------------------------------
    // FilterInputGroup
    // -----------------------------------------------------------------------

    @Test
    public void testFilterInputGroup_emptyWhenNoFiltersActive() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);
        FilterInput<Integer> ageFilter = FilterInput.number(AGE, Integer.class);

        FilterInputGroup group = FilterInputGroup.builder()
                .withFilter(NAME, nameFilter)
                .withFilter(AGE, ageFilter)
                .build();

        assertNotNull(group);
        assertFalse(group.isAnyActive());
        assertTrue(group.getQueryFilter().isEmpty(),
                "Combined filter must be empty when no child is active");
    }

    @Test
    public void testFilterInputGroup_singleActiveFilter() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);
        FilterInput<Integer> ageFilter = FilterInput.number(AGE, Integer.class);

        FilterInputGroup group = FilterInputGroup.builder()
                .withFilter(NAME, nameFilter)
                .withFilter(AGE, ageFilter)
                .build();

        nameFilter.getInput().setValue("John");

        assertTrue(group.isAnyActive());
        assertTrue(group.getQueryFilter().isPresent(),
                "Combined filter must be present when at least one child is active");
    }

    @Test
    public void testFilterInputGroup_multipleActiveFiltersAreAndJoined() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);
        FilterInput<Integer> ageFilter = FilterInput.number(AGE, Integer.class);

        FilterInputGroup group = FilterInputGroup.builder()
                .withFilter(NAME, nameFilter)
                .withFilter(AGE, ageFilter)
                .build();

        nameFilter.getInput().setValue("John");
        ageFilter.getInput().setValue(30);

        assertTrue(group.isAnyActive());
        // Both filters active → combined filter present (AND-joined)
        assertTrue(group.getQueryFilter().isPresent());
    }

    @Test
    public void testFilterInputGroup_resetAll() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);
        FilterInput<Integer> ageFilter = FilterInput.number(AGE, Integer.class);

        FilterInputGroup group = FilterInputGroup.builder()
                .withFilter(NAME, nameFilter)
                .withFilter(AGE, ageFilter)
                .build();

        nameFilter.getInput().setValue("John");
        ageFilter.getInput().setValue(30);
        assertTrue(group.isAnyActive());

        group.resetAll();
        assertFalse(group.isAnyActive(), "Group should be inactive after resetAll");
        assertTrue(group.getQueryFilter().isEmpty());
    }

    @Test
    public void testFilterInputGroup_getFilterInput() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);
        FilterInput<Integer> ageFilter = FilterInput.number(AGE, Integer.class);

        FilterInputGroup group = FilterInputGroup.builder()
                .withFilter(NAME, nameFilter)
                .withFilter(AGE, ageFilter)
                .build();

        Optional<FilterInput<String>> found = group.getFilterInput(NAME);
        assertTrue(found.isPresent());
        assertSame(nameFilter, found.get());
    }

    @Test
    public void testFilterInputGroup_getPropertyBindings_order() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);
        FilterInput<Integer> ageFilter = FilterInput.number(AGE, Integer.class);

        FilterInputGroup group = FilterInputGroup.builder()
                .withFilter(NAME, nameFilter)
                .withFilter(AGE, ageFilter)
                .build();

        var bindings = group.getPropertyBindings().toList();
        assertEquals(2, bindings.size());
        assertSame(NAME, bindings.get(0).getProperty());
        assertSame(AGE, bindings.get(1).getProperty());
    }

    @Test
    public void testFilterInputGroup_withFilterPropertyOnly() {
        FilterInputGroup group = FilterInputGroup.builder()
                .withFilter(NAME)
                .withFilter(AGE)
                .build();

        assertTrue(group.getFilterInput(NAME).isPresent());
        assertTrue(group.getFilterInput(AGE).isPresent());
    }

    @Test
    public void testFilterInputGroup_groupLevelListener() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);
        FilterInput<Integer> ageFilter = FilterInput.number(AGE, Integer.class);

        FilterInputGroup group = FilterInputGroup.builder()
                .withFilter(NAME, nameFilter)
                .withFilter(AGE, ageFilter)
                .build();

        AtomicInteger groupCalls = new AtomicInteger(0);
        group.addFilterChangeListener(event -> groupCalls.incrementAndGet());

        nameFilter.getInput().setValue("John");
        assertEquals(1, groupCalls.get(), "Group listener must fire on name filter change");

        ageFilter.getInput().setValue(25);
        assertEquals(2, groupCalls.get(), "Group listener must fire on age filter change");
    }

    // -----------------------------------------------------------------------
    // FilterInputForm
    // -----------------------------------------------------------------------

    @Test
    public void testFilterInputForm_formLayout() {
        FilterInputForm<FormLayout> form = FilterInputForm.formLayout()
                .withFilter(NAME, FilterInput.string(NAME))
                .withFilter(AGE, FilterInput.number(AGE, Integer.class))
                .build();

        assertNotNull(form);
        assertNotNull(form.getComponent());
        assertInstanceOf(FormLayout.class, form.getComponent());
        assertNotNull(form.getContent());
    }

    @Test
    public void testFilterInputForm_verticalLayout() {
        FilterInputForm<VerticalLayout> form = FilterInputForm.verticalLayout()
                .withFilter(NAME, FilterInput.string(NAME))
                .build();

        assertNotNull(form);
        assertInstanceOf(VerticalLayout.class, form.getComponent());
    }

    @Test
    public void testFilterInputForm_horizontalLayout() {
        FilterInputForm<HorizontalLayout> form = FilterInputForm.horizontalLayout()
                .withFilter(NAME, FilterInput.string(NAME))
                .build();

        assertNotNull(form);
        assertInstanceOf(HorizontalLayout.class, form.getComponent());
    }

    @Test
    public void testFilterInputForm_customBuilder() {
        VerticalLayout customLayout = new VerticalLayout();
        FilterInputForm<VerticalLayout> form = FilterInputForm.builder(customLayout)
                .withFilter(NAME, FilterInput.string(NAME))
                .build();

        assertNotNull(form);
        assertSame(customLayout, form.getContent());
        assertSame(customLayout, form.getComponent());
    }

    @Test
    public void testFilterInputForm_filterBehaviorInherited() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);
        FilterInput<Integer> ageFilter = FilterInput.number(AGE, Integer.class);

        FilterInputForm<FormLayout> form = FilterInputForm.formLayout()
                .withFilter(NAME, nameFilter)
                .withFilter(AGE, ageFilter)
                .build();

        // Initially inactive
        assertFalse(form.isAnyActive());
        assertTrue(form.getQueryFilter().isEmpty());

        // Activate one filter
        nameFilter.getInput().setValue("Alice");
        assertTrue(form.isAnyActive());
        assertTrue(form.getQueryFilter().isPresent());

        // Activate both
        ageFilter.getInput().setValue(28);
        assertTrue(form.getQueryFilter().isPresent());

        // Reset all
        form.resetAll();
        assertFalse(form.isAnyActive());
        assertTrue(form.getQueryFilter().isEmpty());
    }

    @Test
    public void testFilterInputForm_componentsComposedOnLayout() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);
        FilterInput<Integer> ageFilter = FilterInput.number(AGE, Integer.class);

        FilterInputForm<VerticalLayout> form = FilterInputForm.verticalLayout()
                .withFilter(NAME, nameFilter)
                .withFilter(AGE, ageFilter)
                .build();

        VerticalLayout layout = form.getContent();
        // Both filter components should have been added to the layout
        assertEquals(2, layout.getComponentCount(),
                "Both filter inputs should be composed onto the VerticalLayout");
    }

    @Test
    public void testFilterInputForm_withFilterPropertyOnly() {
        FilterInputForm<VerticalLayout> form = FilterInputForm.verticalLayout()
                .withFilter(NAME)
                .withFilter(AGE)
                .build();

        assertTrue(form.getFilterInput(NAME).isPresent());
        assertTrue(form.getFilterInput(AGE).isPresent());
        assertEquals(2, form.getContent().getComponentCount());
    }

    @Test
    public void testFilterInputForm_customComposer() {
        AtomicBoolean composerCalled = new AtomicBoolean(false);

        FilterInputForm.verticalLayout()
                .withFilter(NAME, FilterInput.string(NAME))
                .composer((layout, group) -> composerCalled.set(true))
                .build();

        assertTrue(composerCalled.get(), "Custom composer must be invoked during build()");
    }

    @Test
    public void testFilterInputForm_groupListenerFromForm() {
        FilterInput<String> nameFilter = FilterInput.string(NAME);

        FilterInputForm<FormLayout> form = FilterInputForm.formLayout()
                .withFilter(NAME, nameFilter)
                .build();

        AtomicInteger callCount = new AtomicInteger(0);
        form.addFilterChangeListener(event -> callCount.incrementAndGet());

        nameFilter.getInput().setValue("Bob");
        assertEquals(1, callCount.get(),
                "Group-level listener on form must fire on filter change");
    }

    // -----------------------------------------------------------------------
    // Null guard tests
    // -----------------------------------------------------------------------

    @Test
    public void testFilterInput_nullInputThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> FilterInput.from(null, v -> Optional.empty()));
    }

    @Test
    public void testFilterInput_nullConverterThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> FilterInput.from(Input.string().build(), null));
    }

    @Test
    public void testFilterInput_nullPropertyThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> FilterInput.string(null));
    }

    @Test
    public void testFilterInputGroup_nullPropertyThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> FilterInputGroup.builder().withFilter(null, FilterInput.string(NAME)));
    }

    @Test
    public void testFilterInputGroup_nullFilterInputThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> FilterInputGroup.builder().withFilter(NAME, null));
    }

    // -----------------------------------------------------------------------
    // Components.input builders
    // -----------------------------------------------------------------------

    @Test
    public void testComponentsInput_stringBuilder() {
        Input<String> input = Components.input.string().build();
        FilterInput<String> fi = FilterInput.from(input,
                v -> (v != null && !v.trim().isEmpty()) ? Optional.of(QueryFilter.isNotNull(NAME)) : Optional.empty());

        assertNotNull(fi);
        fi.getInput().setValue("john");
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testComponentsInput_numberBuilder() {
        Input<Integer> input = Components.input.number(Integer.class).build();
        FilterInput<Integer> fi = FilterInput.from(input,
                v -> (v != null) ? Optional.of(QueryFilter.isNotNull(NAME)) : Optional.empty());

        assertNotNull(fi);
        fi.getInput().setValue(10);
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testComponentsInput_booleanBuilder() {
        Input<Boolean> input = Components.input.boolean_().build();
        FilterInput<Boolean> fi = FilterInput.from(input,
                v -> (v != null) ? Optional.of(QueryFilter.isNotNull(NAME)) : Optional.empty());

        assertNotNull(fi);
        fi.getInput().setValue(Boolean.TRUE);
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testComponentsInput_localDateBuilder() {
        Input<LocalDate> input = Components.input.localDate().build();
        FilterInput<LocalDate> fi = FilterInput.from(input,
                v -> (v != null) ? Optional.of(QueryFilter.isNotNull(NAME)) : Optional.empty());

        assertNotNull(fi);
        fi.getInput().setValue(LocalDate.now());
        assertTrue(fi.getQueryFilter().isPresent());
    }

    @Test
    public void testComponentsInput_enumSelectBuilder() {
        Input<Status> input = Components.input.enumSelect(Status.class).build();
        assertNotNull(input);
        input.setValue(Status.PENDING);
        assertEquals(Status.PENDING, input.getValue());
    }

    @Test
    public void testComponentsInput_propertyGroupBuilder() {
        var group = Components.input.propertyGroup(NAME, AGE).build();
        assertNotNull(group);
        assertEquals(2, group.getProperties().size());
    }

    @Test
    public void testComponentsInput_formBuilder() {
        var form = Components.input.form(NAME).build();
        assertNotNull(form);
        assertInstanceOf(FormLayout.class, form.getComponent());
    }

    @Test
    public void testComponentsInput_formBuilderWithCustomContent() {
        VerticalLayout layout = new VerticalLayout();
        var form = Components.input.form(layout, NAME).build();
        assertNotNull(form);
        assertSame(layout, form.getComponent());
    }

    @Test
    public void testComponentsInput_formVerticalBuilder() {
        var form = Components.input.formVertical(NAME).build();
        assertNotNull(form);
        assertInstanceOf(VerticalLayout.class, form.getComponent());
    }

    @Test
    public void testComponentsInput_formHorizontalBuilder() {
        var form = Components.input.formHorizontal(NAME).build();
        assertNotNull(form);
        assertInstanceOf(HorizontalLayout.class, form.getComponent());
    }
}

