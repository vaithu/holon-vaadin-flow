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

import java.io.Serial;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.EmptyBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;

/**
 * Empty-state component inspired by shadcn/ui {@code EmptyState}.
 *
 * <p>Use this component to communicate that a collection, list, or data set contains
 * no items  and to give the user a clear path forward through an optional call-to-action.</p>
 *
 * <p>Composition:</p>
 * <pre>
 * Empty
 *  œ€€ Icon / Illustration  (optional  {@link #setIcon(Icon)})
 *  œ€€ EmptyTitle           ({@link #setTitle(EmptyTitle)} / {@link #setTitle(String)} / {@link #setTitle(Localizable)})
 *  œ€€ EmptyDescription     ({@link #setDescription(EmptyDescription)} / {@link #setDescription(String)})
 *  ”€€ EmptyAction          ({@link #setAction(EmptyAction)} / {@link #setAction(Component...)})
 * </pre>
 *
 * <p>Preferred usage via builder:</p>
 * <pre>{@code
 * Empty empty = Empty.builder()
 *     .icon(new Icon(VaadinIcon.INBOX))
 *     .title("No results found")
 *     .description("Try adjusting your search or filter to find what you're looking for.")
 *     .action(new Button("Clear filters"))
 *     .build();
 * }</pre>
 *
 * <p>All visual styling is handled by {@code empty.css}; no inline styles or Lumo tokens are used.</p>
 *
 * @see EmptyBuilder
 * @see EmptyTitle
 * @see EmptyDescription
 * @see EmptyAction
 */
@StyleSheet("context://empty.css")
public class Empty extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Internal slots
    // -----------------------------------------------------------------------

    private final Div iconSlot;
    private final Div contentSlot;

    private EmptyTitle currentTitle;
    private EmptyDescription currentDescription;
    private EmptyAction currentAction;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates an {@link Empty} component with no content.
     * Use the builder or setter methods to populate it.
     */
    public Empty() {
        addClassName("empty");
        // role="status" so screen readers politely announce when an empty state appears
        getElement().setAttribute("role", "status");

        this.iconSlot = Components.div().styleName("empty__icon").visible(false).build();
        this.contentSlot = Components.div().styleName("empty__content").build();

        add(iconSlot, contentSlot);
    }

    // -----------------------------------------------------------------------
    // Static factory
    // -----------------------------------------------------------------------

    /**
     * Obtain an {@link EmptyBuilder}.
     *
     * @return a new {@link EmptyBuilder}
     */
    public static EmptyBuilder builder() {
        return EmptyBuilder.create();
    }

    // -----------------------------------------------------------------------
    // Icon API
    // -----------------------------------------------------------------------

    /**
     * Sets a Vaadin icon in the icon slot above the title.
     * Passing {@code null} clears the icon.
     *
     * @param icon the icon to display, or {@code null} to clear
     */
    public void setIcon(Icon icon) {
        if (icon == null) {
            clearIcon();
            return;
        }
        iconSlot.removeAll();
        iconSlot.add(icon);
        iconSlot.setVisible(true);
    }

    /**
     * Sets an arbitrary component in the icon slot (e.g. an SVG illustration).
     * Passing {@code null} clears the slot.
     *
     * @param illustration any component to display as the illustration, or {@code null} to clear
     */
    public void setIcon(Component illustration) {
        if (illustration == null) {
            clearIcon();
            return;
        }
        iconSlot.removeAll();
        iconSlot.add(illustration);
        iconSlot.setVisible(true);
    }

    /**
     * Removes the icon / illustration from the icon slot.
     */
    public void clearIcon() {
        iconSlot.removeAll();
        iconSlot.setVisible(false);
    }

    // -----------------------------------------------------------------------
    // Title API
    // -----------------------------------------------------------------------

    /**
     * Returns the current {@link EmptyTitle}, or {@code null} if not set.
     *
     * @return the current title component
     */
    public EmptyTitle getEmptyTitle() {
        return currentTitle;
    }

    /**
     * Sets (or replaces) the {@link EmptyTitle}. Passing {@code null} removes it.
     *
     * @param title the title component, or {@code null} to remove
     */
    public void setTitle(EmptyTitle title) {
        if (this.currentTitle != null) {
            contentSlot.remove(this.currentTitle);
        }
        this.currentTitle = title;
        if (title != null) {
            contentSlot.addComponentAsFirst(title);
        }
    }

    /**
     * Sets the title from a plain string.
     *
     * @param text the title text (not null)
     */
    public void setTitle(String text) {
        setTitle(new EmptyTitle(text));
    }

    /**
     * Sets the title from a Holon {@link Localizable}.
     * The text is resolved on first attach; re-resolved on subsequent attaches.
     *
     * @param localizable the localizable message (not null)
     */
    public void setTitle(Localizable localizable) {
        setTitle(new EmptyTitle(localizable));
    }

    // -----------------------------------------------------------------------
    // Description API
    // -----------------------------------------------------------------------

    /**
     * Returns the current {@link EmptyDescription}, or {@code null} if not set.
     *
     * @return the current description component
     */
    public EmptyDescription getDescription() {
        return currentDescription;
    }

    /**
     * Sets (or replaces) the {@link EmptyDescription}. Passing {@code null} removes it.
     *
     * @param description the description component, or {@code null} to remove
     */
    public void setDescription(EmptyDescription description) {
        if (this.currentDescription != null) {
            contentSlot.remove(this.currentDescription);
        }
        this.currentDescription = description;
        if (description != null) {
            if (this.currentTitle != null) {
                contentSlot.addComponentAtIndex(1, description);
            } else {
                contentSlot.addComponentAsFirst(description);
            }
        }
    }

    /**
     * Sets the description from a plain string.
     *
     * @param text the description text (not null)
     */
    public void setDescription(String text) {
        setDescription(new EmptyDescription(text));
    }

    /**
     * Sets the description from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     */
    public void setDescription(Localizable localizable) {
        setDescription(new EmptyDescription(localizable));
    }

    // -----------------------------------------------------------------------
    // Action API
    // -----------------------------------------------------------------------

    /**
     * Returns the current {@link EmptyAction}, or {@code null} if not set.
     *
     * @return the current action container
     */
    public EmptyAction getAction() {
        return currentAction;
    }

    /**
     * Sets (or replaces) the {@link EmptyAction}. Passing {@code null} removes it.
     *
     * @param action the action container, or {@code null} to remove
     */
    public void setAction(EmptyAction action) {
        if (this.currentAction != null) {
            contentSlot.remove(this.currentAction);
        }
        this.currentAction = action;
        if (action != null) {
            contentSlot.add(action);
        }
    }

    /**
     * Wraps the given components in an {@link EmptyAction} and sets it.
     *
     * @param actions action components (buttons, links, etc.)
     */
    public void setAction(Component... actions) {
        setAction(new EmptyAction(actions));
    }
}
