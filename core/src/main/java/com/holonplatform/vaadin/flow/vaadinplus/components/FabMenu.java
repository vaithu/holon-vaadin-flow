/*
 * Copyright 2016-2026 Axioma srl.
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

import com.holonplatform.vaadin.flow.components.builders.FabMenuBuilder;
import com.holonplatform.vaadin.flow.components.builders.FabMenuConfigurator;
import com.holonplatform.vaadin.flow.internal.components.support.ComponentClickListenerAdapter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Material Design 3 <a href="https://m3.material.io/components/floating-action-button/overview">
 * FAB Menu</a> — a "speed-dial" of related actions revealed from a single main {@link Fab} trigger.
 *
 * <p>Tapping the trigger reveals a stack of smaller ({@link Fab.Size#SMALL}) action FABs — either
 * stacked vertically (the default) or laid out horizontally, see {@link Orientation} — together
 * with a full-screen scrim that closes the menu when tapped. Selecting any action automatically
 * closes the menu. The trigger icon swaps to a <em>close</em> icon while the menu is open.</p>
 *
 * <p>Item labels can be rendered in two ways, see {@link LabelPlacement}: baked into the item's own
 * extended pill shape ({@link LabelPlacement#INLINE}, the default), or as a separate label chip
 * that fades in independently of the icon button ({@link LabelPlacement#SIDE}).</p>
 *
 * <p>DOM structure (with the default {@link LabelPlacement#INLINE}):</p>
 * <pre>
 * &lt;div class="fab-menu [fab-menu--pos-{position}] [fab-menu--horizontal] [fab-menu--open]"&gt;
 *   &lt;div class="fab-menu__scrim"&gt;&lt;/div&gt;
 *   &lt;div class="fab-menu__items"&gt;
 *     &lt;vaadin-button class="fab fab-menu__item ..."&gt;...&lt;/vaadin-button&gt;
 *     ...
 *   &lt;/div&gt;
 *   &lt;vaadin-button class="fab fab-menu__trigger ..."&gt;...&lt;/vaadin-button&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>All visual styling lives in {@code fab-menu.css}. No inline styles are used.</p>
 *
 * <p>Preferred usage — via the {@link com.holonplatform.vaadin.flow.components.Components}
 * factory or the builder:</p>
 * <pre>{@code
 * FabMenu menu = Components.fabMenu(VaadinIcon.PLUS, Fab.Color.PRIMARY)
 *     .position(Fab.Position.BOTTOM_END)
 *     .orientation(FabMenu.Orientation.VERTICAL)
 *     .labelPlacement(FabMenu.LabelPlacement.SIDE)
 *     .item(VaadinIcon.EDIT, "Compose", e -> compose())
 *     .item(VaadinIcon.CAMERA, "Photo", e -> takePhoto())
 *     .item(VaadinIcon.UPLOAD, "Attach", e -> attach())
 *     .build();
 * }</pre>
 *
 * @see FabMenuItem
 * @see FabMenuBuilder
 * @see FabMenuConfigurator
 */
@StyleSheet("context://fab-menu.css")
public class FabMenu extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Layout direction for the revealed item stack.
     */
    public enum Orientation {
        /** Items stack vertically above/below the trigger (the M3 default). */
        VERTICAL(null),
        /** Items are laid out horizontally, extending to the side of the trigger. */
        HORIZONTAL("fab-menu--horizontal");

        private final String cssClass;

        Orientation(String cssClass) {
            this.cssClass = cssClass;
        }

        /** Returns the CSS modifier class, or {@code null} for {@link #VERTICAL}. */
        public String getCssClass() {
            return cssClass;
        }
    }

    /**
     * How an item's label (if any) is rendered.
     */
    public enum LabelPlacement {
        /** Labels are never shown, even if a {@link FabMenuItem} specifies one. */
        NONE,
        /** The label is baked into the item's own extended pill shape (the default). */
        INLINE,
        /**
         * The label is rendered as a separate chip beside the icon button, fading in
         * independently (with its own transition timing) rather than growing/shrinking
         * together with the icon button's pill shape.
         */
        SIDE
    }

    private final Div scrim;
    private final Div itemsContainer;
    private final Fab trigger;
    private final List<Fab> itemButtons = new ArrayList<>();

    private VaadinIcon closedIcon;
    private VaadinIcon openIcon = VaadinIcon.CLOSE;
    private boolean open = false;
    private boolean backdrop = true;
    private Fab.Position currentPosition = Fab.Position.NONE;
    private Orientation orientation = Orientation.VERTICAL;
    private LabelPlacement labelPlacement = LabelPlacement.INLINE;


    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a FAB menu with a {@link VaadinIcon#PLUS} primary trigger.
     */
    public FabMenu() {
        this(VaadinIcon.PLUS, Fab.Color.PRIMARY);
    }

    /**
     * Creates a FAB menu with the given trigger icon and default (primary) color.
     *
     * @param icon the trigger's closed-state icon (not null)
     */
    public FabMenu(VaadinIcon icon) {
        this(icon, Fab.Color.PRIMARY);
    }

    /**
     * Creates a fully-specified FAB menu.
     *
     * @param icon  the trigger's closed-state icon (not null)
     * @param color the trigger (and default item) color scheme
     */
    public FabMenu(VaadinIcon icon, Fab.Color color) {
        addClassName("fab-menu");
        this.closedIcon = icon;

        this.scrim = new Div();
        scrim.addClassName("fab-menu__scrim");
        scrim.addClickListener(e -> close());

        this.itemsContainer = new Div();
        itemsContainer.addClassName("fab-menu__items");

        this.trigger = new Fab(icon, color);
        trigger.addClassName("fab-menu__trigger");
        trigger.addClickListener(e -> toggle());

        add(scrim, itemsContainer, trigger);
    }

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Obtain a {@link FabMenuBuilder} with a {@link VaadinIcon#PLUS} primary trigger.
     *
     * @return a new {@link FabMenuBuilder}
     */
    public static FabMenuBuilder builder() {
        return FabMenuBuilder.create();
    }

    /**
     * Obtain a {@link FabMenuBuilder} pre-configured with the given trigger icon.
     *
     * @param icon the trigger's closed-state icon (not null)
     * @return a new {@link FabMenuBuilder}
     */
    public static FabMenuBuilder builder(VaadinIcon icon) {
        return FabMenuBuilder.create(icon);
    }

    /**
     * Obtain a {@link FabMenuBuilder} pre-configured with trigger icon and color.
     *
     * @param icon  the trigger's closed-state icon (not null)
     * @param color the trigger (and default item) color scheme
     * @return a new {@link FabMenuBuilder}
     */
    public static FabMenuBuilder builder(VaadinIcon icon, Fab.Color color) {
        return FabMenuBuilder.create(icon, color);
    }

    /**
     * Obtain a {@link FabMenuConfigurator.BaseFabMenuConfigurator} to configure an existing
     * {@link FabMenu}.
     *
     * @param menu the menu to configure (not null)
     * @return a {@link FabMenuConfigurator.BaseFabMenuConfigurator}
     */
    public static FabMenuConfigurator.BaseFabMenuConfigurator configure(FabMenu menu) {
        return FabMenuConfigurator.configure(menu);
    }

    // -----------------------------------------------------------------------
    // Trigger API
    // -----------------------------------------------------------------------

    /**
     * Returns the main trigger {@link Fab}.
     *
     * @return the trigger
     */
    public Fab getTrigger() {
        return trigger;
    }

    /**
     * Sets the trigger size (item FABs are always {@link Fab.Size#SMALL} per the M3 spec).
     *
     * @param size the trigger size (not null)
     */
    public void setTriggerSize(Fab.Size size) {
        trigger.setFabSize(size);
    }

    /**
     * Sets the trigger color scheme.
     *
     * @param color the color scheme ({@code null} = {@link Fab.Color#SURFACE})
     */
    public void setTriggerColor(Fab.Color color) {
        trigger.setFabColor(color);
    }

    /**
     * Sets the trigger icons: shown when closed, and shown while the menu is open (typically a
     * "close"/"X" icon). Defaults to the constructor icon and {@link VaadinIcon#CLOSE}.
     *
     * @param closedIcon icon shown when the menu is closed (not null)
     * @param openIcon   icon shown when the menu is open (not null)
     */
    public void setTriggerIcons(VaadinIcon closedIcon, VaadinIcon openIcon) {
        this.closedIcon = closedIcon;
        this.openIcon = openIcon;
        trigger.setIcon(open ? openIcon.create() : closedIcon.create());
    }

    // -----------------------------------------------------------------------
    // Orientation / label placement
    // -----------------------------------------------------------------------

    /**
     * Returns the current item stack orientation.
     *
     * @return the current {@link Orientation} (never null)
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Sets the item stack orientation (vertical stack vs. horizontal row).
     *
     * @param orientation the new orientation (not null; {@link Orientation#VERTICAL} is the default)
     */
    public void setOrientation(Orientation orientation) {
        if (this.orientation != null && this.orientation.getCssClass() != null) {
            removeClassName(this.orientation.getCssClass());
        }
        this.orientation = (orientation != null) ? orientation : Orientation.VERTICAL;
        if (this.orientation.getCssClass() != null) {
            addClassName(this.orientation.getCssClass());
        }
    }

    /**
     * Returns the current label placement mode.
     *
     * @return the current {@link LabelPlacement} (never null)
     */
    public LabelPlacement getLabelPlacement() {
        return labelPlacement;
    }

    /**
     * Sets how item labels are rendered. Only affects items added <em>after</em> this call —
     * already-added items keep the placement mode that was active when they were added.
     *
     * @param labelPlacement the new label placement mode (not null; {@link LabelPlacement#INLINE}
     *                       is the default)
     */
    public void setLabelPlacement(LabelPlacement labelPlacement) {
        this.labelPlacement = (labelPlacement != null) ? labelPlacement : LabelPlacement.INLINE;
    }

    // -----------------------------------------------------------------------
    // Items API
    // -----------------------------------------------------------------------

    /**
     * Adds an action item to the menu, rendered as a small {@link Fab}. How the item's label (if
     * any) is displayed depends on the current {@link #getLabelPlacement()}. Selecting the item
     * automatically closes the menu.
     *
     * @param item the item descriptor (not null)
     * @return the created item {@link Fab}, for further fine-tuning if needed
     */
    public Fab addItem(FabMenuItem item) {
        final Fab button = new Fab(item.getIcon(), item.getColor() != null ? item.getColor() : Fab.Color.SURFACE);
        button.setFabSize(Fab.Size.SMALL);
        button.addClassName("fab-menu__item");
        button.setEnabled(item.isEnabled());
        if (item.getClickListener() != null) {
            button.addClickListener(new ComponentClickListenerAdapter<>(item.getClickListener()));
        }
        // Selecting any action closes the menu, per the M3 FAB Menu interaction pattern
        button.addClickListener(e -> close());

        final boolean hasLabel = item.getLabel() != null && !item.getLabel().isBlank();
        final Component itemComponent;
        if (hasLabel && labelPlacement == LabelPlacement.INLINE) {
            button.extended(item.getLabel());
            itemComponent = button;
        } else if (hasLabel && labelPlacement == LabelPlacement.SIDE) {
            final Span label = new Span(item.getLabel());
            label.addClassName("fab-menu__item-label");
            final Div row = new Div();
            row.addClassName("fab-menu__item-row");
            row.add(label, button);
            itemComponent = row;
        } else {
            itemComponent = button;
        }

        itemsContainer.add(itemComponent);
        itemButtons.add(button);
        return button;
    }

    /**
     * Returns an unmodifiable view of the currently configured item {@link Fab}s, in the order
     * they were added.
     *
     * @return the item buttons
     */
    public List<Fab> getItemButtons() {
        return Collections.unmodifiableList(itemButtons);
    }

    /**
     * Removes all configured items.
     */
    public void clearItems() {
        itemsContainer.removeAll();
        itemButtons.clear();
    }

    // -----------------------------------------------------------------------
    // Open / close state
    // -----------------------------------------------------------------------

    /**
     * Returns whether the menu is currently open.
     *
     * @return {@code true} if open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Opens the menu, revealing the item stack and the scrim (if enabled), and swapping the
     * trigger icon to the "open" icon.
     */
    public void open() {
        if (open) {
            return;
        }
        this.open = true;
        addClassName("fab-menu--open");
        trigger.setIcon(openIcon.create());
        trigger.getElement().setAttribute("aria-expanded", "true");
    }

    /**
     * Closes the menu, hiding the item stack and the scrim, and restoring the trigger's
     * closed-state icon.
     */
    public void close() {
        if (!open) {
            return;
        }
        this.open = false;
        removeClassName("fab-menu--open");
        trigger.setIcon(closedIcon.create());
        trigger.getElement().setAttribute("aria-expanded", "false");
    }

    /**
     * Toggles the menu open/closed state.
     */
    public void toggle() {
        if (open) {
            close();
        } else {
            open();
        }
    }

    // -----------------------------------------------------------------------
    // Backdrop / scrim
    // -----------------------------------------------------------------------

    /**
     * Returns whether the full-screen scrim (backdrop) is enabled.
     *
     * @return {@code true} if enabled (the default)
     */
    public boolean isBackdrop() {
        return backdrop;
    }

    /**
     * Enables or disables the full-screen scrim shown behind the item stack while the menu is
     * open. When enabled, tapping the scrim closes the menu.
     *
     * @param backdrop {@code false} to disable the scrim entirely
     */
    public void setBackdrop(boolean backdrop) {
        this.backdrop = backdrop;
        scrim.setVisible(backdrop);
    }

    // -----------------------------------------------------------------------
    // Position API
    // -----------------------------------------------------------------------

    /**
     * Returns the current fixed docking position.
     *
     * @return the current {@link Fab.Position} (never null)
     */
    public Fab.Position getFabMenuPosition() {
        return currentPosition;
    }

    /**
     * Docks the whole menu (trigger + item stack + scrim) at a fixed position on screen.
     *
     * @param position the new position (not null; {@link Fab.Position#NONE} removes fixed positioning)
     */
    public void setFabMenuPosition(Fab.Position position) {
        if (currentPosition != null && currentPosition != Fab.Position.NONE) {
            removeClassName(positionClassName(currentPosition));
        }
        currentPosition = (position != null) ? position : Fab.Position.NONE;
        if (currentPosition != Fab.Position.NONE) {
            addClassName(positionClassName(currentPosition));
        }
    }

    private static String positionClassName(Fab.Position position) {
        return switch (position) {
            case BOTTOM_END -> "fab-menu--pos-bottom-end";
            case BOTTOM_START -> "fab-menu--pos-bottom-start";
            case BOTTOM_CENTER -> "fab-menu--pos-bottom-center";
            case TOP_END -> "fab-menu--pos-top-end";
            case TOP_START -> "fab-menu--pos-top-start";
            case NONE -> "";
        };
    }
}







