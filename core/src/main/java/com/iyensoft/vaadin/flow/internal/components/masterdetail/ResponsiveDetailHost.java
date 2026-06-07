package com.iyensoft.vaadin.flow.internal.components.masterdetail;

import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;

import java.util.function.Function;

/**
 * Owns the desktop detail slot ↔ mobile {@link Sheet} placement of dynamic detail
 * components for a master-detail screen.
 *
 * <h3>Build-once strategy</h3>
 * <p>The configured {@code detailContentProvider} is invoked exactly once — on the
 * first {@link #place} call — and the resulting components are cached. Subsequent
 * calls only <em>move</em> the same component instances between the desktop slot
 * and the mobile sheet (or do nothing if they are already in the correct location),
 * which keeps any {@link com.vaadin.flow.signals.Signal#effect Signal.effect} bound
 * to those components alive across viewport rotations.</p>
 *
 * <h3>Slot visibility</h3>
 * <p>The desktop slot uses the CSS class {@code mdl-detail--no-selection} as its
 * "empty" state — added by {@link #hideDesktop()} and removed by {@link #showDesktop()}.
 * The cached components are never removed from the DOM; the placeholder is rendered
 * via a CSS {@code ::before} pseudo-element from {@code master-details.css}.</p>
 */
public final class ResponsiveDetailHost {

    private static final String CLASS_NO_SELECTION = "mdl-detail--no-selection";

    private final Layout dynamicSlot;
    private final Sheet  mobileSheet;

    /** Built lazily on first {@link #place} call. */
    private Component[] cachedComponents;

    private enum Location { NONE, DESKTOP, MOBILE }
    private Location location = Location.NONE;

    public ResponsiveDetailHost(Layout dynamicSlot, Sheet mobileSheet) {
        this.dynamicSlot = dynamicSlot;
        this.mobileSheet = mobileSheet;
        // Initial state: nothing selected — show placeholder.
        dynamicSlot.addClassName(CLASS_NO_SELECTION);
    }

    // -- placement -----------------------------------------------------------

    /**
     * Renders the detail for {@code item} in the appropriate container for {@code mode}.
     * Builds the components once on the first call; on subsequent calls only moves
     * the cached components between the desktop slot and mobile sheet as needed.
     */
    public <T> void place(T item, ViewMode mode, Function<T, Component[]> contentProvider) {
        ensureComponents(item, contentProvider);
        if (isMobile(mode)) {
            placeInMobile();
            mobileSheet.open();
        } else {
            placeInDesktop();
            showDesktop();
        }
    }

    /**
     * Hides the detail: removes desktop visibility and closes the mobile sheet.
     * Cached components remain in the DOM so reactive subscriptions are preserved.
     */
    public void hide() {
        hideDesktop();
        if (mobileSheet.isOpen()) {
            mobileSheet.close();
        }
    }

    /**
     * Reacts to a viewport-mode transition. Mirrors {@link #place} for the new
     * mode if a current item was supplied; mirrors {@link #hide} otherwise.
     */
    public <T> void onModeChanged(ViewMode newMode,
                                  T currentItem,
                                  Function<T, Component[]> contentProvider) {
        if (isMobile(newMode)) {
            hideDesktop();
            if (currentItem != null) {
                ensureComponents(currentItem, contentProvider);
                placeInMobile();
                mobileSheet.open();
            }
        } else {
            if (mobileSheet.isOpen()) mobileSheet.close();
            if (currentItem != null) {
                ensureComponents(currentItem, contentProvider);
                placeInDesktop();
                showDesktop();
            }
        }
    }

    // -- lifecycle hooks -----------------------------------------------------

    /** Detaches the mobile sheet if currently attached (call from owner's onDetach). */
    public void onOwnerDetach() {
        if (mobileSheet.isAttached()) {
            mobileSheet.detach();
        }
    }

    // -- internal ------------------------------------------------------------

    private <T> void ensureComponents(T firstItem, Function<T, Component[]> contentProvider) {
        if (cachedComponents == null) {
            cachedComponents = contentProvider.apply(firstItem);
            location = Location.NONE;
        }
    }

    private void placeInDesktop() {
        if (location != Location.DESKTOP && cachedComponents != null) {
            dynamicSlot.add(cachedComponents); // Vaadin auto-detaches from sheet first
            location = Location.DESKTOP;
        }
    }

    private void placeInMobile() {
        if (location != Location.MOBILE && cachedComponents != null) {
            mobileSheet.setContent(cachedComponents); // Vaadin auto-detaches from slot first
            location = Location.MOBILE;
        }
    }

    private void hideDesktop() {
        dynamicSlot.addClassName(CLASS_NO_SELECTION);
    }

    private void showDesktop() {
        dynamicSlot.removeClassName(CLASS_NO_SELECTION);
    }

    private static boolean isMobile(ViewMode mode) {
        return mode == ViewMode.MOBILE
                || mode == ViewMode.MOBILE_PORTRAIT
                || mode == ViewMode.MOBILE_LANDSCAPE;
    }
}


