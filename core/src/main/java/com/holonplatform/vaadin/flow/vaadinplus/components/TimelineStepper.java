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
package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.builders.TimelineStepperBuilder;
import com.holonplatform.vaadin.flow.components.builders.TimelineStepperConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Vertical audit-log / event-history timeline with infinite-scroll lazy loading.
 *
 * <p>Wraps the {@code <timeline-stepper>} Shadow-DOM web component. The component
 * communicates with JavaScript through HTML attributes (for initial configuration)
 * and {@link com.vaadin.flow.dom.Element#callJsFunction} calls (for data mutations
 * after initial render).</p>
 *
 * <h3>Infinite-scroll loading pattern</h3>
 * <pre>{@code
 * TimelineStepper timeline = TimelineStepper.builder()
 *     .pageSize(20)
 *     .hasMore(true)
 *     .width("100%")
 *     .onLoadMore(e -> {
 *         List<AuditEntry> page = auditService.getPage(e.getPage(), e.getPageSize());
 *         timeline.appendEntries(page);
 *         timeline.setHasMore(auditService.hasMore(e.getPage()));
 *     })
 *     .onEntryClick(e -> Notification.show("Selected: " + e.getEntryId()))
 *     .build();
 * }</pre>
 *
 * @see AuditEntry
 * @see TimelineStepperBuilder
 * @see TimelineStepperConfigurator
 */
@com.vaadin.flow.component.Tag("timeline-stepper")
@JsModule("./timeline-stepper.js")
@StyleSheet("context://timeline.css")
public class TimelineStepper extends Component implements HasSize, HasEnabled {

    private static final long serialVersionUID = 1L;

    /** Tracks the number of active {@link EntryClickEvent} listeners. */
    private int clickListenerCount = 0;

    // -----------------------------------------------------------------------
    // Severity enum
    // -----------------------------------------------------------------------

    /**
     * Severity level for a timeline {@link AuditEntry}.
     *
     * <p>Each constant maps to the lowercase JSON value expected by the
     * {@code <timeline-stepper>} web component:</p>
     * <ul>
     *   <li>{@link #INFO}    → {@code "info"}</li>
     *   <li>{@link #SUCCESS} → {@code "success"}</li>
     *   <li>{@link #WARNING} → {@code "warning"}</li>
     *   <li>{@link #ERROR}   → {@code "error"}</li>
     * </ul>
     */
    public enum Severity {
        INFO, SUCCESS, WARNING, ERROR;

        /** Returns the lowercase JSON value used by the web component. */
        public String value() {
            return name().toLowerCase();
        }
    }

    // -----------------------------------------------------------------------
    // AuditEntry — convenience entry builder/DTO
    // -----------------------------------------------------------------------

    /**
     * Convenience builder for a single audit entry.
     *
     * <p>Build an entry, then pass it to {@link TimelineStepper#appendEntries(List)}
     * or serialize it to JSON via {@link #toJson()} for use with
     * {@link TimelineStepper#appendEntries(String)}.</p>
     *
     * <h3>Severity values</h3>
     * <ul>
     *   <li>{@code "info"} (default)</li>
     *   <li>{@code "success"}</li>
     *   <li>{@code "warning"}</li>
     *   <li>{@code "error"}</li>
     * </ul>
     */
    public static class AuditEntry {
        private final String id;
        private final String timestamp;
        private final String actor;
        private final String action;
        private String actorRole;
        private String detail;
        private Severity severity = Severity.INFO;
        private String category;

        /**
         * Creates a new entry with mandatory fields.
         *
         * @param id        unique entry identifier
         * @param timestamp display timestamp (ISO-8601 or any display string)
         * @param actor     name of the actor (e.g. "Jane Smith" or "System")
         * @param action    short description of the action
         */
        public AuditEntry(String id, String timestamp, String actor, String action) {
            this.id        = id;
            this.timestamp = timestamp;
            this.actor     = actor;
            this.action    = action;
        }

        /** Sets the actor's role label (e.g. "Admin"). */
        public AuditEntry actorRole(String r) { this.actorRole = r; return this; }

        /** Sets the optional detail text shown below the action. */
        public AuditEntry detail(String d) { this.detail = d; return this; }

        /**
         * Sets the severity using the type-safe {@link Severity} enum.
         *
         * @param s severity level (not null; defaults to {@link Severity#INFO})
         */
        public AuditEntry severity(Severity s) {
            this.severity = s != null ? s : Severity.INFO;
            return this;
        }

        /**
         * Sets the severity from a string value for convenience.
         * Accepted values (case-insensitive): {@code "info"}, {@code "success"},
         * {@code "warning"}, {@code "error"}. Unrecognised values fall back to
         * {@link Severity#INFO}.
         *
         * @param s severity string
         */
        public AuditEntry severity(String s) {
            if (s == null) { this.severity = Severity.INFO; return this; }
            try { this.severity = Severity.valueOf(s.toUpperCase()); }
            catch (IllegalArgumentException e) { this.severity = Severity.INFO; }
            return this;
        }

        /** Sets an optional category badge label (e.g. "Auth", "Billing"). */
        public AuditEntry category(String c) { this.category = c; return this; }

        /** Serializes this entry to a JSON object string. */
        public String toJson() {
            StringBuilder sb = new StringBuilder("{");
            sb.append(jsonField("id",        id));
            sb.append(",").append(jsonField("timestamp", timestamp));
            sb.append(",").append(jsonField("actor",     actor));
            if (actorRole != null) sb.append(",").append(jsonField("actorRole", actorRole));
            sb.append(",").append(jsonField("action",    action));
            if (detail   != null) sb.append(",").append(jsonField("detail",    detail));
            sb.append(",").append(jsonField("severity",  severity.value()));
            if (category != null) sb.append(",").append(jsonField("category",  category));
            sb.append("}");
            return sb.toString();
        }

        private static String jsonField(String key, String value) {
            if (value == null) return "\"" + key + "\":null";
            // Escape backslashes first, then double-quotes.
            String escaped = value.replace("\\", "\\\\").replace("\"", "\\\"");
            return "\"" + key + "\":\"" + escaped + "\"";
        }
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /** Creates an empty timeline with default settings. */
    public TimelineStepper() {}

    // -----------------------------------------------------------------------
    // Configuration (attribute-based — synced to the web component)
    // -----------------------------------------------------------------------

    /**
     * Sets the page size sent in the {@link LoadMoreEvent}.
     *
     * @param size entries per page (default 20)
     */
    public void setPageSize(int size) {
        getElement().setAttribute("page-size", String.valueOf(size));
    }

    /**
     * Returns the configured page size.
     *
     * @return page size; {@code 20} if not explicitly set
     */
    public int getPageSize() {
        String val = getElement().getAttribute("page-size");
        return val != null ? Integer.parseInt(val) : 20;
    }

    /**
     * Tells the component whether more pages are available to load.
     *
     * <p>Sets both the {@code has-more} attribute (for Java-side reads) and calls
     * the JS {@code setHasMore} function so the client updates its pagination
     * state without resetting accumulated entries via {@code _parse()}.</p>
     *
     * @param hasMore {@code true} to show the infinite-scroll sentinel
     */
    public void setHasMore(boolean hasMore) {
        getElement().setAttribute("has-more", String.valueOf(hasMore));
        getElement().callJsFunction("setHasMore", hasMore);
    }

    /**
     * Returns whether more pages are currently flagged as available.
     *
     * @return {@code true} if more pages exist
     */
    public boolean isHasMore() {
        return "true".equals(getElement().getAttribute("has-more"));
    }

    /**
     * Shows or hides the loading skeleton rows.
     *
     * <p>Sets both the {@code loading} attribute (for Java-side reads) and calls
     * the JS {@code setLoading} function so the client updates its loading state
     * without resetting accumulated entries via {@code _parse()}.</p>
     *
     * @param loading {@code true} to show skeletons
     */
    public void setLoading(boolean loading) {
        getElement().setAttribute("loading", String.valueOf(loading));
        getElement().callJsFunction("setLoading", loading);
    }

    /**
     * Returns whether the loading skeleton is currently shown.
     *
     * @return {@code true} if loading
     */
    public boolean isLoading() {
        return "true".equals(getElement().getAttribute("loading"));
    }

    /**
     * Seeds the component with an initial JSON array of entries.
     * For subsequent pages prefer {@link #appendEntries(String)}.
     *
     * @param jsonArray JSON array string
     */
    public void setItems(String jsonArray) {
        getElement().setAttribute("items", jsonArray);
    }

    /**
     * Seeds the component with an initial typed list of entries.
     * Convenience overload that serializes to JSON and delegates to
     * {@link #setItems(String)}.
     *
     * @param entries initial page of entries (not null)
     */
    public void setItems(List<AuditEntry> entries) {
        setItems(toJsonArray(entries));
    }

    // -----------------------------------------------------------------------
    // Data mutation API (JS function calls — fire-and-forget after first render)
    // -----------------------------------------------------------------------

    /**
     * Pushes the next page of entries onto the bottom of the list.
     * Automatically clears the loading state in the component.
     *
     * @param jsonArray JSON array string (e.g. from Jackson/Gson)
     */
    public void appendEntries(String jsonArray) {
        getElement().callJsFunction("appendEntries", jsonArray);
    }

    /**
     * Pushes the next page from a typed list.
     *
     * @param entries list of {@link AuditEntry} instances
     */
    public void appendEntries(List<AuditEntry> entries) {
        appendEntries(toJsonArray(entries));
    }

    /**
     * Prepends new real-time events at the top of the list.
     *
     * @param jsonArray JSON array string
     */
    public void prependEntries(String jsonArray) {
        getElement().callJsFunction("prependEntries", jsonArray);
    }

    /**
     * Prepends new real-time events from a typed list.
     *
     * @param entries list of {@link AuditEntry} instances
     */
    public void prependEntries(List<AuditEntry> entries) {
        prependEntries(toJsonArray(entries));
    }

    /**
     * Patches a single entry by id (merges provided fields).
     *
     * @param jsonObject JSON object string with the fields to update (must contain {@code id})
     */
    public void updateEntry(String jsonObject) {
        getElement().callJsFunction("updateEntry", jsonObject);
    }

    /**
     * Removes an entry by id.
     *
     * @param id the entry id to remove
     */
    public void removeEntry(String id) {
        getElement().callJsFunction("removeEntry", id);
    }

    /** Resets the timeline to an empty state. */
    public void clearAll() {
        getElement().callJsFunction("clearAll");
    }

    // -----------------------------------------------------------------------
    // Events
    // -----------------------------------------------------------------------

    /**
     * Fired when the IntersectionObserver sentinel enters the viewport —
     * i.e. the user has scrolled near the bottom and the next page should load.
     */
    @DomEvent("tl-load-more")
    public static class LoadMoreEvent extends ComponentEvent<TimelineStepper> {
        private final int page;
        private final int pageSize;

        public LoadMoreEvent(
                TimelineStepper source,
                boolean fromClient,
                @EventData("event.detail.page")     int page,
                @EventData("event.detail.pageSize") int pageSize) {
            super(source, fromClient);
            this.page     = page;
            this.pageSize = pageSize;
        }

        /** Returns the 1-based page number being requested. */
        public int getPage()     { return page; }

        /** Returns the number of entries requested per page. */
        public int getPageSize() { return pageSize; }
    }

    /**
     * Registers a listener notified when more entries should be loaded.
     *
     * @param listener the listener (not null)
     * @return a {@link Registration} to remove the listener
     */
    public Registration addLoadMoreListener(
            ComponentEventListener<LoadMoreEvent> listener) {
        return addListener(LoadMoreEvent.class, listener);
    }

    /**
     * Fired when the user clicks or keyboard-activates an entry row.
     */
    @DomEvent("tl-entry-click")
    public static class EntryClickEvent extends ComponentEvent<TimelineStepper> {
        private final String entryId;

        public EntryClickEvent(
                TimelineStepper source,
                boolean fromClient,
                @EventData("event.detail.id") String entryId) {
            super(source, fromClient);
            this.entryId = entryId;
        }

        /** Returns the id of the clicked entry. */
        public String getEntryId() { return entryId; }
    }

    /**
     * Registers a listener notified when an entry row is activated.
     *
     * <p>Adding the <em>first</em> listener automatically sets the {@code clickable}
     * attribute on the host element so the web component shows a pointer cursor and
     * hover effects. Removing the <em>last</em> listener clears the attribute, hiding
     * all interactive affordances and preventing misleading UX.</p>
     *
     * @param listener the listener (not null)
     * @return a {@link Registration} to remove the listener
     */
    public Registration addEntryClickListener(
            ComponentEventListener<EntryClickEvent> listener) {
        Registration reg = addListener(EntryClickEvent.class, listener);
        if (clickListenerCount == 0) {
            getElement().setAttribute("clickable", "true");
        }
        clickListenerCount++;
        return () -> {
            reg.remove();
            clickListenerCount--;
            if (clickListenerCount == 0) {
                getElement().removeAttribute("clickable");
            }
        };
    }

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Returns a new fluent {@link TimelineStepperBuilder}.
     *
     * @return a new {@link TimelineStepperBuilder}
     */
    public static TimelineStepperBuilder builder() {
        return TimelineStepperBuilder.create();
    }

    /**
     * Returns a fluent configurator for an <strong>existing</strong>
     * {@link TimelineStepper} instance.
     *
     * @param timeline the timeline to configure (not null)
     * @return a {@link TimelineStepperConfigurator.BaseTimelineStepperConfigurator}
     */
    public static TimelineStepperConfigurator.BaseTimelineStepperConfigurator configure(TimelineStepper timeline) {
        return TimelineStepperConfigurator.configure(timeline);
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private static String toJsonArray(List<AuditEntry> entries) {
        return "[" + entries.stream()
                .map(AuditEntry::toJson)
                .collect(Collectors.joining(",")) + "]";
    }
}

