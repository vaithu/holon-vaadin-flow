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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TimelineStepperConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.List;
import java.util.Optional;

/**
 * Base {@link TimelineStepperConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractTimelineStepperConfigurator<C extends TimelineStepperConfigurator<C>>
        extends AbstractComponentConfigurator<TimelineStepper, C>
        implements TimelineStepperConfigurator<C> {

    public AbstractTimelineStepperConfigurator(TimelineStepper component) {
        super(component);
    }

    @Override
    public C pageSize(int size) {
        getComponent().setPageSize(size);
        return getConfigurator();
    }

    @Override
    public C hasMore(boolean hasMore) {
        // Write the attribute only — the callJsFunction path in setHasMore() is
        // only needed for post-render calls that must not reset _entries.
        // At build time the component is not yet attached, so the attribute is
        // sufficient and queuing a redundant JS call is unnecessary.
        getComponent().getElement().setAttribute("has-more", String.valueOf(hasMore));
        return getConfigurator();
    }

    @Override
    public C loading(boolean loading) {
        // Same rationale as hasMore(): attribute-only at build time.
        getComponent().getElement().setAttribute("loading", String.valueOf(loading));
        return getConfigurator();
    }

    @Override
    public C items(List<TimelineStepper.AuditEntry> entries) {
        getComponent().setItems(toJsonArray(entries));
        return getConfigurator();
    }

    @Override
    public C items(String jsonArray) {
        getComponent().setItems(jsonArray);
        return getConfigurator();
    }

    @Override
    public C onLoadMore(ComponentEventListener<TimelineStepper.LoadMoreEvent> listener) {
        getComponent().addLoadMoreListener(listener);
        return getConfigurator();
    }

    @Override
    public C onEntryClick(ComponentEventListener<TimelineStepper.EntryClickEvent> listener) {
        getComponent().addEntryClickListener(listener);
        return getConfigurator();
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static String toJsonArray(List<TimelineStepper.AuditEntry> entries) {
        if (entries == null || entries.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(entries.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
    }
}
