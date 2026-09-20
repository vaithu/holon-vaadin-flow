package com.vaadin.flow.component.page;

/**
 * Test-only bridge to {@link Page}'s package-private {@code setWindowSize}.
 *
 * <p>Lives in {@code com.vaadin.flow.component.page} so it can reach the package-private setter
 * that Vaadin normally calls from {@code ExtendedClientDetails} during bootstrap. Tests need it to
 * simulate a viewport, because {@link Page#windowSizeSignal()} only exposes a read-only view and
 * defaults to {@code 0x0} — which every responsive component would classify as mobile.</p>
 */
public final class PageTestSupport {

    private PageTestSupport() {
    }

    /**
     * Seeds the window-size signal of {@code page} as if the client had reported this viewport.
     *
     * @param page   the page to update
     * @param width  the window inner width in pixels
     * @param height the window inner height in pixels
     */
    public static void setWindowSize(Page page, int width, int height) {
        page.setWindowSize(width, height);
    }
}
