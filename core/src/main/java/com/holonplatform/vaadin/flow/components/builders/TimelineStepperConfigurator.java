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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTimelineStepperConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper.AuditEntry;
import com.vaadin.flow.component.ComponentEventListener;

import java.util.List;
import java.util.stream.Stream;

/**
 * Fluent configurator for {@link TimelineStepper} components.
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 */
public interface TimelineStepperConfigurator<C extends TimelineStepperConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>,
        HasEnabledConfigurator<C> {

    /**
     * Sets the number of entries requested per page in the {@code tl-load-more} event.
     *
     * @param size page size (default 20)
     * @return this configurator for chaining
     */
    C pageSize(int size);

    /**
     * Sets whether more pages are available (shows the scroll sentinel).
     *
     * @param hasMore {@code true} if more entries can be loaded
     * @return this configurator for chaining
     */
    C hasMore(boolean hasMore);

    /**
     * Sets the initial loading state (shows skeleton rows).
     *
     * @param loading {@code true} to show loading skeletons
     * @return this configurator for chaining
     */
    C loading(boolean loading);

    /**
     * Seeds the timeline with an initial set of entries at creation time.
     *
     * <p>Use this to pre-load the first page of data directly in the builder so
     * the component renders immediately with content — no extra round-trip needed.
     *
     * <pre>{@code
     * TimelineStepper tl = TimelineStepper.builder()
     *     .items(auditService.getFirstPage())
     *     .hasMore(auditService.hasMore(0))
     *     .onLoadMore(e -> {
     *         tl.appendEntries(auditService.getPage(e.getPage(), e.getPageSize()));
     *         tl.setHasMore(auditService.hasMore(e.getPage()));
     *     })
     *     .build();
     * }</pre>
     *
     * @param entries ordered list of entries for the first page (not null)
     * @return this configurator for chaining
     */
    C items(List<AuditEntry> entries);

    /**
     * Seeds the timeline with an initial JSON array of entries at creation time.
     *
     * @param jsonArray JSON array string (not null)
     * @return this configurator for chaining
     * @see #items(List)
     */
    C items(String jsonArray);

    /**
     * Seeds the timeline with an initial stream of entries at creation time.
     *
     * <p>The stream is consumed <em>once</em> and closed automatically.  No intermediate
     * {@link List} is allocated — the JSON is built entry-by-entry directly from the
     * stream, making this the lowest-overhead overload for large first-page datasets.
     *
     * <pre>{@code
     * TimelineStepper tl = TimelineStepper.builder()
     *     .items(auditService.streamFirstPage())   // Stream<AuditEntry>
     *     .hasMore(true)
     *     .build();
     * }</pre>
     *
     * @param entries ordered stream of entries (not null, consumed and closed by this call)
     * @return this configurator for chaining
     * @see #items(List)
     * @see #items(String)
     */
    default C items(Stream<AuditEntry> entries) {
        StringBuilder sb = new StringBuilder("[");
        boolean[] first = {true};
        try (Stream<AuditEntry> s = entries) {
            s.forEach(e -> {
                if (!first[0]) sb.append(',');
                sb.append(e.toJson());
                first[0] = false;
            });
        }
        sb.append("]");
        return items(sb.toString());
    }

    /**
     * Adds a listener notified when the user scrolls to the bottom and more entries
     * should be loaded.
     *
     * @param listener the listener (not null)
     * @return this configurator for chaining
     */
    C onLoadMore(ComponentEventListener<TimelineStepper.LoadMoreEvent> listener);

    /**
     * Adds a listener notified when the user clicks or keyboard-activates an entry row.
     *
     * @param listener the listener (not null)
     * @return this configurator for chaining
     */
    C onEntryClick(ComponentEventListener<TimelineStepper.EntryClickEvent> listener);

    /**
     * Returns a configurator for an <strong>existing</strong> {@link TimelineStepper} instance.
     *
     * @param timeline the timeline to configure (not null)
     * @return a {@link BaseTimelineStepperConfigurator}
     */
    static BaseTimelineStepperConfigurator configure(TimelineStepper timeline) {
        return new DefaultTimelineStepperConfigurator(timeline);
    }

    /** Base configurator type returned by {@link #configure(TimelineStepper)}. */
    interface BaseTimelineStepperConfigurator
            extends TimelineStepperConfigurator<BaseTimelineStepperConfigurator> {
    }
}

