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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTimelineStepperBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper;

/**
 * Fluent builder to create and configure {@link TimelineStepper} components.
 *
 * <h3>Pattern A — pre-load first page at build time (recommended)</h3>
 * <pre>{@code
 * List<AuditEntry> firstPage = auditService.getFirstPage(20);
 *
 * TimelineStepper tl = TimelineStepper.builder()
 *     .pageSize(20)
 *     .items(firstPage)                          // seed initial data immediately
 *     .hasMore(auditService.hasMore(0))
 *     .width("100%")
 *     .onLoadMore(e -> {
 *         tl.appendEntries(auditService.getPage(e.getPage(), e.getPageSize()));
 *         tl.setHasMore(auditService.hasMore(e.getPage()));
 *     })
 *     .onEntryClick(e -> Notification.show("Entry: " + e.getEntryId()))
 *     .build();
 * }</pre>
 *
 * <h3>Pattern B — lazy first load via sentinel (no pre-loaded data)</h3>
 * <pre>{@code
 * // Start with loading skeletons; tl-load-more fires immediately when
 * // the component is visible because the sentinel is above the fold.
 * TimelineStepper tl = TimelineStepper.builder()
 *     .pageSize(20)
 *     .loading(true)
 *     .hasMore(true)
 *     .width("100%")
 *     .onLoadMore(e -> {
 *         List<AuditEntry> page = auditService.getPage(e.getPage(), e.getPageSize());
 *         tl.appendEntries(page);
 *         tl.setHasMore(auditService.hasMore(e.getPage()));
 *     })
 *     .onEntryClick(e -> Notification.show("Entry: " + e.getEntryId()))
 *     .build();
 * }</pre>
 */
public interface TimelineStepperBuilder
        extends TimelineStepperConfigurator<TimelineStepperBuilder>,
        ComponentBuilder<TimelineStepper, TimelineStepperBuilder> {

    /**
     * Creates a new {@link TimelineStepperBuilder}.
     *
     * @return a new {@link TimelineStepperBuilder}
     */
    static TimelineStepperBuilder create() {
        return new DefaultTimelineStepperBuilder();
    }
}

