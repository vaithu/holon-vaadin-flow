/*
 * Copyright 2016-2024 Axioma srl.
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

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.TimelineStepperBuilder;
import com.holonplatform.vaadin.flow.components.builders.TimelineStepperConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper.AuditEntry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TimelineStepper}, {@link TimelineStepperBuilder}
 * and {@link TimelineStepperConfigurator}.
 *
 * <p>Tests run without a live Vaadin UI or VaadinService. Browser-side
 * interactions (IntersectionObserver, JS function calls, DOM events) are not
 * verified here; only the Java-layer attribute management, JSON serialization,
 * and builder fluency are covered.</p>
 */
class TestTimelineStepper {

    // =========================================================================
    // TimelineStepper — default construction
    // =========================================================================

    @Test
    void timeline_defaultConstructor_tagIsCorrect() {
        TimelineStepper t = new TimelineStepper();
        assertEquals("timeline-stepper", t.getElement().getTag());
    }

    @Test
    void timeline_defaultConstructor_pageSizeDefault() {
        TimelineStepper t = new TimelineStepper();
        assertEquals(20, t.getPageSize());
    }

    @Test
    void timeline_defaultConstructor_hasMoreFalse() {
        TimelineStepper t = new TimelineStepper();
        assertFalse(t.isHasMore());
    }

    @Test
    void timeline_defaultConstructor_loadingFalse() {
        TimelineStepper t = new TimelineStepper();
        assertFalse(t.isLoading());
    }

    // =========================================================================
    // TimelineStepper — pageSize
    // =========================================================================

    @Test
    void setPageSize_setsAttribute() {
        TimelineStepper t = new TimelineStepper();
        t.setPageSize(30);
        assertEquals("30", t.getElement().getAttribute("page-size"));
    }

    @Test
    void getPageSize_afterSet_returnsSetValue() {
        TimelineStepper t = new TimelineStepper();
        t.setPageSize(50);
        assertEquals(50, t.getPageSize());
    }

    @Test
    void getPageSize_whenAttributeAbsent_returnsDefault() {
        TimelineStepper t = new TimelineStepper();
        t.getElement().removeAttribute("page-size");
        assertEquals(20, t.getPageSize());
    }

    // =========================================================================
    // TimelineStepper — hasMore
    // =========================================================================

    @Test
    void setHasMore_true_setsAttributeToTrue() {
        TimelineStepper t = new TimelineStepper();
        t.setHasMore(true);
        assertEquals("true", t.getElement().getAttribute("has-more"));
        assertTrue(t.isHasMore());
    }

    @Test
    void setHasMore_false_setsAttributeToFalse() {
        TimelineStepper t = new TimelineStepper();
        t.setHasMore(true);
        t.setHasMore(false);
        assertEquals("false", t.getElement().getAttribute("has-more"));
        assertFalse(t.isHasMore());
    }

    // =========================================================================
    // TimelineStepper — loading
    // =========================================================================

    @Test
    void setLoading_true_setsAttributeToTrue() {
        TimelineStepper t = new TimelineStepper();
        t.setLoading(true);
        assertEquals("true", t.getElement().getAttribute("loading"));
        assertTrue(t.isLoading());
    }

    @Test
    void setLoading_false_setsAttributeToFalse() {
        TimelineStepper t = new TimelineStepper();
        t.setLoading(true);
        t.setLoading(false);
        assertFalse(t.isLoading());
    }

    // =========================================================================
    // TimelineStepper — setItems
    // =========================================================================

    @Test
    void setItems_setsAttribute() {
        TimelineStepper t = new TimelineStepper();
        t.setItems("[{\"id\":\"1\"}]");
        assertEquals("[{\"id\":\"1\"}]", t.getElement().getAttribute("items"));
    }

    // =========================================================================
    // TimelineStepper — event listeners
    // =========================================================================

    @Test
    void addLoadMoreListener_doesNotThrow() {
        TimelineStepper t = new TimelineStepper();
        assertDoesNotThrow(() -> t.addLoadMoreListener(e -> { /* no-op */ }));
    }

    @Test
    void addEntryClickListener_doesNotThrow() {
        TimelineStepper t = new TimelineStepper();
        assertDoesNotThrow(() -> t.addEntryClickListener(e -> { /* no-op */ }));
    }

    // =========================================================================
    // AuditEntry — toJson
    // =========================================================================

    @Test
    void auditEntry_toJson_containsId() {
        String json = new AuditEntry("id-001", "2026-01-01T00:00:00Z", "Alice", "Login").toJson();
        assertTrue(json.contains("\"id\":\"id-001\""));
    }

    @Test
    void auditEntry_toJson_containsTimestamp() {
        String json = new AuditEntry("1", "2026-04-09T10:00:00Z", "Bob", "Action").toJson();
        assertTrue(json.contains("\"timestamp\":\"2026-04-09T10:00:00Z\""));
    }

    @Test
    void auditEntry_toJson_containsActor() {
        String json = new AuditEntry("1", "ts", "Jane Smith", "Logout").toJson();
        assertTrue(json.contains("\"actor\":\"Jane Smith\""));
    }

    @Test
    void auditEntry_toJson_containsAction() {
        String json = new AuditEntry("1", "ts", "Actor", "Payment processed").toJson();
        assertTrue(json.contains("\"action\":\"Payment processed\""));
    }

    @Test
    void auditEntry_toJson_defaultSeverityIsInfo() {
        String json = new AuditEntry("1", "ts", "Actor", "Action").toJson();
        assertTrue(json.contains("\"severity\":\"info\""));
    }

    @Test
    void auditEntry_toJson_withSeverity_setsCorrectSeverity() {
        String json = new AuditEntry("1", "ts", "Actor", "Action")
                .severity("error").toJson();
        assertTrue(json.contains("\"severity\":\"error\""));
    }

    @Test
    void auditEntry_toJson_withActorRole_includesActorRole() {
        String json = new AuditEntry("1", "ts", "Admin", "Deleted user")
                .actorRole("Super Admin").toJson();
        assertTrue(json.contains("\"actorRole\":\"Super Admin\""));
    }

    @Test
    void auditEntry_toJson_withoutActorRole_noActorRoleField() {
        String json = new AuditEntry("1", "ts", "Admin", "Action").toJson();
        assertFalse(json.contains("actorRole"));
    }

    @Test
    void auditEntry_toJson_withDetail_includesDetail() {
        String json = new AuditEntry("1", "ts", "Actor", "Action")
                .detail("Extra context here").toJson();
        assertTrue(json.contains("\"detail\":\"Extra context here\""));
    }

    @Test
    void auditEntry_toJson_withoutDetail_noDetailField() {
        String json = new AuditEntry("1", "ts", "Actor", "Action").toJson();
        assertFalse(json.contains("\"detail\""));
    }

    @Test
    void auditEntry_toJson_withCategory_includesCategory() {
        String json = new AuditEntry("1", "ts", "Actor", "Action")
                .category("Billing").toJson();
        assertTrue(json.contains("\"category\":\"Billing\""));
    }

    @Test
    void auditEntry_toJson_withoutCategory_noCategoryField() {
        String json = new AuditEntry("1", "ts", "Actor", "Action").toJson();
        assertFalse(json.contains("\"category\""));
    }

    @Test
    void auditEntry_toJson_isValidJsonObject() {
        String json = new AuditEntry("e1", "2026-01-01T00:00:00Z", "Alice", "Updated profile")
                .actorRole("Admin").detail("Changed email").severity("success").category("Auth")
                .toJson();
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
        // Basic JSON structure validation
        assertTrue(json.contains(":"));
    }

    @Test
    void auditEntry_toJson_escapesDoubleQuoteInValue() {
        String json = new AuditEntry("1", "ts", "Actor", "He said \"hello\"").toJson();
        assertTrue(json.contains("He said \\\"hello\\\""));
    }

    @Test
    void auditEntry_toJson_escapesBackslashInValue() {
        String json = new AuditEntry("1", "ts", "C:\\Users\\alice", "Login").toJson();
        assertTrue(json.contains("C:\\\\Users\\\\alice"));
    }

    @Test
    void auditEntry_toJson_escapesBackslashBeforeDoubleQuote() {
        // label: path\"value
        String json = new AuditEntry("1", "ts", "Actor", "path\\\"value").toJson();
        assertTrue(json.contains("path\\\\\\\"value"));
    }

    @Test
    void auditEntry_fluent_actorRole_returnsThis() {
        AuditEntry e = new AuditEntry("1", "ts", "A", "B");
        assertSame(e, e.actorRole("Admin"));
    }

    @Test
    void auditEntry_fluent_detail_returnsThis() {
        AuditEntry e = new AuditEntry("1", "ts", "A", "B");
        assertSame(e, e.detail("Some detail"));
    }

    @Test
    void auditEntry_fluent_severity_returnsThis() {
        AuditEntry e = new AuditEntry("1", "ts", "A", "B");
        assertSame(e, e.severity("warning"));
    }

    @Test
    void auditEntry_fluent_category_returnsThis() {
        AuditEntry e = new AuditEntry("1", "ts", "A", "B");
        assertSame(e, e.category("Auth"));
    }

    // =========================================================================
    // TimelineStepperBuilder
    // =========================================================================

    @Test
    void builder_staticFactory_returnsNonNull() {
        assertNotNull(TimelineStepper.builder());
    }

    @Test
    void builder_create_returnsNonNull() {
        assertNotNull(TimelineStepperBuilder.create());
    }

    @Test
    void builder_pageSize_setsAttribute() {
        TimelineStepper t = TimelineStepper.builder().pageSize(25).build();
        assertEquals(25, t.getPageSize());
    }

    @Test
    void builder_hasMore_setsAttribute() {
        TimelineStepper t = TimelineStepper.builder().hasMore(true).build();
        assertTrue(t.isHasMore());
    }

    @Test
    void builder_loading_setsAttribute() {
        TimelineStepper t = TimelineStepper.builder().loading(true).build();
        assertTrue(t.isLoading());
    }

    @Test
    void builder_width_setsWidth() {
        TimelineStepper t = TimelineStepper.builder().width("720px").build();
        assertEquals("720px", t.getWidth());
    }

    @Test
    void builder_fullWidth_setsWidth() {
        TimelineStepper t = TimelineStepper.builder().fullWidth().build();
        assertEquals("100%", t.getWidth());
    }

    @Test
    void builder_id_setsId() {
        TimelineStepper t = TimelineStepper.builder().id("my-timeline").build();
        assertTrue(t.getId().isPresent());
        assertEquals("my-timeline", t.getId().get());
    }

    @Test
    void builder_styleName_addsClass() {
        TimelineStepper t = TimelineStepper.builder().styleName("timeline--narrow").build();
        assertTrue(t.getClassNames().contains("timeline--narrow"));
    }

    @Test
    void builder_visible_false_hidesComponent() {
        TimelineStepper t = TimelineStepper.builder().visible(false).build();
        assertFalse(t.isVisible());
    }

    @Test
    void builder_enabled_false_disablesComponent() {
        TimelineStepper t = TimelineStepper.builder().enabled(false).build();
        assertFalse(t.isEnabled());
    }

    @Test
    void builder_onLoadMore_doesNotThrow() {
        assertDoesNotThrow(() -> TimelineStepper.builder()
                .onLoadMore(e -> { /* no-op */ })
                .build());
    }

    @Test
    void builder_onEntryClick_doesNotThrow() {
        assertDoesNotThrow(() -> TimelineStepper.builder()
                .onEntryClick(e -> { /* no-op */ })
                .build());
    }

    @Test
    void builder_fluent_chainReturnsSameBuilder() {
        TimelineStepperBuilder b = TimelineStepper.builder();
        assertSame(b, b.pageSize(10));
        assertSame(b, b.hasMore(false));
        assertSame(b, b.loading(false));
    }

    @Test
    void builder_fullChain_buildsCorrectComponent() {
        TimelineStepper t = TimelineStepper.builder()
                .pageSize(15)
                .hasMore(true)
                .loading(true)
                .width("600px")
                .id("audit-timeline")
                .styleName("timeline--narrow")
                .build();

        assertEquals(15,    t.getPageSize());
        assertTrue(t.isHasMore());
        assertTrue(t.isLoading());
        assertEquals("600px", t.getWidth());
        assertTrue(t.getId().isPresent());
        assertEquals("audit-timeline", t.getId().get());
        assertTrue(t.getClassNames().contains("timeline--narrow"));
    }

    // =========================================================================
    // TimelineStepperConfigurator
    // =========================================================================

    @Test
    void configurator_configure_returnsNonNull() {
        assertNotNull(TimelineStepperConfigurator.configure(new TimelineStepper()));
    }

    @Test
    void configurator_configure_changesPageSize() {
        TimelineStepper t = new TimelineStepper();
        TimelineStepperConfigurator.configure(t).pageSize(40);
        assertEquals(40, t.getPageSize());
    }

    @Test
    void configurator_configure_changesHasMore() {
        TimelineStepper t = new TimelineStepper();
        TimelineStepperConfigurator.configure(t).hasMore(true);
        assertTrue(t.isHasMore());
    }

    @Test
    void configurator_staticFactory_fromComponent_returnsNonNull() {
        assertNotNull(TimelineStepper.configure(new TimelineStepper()));
    }

    @Test
    void configurator_staticFactory_changesLoading() {
        TimelineStepper t = new TimelineStepper();
        TimelineStepper.configure(t).loading(true);
        assertTrue(t.isLoading());
    }

    // =========================================================================
    // Components entry-point integration
    // =========================================================================

    @Test
    void components_timelineStepper_returnsNonNull() {
        assertNotNull(Components.timelineStepper());
    }

    @Test
    void components_timelineStepper_buildsComponent() {
        TimelineStepper t = Components.timelineStepper().pageSize(10).build();
        assertNotNull(t);
        assertEquals(10, t.getPageSize());
    }

    @Test
    void components_configure_timeline_returnsNonNull() {
        assertNotNull(Components.configure(new TimelineStepper()));
    }

    // =========================================================================
    // appendEntries with list — JSON array helper
    // =========================================================================

    @Test
    void appendEntries_list_doesNotThrow() {
        TimelineStepper t = new TimelineStepper();
        List<AuditEntry> entries = List.of(
                new AuditEntry("1", "ts", "Alice", "Login").severity("info"),
                new AuditEntry("2", "ts", "Bob",   "Logout").severity("success")
        );
        // callJsFunction is a no-op outside a live UI, but must not throw
        assertDoesNotThrow(() -> t.appendEntries(entries));
    }

    @Test
    void prependEntries_list_doesNotThrow() {
        TimelineStepper t = new TimelineStepper();
        assertDoesNotThrow(() -> t.prependEntries(List.of(
                new AuditEntry("3", "ts", "Eve", "Password changed").severity("warning")
        )));
    }
}

