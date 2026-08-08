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
import com.holonplatform.vaadin.flow.components.builders.CarouselBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

import java.text.MessageFormat;

/**
 * Accessible carousel (slideshow) component inspired by shadcn/ui Carousel.
 *
 * <p>Uses CSS {@code scroll-snap} for smooth, hardware-accelerated sliding
 * with no external JS library dependency. Navigation is driven by server-side
 * state; the physical scroll is applied via a minimal
 * {@code Element.executeJs()} call so the browser handles all animation.</p>
 *
 * <p>Component hierarchy:</p>
 * <pre>
 * Carousel                     â† root &lt;div&gt; with role="region"
 *   CarouselContent            â† scroll viewport &lt;div&gt;
 *     CarouselItem             â† individual slide &lt;div&gt;
 *       â€¦ user content â€¦
 *   CarouselPrevious           â† ghost prev button
 *   CarouselNext               â† ghost next button
 * </pre>
 *
 * <p>Minimal usage:</p>
 * <pre>{@code
 * Carousel carousel = new Carousel();
 * carousel.addItem(card1, card2, card3);
 * }</pre>
 *
 * <p>Fluent builder usage:</p>
 * <pre>{@code
 * Carousel carousel = Carousel.builder()
 *     .orientation(Carousel.Orientation.HORIZONTAL)
 *     .loop(true)
 *     .items(item1, item2, item3)
 *     .build();
 * }</pre>
 *
 * <p>All styling is defined in {@code carousel.css}. No inline styles or Lumo tokens.</p>
 *
 * @see CarouselContent
 * @see CarouselItem
 * @see CarouselPrevious
 * @see CarouselNext
 */
@StyleSheet("context://carousel.css")
public class Carousel extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Orientation
    // -----------------------------------------------------------------------

    /**
     * Scrolling axis of the carousel.
     */
    public enum Orientation {

        /** Left-to-right sliding (default). */
        HORIZONTAL("carousel--horizontal"),
        /** Top-to-bottom sliding. */
        VERTICAL("carousel--vertical");

        private final String cssClass;

        Orientation(String cssClass) {
            this.cssClass = cssClass;
        }

        /**
         * Returns the BEM modifier class for this orientation.
         *
         * @return CSS class name (never null)
         */
        public String getCssClass() {
            return cssClass;
        }
    }

    // -----------------------------------------------------------------------
    // Slide-change event
    // -----------------------------------------------------------------------

    /**
     * Fired whenever the active slide changes via {@link #previous()},
     * {@link #next()}, or {@link #scrollTo(int)}.
     */
    public static class SlideChangeEvent extends ComponentEvent<Carousel> {

        @Serial
        private static final long serialVersionUID = 1L;

        private final int previousIndex;
        private final int currentIndex;

        public SlideChangeEvent(Carousel source, int previousIndex, int currentIndex) {
            super(source, false);
            this.previousIndex = previousIndex;
            this.currentIndex = currentIndex;
        }

        /** Returns the index of the slide that was active before the change. */
        public int getPreviousIndex() {
            return previousIndex;
        }

        /** Returns the index of the newly active slide. */
        public int getCurrentIndex() {
            return currentIndex;
        }
    }

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private final CarouselContent content;
    private final CarouselPrevious previous;
    private final CarouselNext next;

    private Orientation orientation;
    private boolean loop = false;
    private int currentIndex = 0;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a horizontal, non-looping carousel with no items.
     */
    public Carousel() {
        this(Orientation.HORIZONTAL);
    }

    /**
     * Creates a carousel with the given orientation and no items.
     *
     * @param orientation the scroll axis (not null)
     */
    public Carousel(Orientation orientation) {
        addClassName("carousel");
        getElement().setAttribute("role", "region");
        getElement().setAttribute("aria-roledescription", "carousel");

        this.content = new CarouselContent();
        this.previous = new CarouselPrevious();
        this.next = new CarouselNext();

        previous.addClickListener(e -> previous());
        next.addClickListener(e -> next());

        add(content, previous, next);
        setOrientation(orientation);
        updateNavigationState();
    }

    // -----------------------------------------------------------------------
    // Static factory / builder
    // -----------------------------------------------------------------------

    /**
     * Returns a new Holon Platform fluent {@link CarouselBuilder} for a horizontal carousel.
     *
     * <p>Usage:
     * <pre>{@code
     * Carousel carousel = Carousel.builder()
     *     .loop(true)
     *     .addItem(card1, card2, card3)
     *     .build();
     * }</pre>
     *
     * @return a new {@link CarouselBuilder} (never null)
     */
    public static CarouselBuilder builder() {
        return CarouselBuilder.create();
    }

    /**
     * Returns a new Holon Platform fluent {@link CarouselBuilder} with the given orientation.
     *
     * @param orientation the scroll axis (not null)
     * @return a new {@link CarouselBuilder} (never null)
     */
    public static CarouselBuilder builder(Orientation orientation) {
        return CarouselBuilder.create(orientation);
    }

    // -----------------------------------------------------------------------
    // Orientation API
    // -----------------------------------------------------------------------

    /**
     * Returns the current scroll orientation.
     *
     * @return orientation (never null)
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Changes the scroll orientation. Removes the previous orientation CSS class
     * and adds the new one.
     *
     * @param orientation the new orientation (not null)
     */
    public void setOrientation(Orientation orientation) {
        if (this.orientation != null) {
            removeClassName(this.orientation.getCssClass());
        }
        this.orientation = orientation;
        addClassName(orientation.getCssClass());
        content.setOrientation(orientation);
    }

    // -----------------------------------------------------------------------
    // Loop API
    // -----------------------------------------------------------------------

    /**
     * Returns {@code true} if the carousel loops (wraps around at both ends).
     *
     * @return {@code true} when looping
     */
    public boolean isLoop() {
        return loop;
    }

    /**
     * Enables or disables wrap-around looping.
     * When disabled the Previous/Next buttons are hidden at the first and last slide.
     *
     * @param loop {@code true} to enable looping
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
        updateNavigationState();
    }

    // -----------------------------------------------------------------------
    // Item API
    // -----------------------------------------------------------------------

    /**
     * Wraps each component in a new {@link CarouselItem} and appends it.
     *
     * @param components one or more slide contents (not null)
     */
    public void addItem(Component... components) {
        for (Component c : components) {
            int slideNumber = content.getItemCount() + 1;
            CarouselItem item = new CarouselItem(c);
            item.getElement().setAttribute("aria-label",
                    MessageFormat.format(LocalizationProvider.localize("Slide {0}", "carousel.slide_label"), slideNumber));
            content.add(item);
        }
        updateNavigationState();
    }

    /**
     * Returns the inner {@link CarouselContent} scroll viewport.
     * Use {@link CarouselContent#add(CarouselItem...)} for advanced composition.
     *
     * @return the content container (never null)
     */
    public CarouselContent getContent() {
        return content;
    }

    /**
     * Returns the total number of slides currently registered.
     *
     * @return slide count â‰¥ 0
     */
    public int getItemCount() {
        return content.getItemCount();
    }

    // -----------------------------------------------------------------------
    // Navigation API
    // -----------------------------------------------------------------------

    /**
     * Returns the zero-based index of the currently visible slide.
     *
     * @return current slide index
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Navigates to the previous slide.
     * If looping is enabled and the first slide is active, wraps to the last.
     * If looping is disabled and the first slide is active, this is a no-op.
     */
    public void previous() {
        int count = content.getItemCount();
        if (count == 0) return;
        if (currentIndex == 0 && !loop) return;
        int prev = currentIndex;
        currentIndex = (currentIndex - 1 + count) % count;
        applyScroll();
        updateNavigationState();
        fireEvent(new SlideChangeEvent(this, prev, currentIndex));
    }

    /**
     * Navigates to the next slide.
     * If looping is enabled and the last slide is active, wraps to the first.
     * If looping is disabled and the last slide is active, this is a no-op.
     */
    public void next() {
        int count = content.getItemCount();
        if (count == 0) return;
        if (currentIndex == count - 1 && !loop) return;
        int prev = currentIndex;
        currentIndex = (currentIndex + 1) % count;
        applyScroll();
        updateNavigationState();
        fireEvent(new SlideChangeEvent(this, prev, currentIndex));
    }

    /**
     * Navigates directly to the given zero-based slide index.
     *
     * @param index target slide index (0 â‰¤ index &lt; {@link #getItemCount()})
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     */
    public void scrollTo(int index) {
        int count = content.getItemCount();
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException(
                    "Slide index " + index + " out of bounds for size " + count);
        }
        int prev = currentIndex;
        currentIndex = index;
        applyScroll();
        updateNavigationState();
        if (prev != currentIndex) {
            fireEvent(new SlideChangeEvent(this, prev, currentIndex));
        }
    }

    // -----------------------------------------------------------------------
    // Event API
    // -----------------------------------------------------------------------

    /**
     * Registers a listener that is notified whenever the active slide changes.
     *
     * @param listener the listener (not null)
     * @return registration handle to remove the listener
     */
    public Registration addSlideChangeListener(ComponentEventListener<SlideChangeEvent> listener) {
        return addListener(SlideChangeEvent.class, listener);
    }

    // -----------------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------------

    /**
     * Instructs the browser to scroll the active slide into view.
     * Uses {@code scrollIntoView} with {@code behavior:'smooth'} so the
     * CSS scroll-snap container handles all easing.
     */
    private void applyScroll() {
        content.getElement().executeJs(
                "var items = this.children; if (items[$0]) { items[$0].scrollIntoView({behavior:'smooth', block:'nearest', inline:'nearest'}); }",
                currentIndex);
    }

    /**
     * Updates disabled state and aria attributes on Previous/Next buttons
     * to reflect the current index and loop setting.
     */
    private void updateNavigationState() {
        int count = content.getItemCount();
        boolean atFirst = currentIndex == 0;
        boolean atLast  = currentIndex >= count - 1;

        previous.setDisabled(!loop && atFirst);
        next.setDisabled(!loop && (count == 0 || atLast));

        getElement().setAttribute("aria-label",
                MessageFormat.format(LocalizationProvider.localize("Slide {0} of {1}", "carousel.slide_of_total"), currentIndex + 1, count));
    }

}




