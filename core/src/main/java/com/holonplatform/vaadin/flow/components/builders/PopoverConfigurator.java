package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;

/***
 * Popover is a component for creating overlays that are positioned next to specified component (target).
 * @param <C>
 */

public interface PopoverConfigurator<C extends PopoverConfigurator<C>> extends ComponentConfigurator<C>, HasComponentsConfigurator<C>
        , HasThemeVariantConfigurator<PopoverVariant, C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasAriaLabelConfigurator<C> {

    /**
     * Set true to make the popover content automatically receive focus after it is opened. Modal popovers use this behavior by default.
     * @param autofocus
     * @return
     */
    C autofocus(boolean autofocus);

    /**
     * Sets whether component should show a backdrop (modality curtain) when opened.
     * @param backdropVisible
     * @return
     */
    C backdropVisible(boolean backdropVisible);

    /**
     * Sets the CSS class names of the popover overlay element.
     * @param className
     * @return
     */
    C className(String className);

    /**
     * Sets whether this popover can be closed by pressing the Esc key or not.
     * @param closeOnEsc
     * @return
     */
    C closeOnEsc(boolean closeOnEsc);

    /**
     * Sets whether this popover can be closed by clicking outside of it or not.
     * @param closeOnOutsideClick
     * @return
     */
    C closeOnOutsideClick(boolean closeOnOutsideClick);

    /**
     * Sets the default focus delay to be used by all popover instances (running in the same JVM), except for those that have focus delay configured using setFocusDelay(int).
     * @param defaultFocusDelay
     * @return
     */
    C defaultFocusDelay(int defaultFocusDelay);

    /**
     * Sets the default hide delay to be used by all popover instances (running in the same JVM), except for those that have hide delay configured using setHideDelay(int).
     * @param defaultHideDelay
     * @return
     */
    C defaultHideDelay(int defaultHideDelay);

    /**
     * Sets the default hover delay to be used by all popover instances (running in the same JVM), except for those that have hover delay configured using setHoverDelay(int).
     * @param defaultHoverDelay
     * @return
     */
    C defaultHoverDelay(int defaultHoverDelay);

    /**
     * The delay in milliseconds before the popover is opened on target focus.
     * @param focusDelay
     * @return
     */
    C focusDelay(int focusDelay);

    /**
     * The id of the element to be used as the popover target value.
     * @param id
     * @return
     */
    C forId(String id);

    /**
     * Sets the height of the popover overlay content area.
     * @param height
     * @return
     */
    C height(String height);

    /**
     * The delay in milliseconds before the popover is closed on losing hover.
     * @param hideDelay
     * @return
     */
    C hideDelay(int hideDelay);

    /**
     * The delay in milliseconds before the popover is opened on target hover.
     * @param hoverDelay
     * @return
     */
    C hoverDelay(int hoverDelay);

    /**
     * Sets whether component should open modal or modeless popover.
     * @param modal
     * @return
     */
    C modal(boolean modal);

    /**
     * Sets whether component should open modal or modeless popover and whether the component should show a backdrop (modality curtain) when opened.
     * @param modal
     * @param backdropVisible
     * @return
     */
    C modal(boolean modal, boolean backdropVisible);

    /**
     * Opens or closes the popover.
     * @param opened
     * @return
     */
    C opened(boolean opened);

    /**
     * Sets whether the popover can be opened via target click.
     * @param openOnClick
     * @return
     */
    C openOnClick(boolean openOnClick);

    /**
     * Sets whether the popover can be opened via target focus.
     * @param openOnFocus
     * @return
     */
    C openOnFocus(boolean openOnFocus);

    /**
     * Sets whether the popover can be opened via target hover.
     * @param openOnHover
     * @return
     */
    C openOnHover(boolean openOnHover);

    /**
     * Sets the ARIA role for the overlay element, used by screen readers.
     * @param role
     * @return
     */
    C overlayRole(String role);

    /**
     * Sets position of the popover with respect to its target.
     * @param position
     * @return
     */
    C position(PopoverPosition position);

    /**
     * Sets the target component for this popover.
     * @param target
     * @return
     */
    C target(Component target);

    /**
     * Sets the width of the popover overlay content area.
     * @param width
     * @return
     */
    C width(String width);

    /***
     * Opens the popover.
     */

    void open();

    default C arrow() {
        return withThemeVariants(PopoverVariant.ARROW);
    }

}
