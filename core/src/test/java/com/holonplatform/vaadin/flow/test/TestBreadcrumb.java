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
package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.RouterLink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the Breadcrumb component family:
 * {@link Breadcrumb}, {@link BreadcrumbItem}, {@link BreadcrumbPage},
 * {@link BreadcrumbSeparator}, {@link BreadcrumbEllipsis}.
 *
 * <p>Tests run without a live Vaadin UI or VaadinService. RouterLink is created
 * with its no-arg constructor to avoid route resolution, and href is set directly
 * via the element attribute API. AfterNavigationEvent is mocked with Mockito.</p>
 */
class TestBreadcrumb {

    // =========================================================================
    // Breadcrumb — constructor and base state
    // =========================================================================

    @Test
    void breadcrumb_default_hasBaseClassAndAriaLabel() {
        Breadcrumb bc = new Breadcrumb();

        assertTrue(bc.getClassNames().contains("breadcrumb"), "base 'breadcrumb' class must be present");
        assertEquals("Breadcrumb", bc.getElement().getAttribute("aria-label"),
                "aria-label must be 'Breadcrumb'");
    }

    @Test
    void breadcrumb_default_hasOneChildOl() {
        Breadcrumb bc = new Breadcrumb();
        // The Nav wraps a single <ol breadcrumb__list>
        assertEquals(1, bc.getElement().getChildCount(), "nav must have exactly one child (<ol>)");
        assertTrue(bc.getElement().getChild(0).getAttribute("class").contains("breadcrumb__list"),
                "<ol> must have 'breadcrumb__list' class");
    }

    @Test
    void breadcrumb_constructorWithItems_populatesOl() {
        BreadcrumbSeparator sep = new BreadcrumbSeparator();
        BreadcrumbPage page = new BreadcrumbPage("Breadcrumb");

        Breadcrumb bc = new Breadcrumb(
                new BreadcrumbItem(linkWithHref("home")),
                sep,
                page
        );

        assertEquals(3, olChildCount(bc), "3 items must be in the <ol>");
    }

    // =========================================================================
    // Breadcrumb — add / remove
    // =========================================================================

    @Test
    void breadcrumb_add_appendsItems() {
        Breadcrumb bc = new Breadcrumb();
        bc.add(new BreadcrumbItem(linkWithHref("home")));
        bc.add(new BreadcrumbSeparator());
        bc.add(new BreadcrumbPage("Current"));

        assertEquals(3, olChildCount(bc));
    }

    @Test
    void breadcrumb_removeAll_clearsOl() {
        Breadcrumb bc = new Breadcrumb();
        bc.add(new BreadcrumbItem(linkWithHref("home")));
        bc.add(new BreadcrumbPage("Current"));

        bc.removeAll();

        assertEquals(0, olChildCount(bc), "<ol> must be empty after removeAll");
    }

    @Test
    void breadcrumb_remove_specificItem_removesIt() {
        BreadcrumbPage page = new BreadcrumbPage("Current");
        Breadcrumb bc = new Breadcrumb();
        bc.add(new BreadcrumbItem(linkWithHref("home")));
        bc.add(page);

        bc.remove(page);

        assertEquals(1, olChildCount(bc), "only the item crumb should remain");
    }

    // =========================================================================
    // Breadcrumb — addWithSeparators
    // =========================================================================

    @Test
    void addWithSeparators_twoItems_insertsSeparatorBetween() {
        Breadcrumb bc = new Breadcrumb();
        bc.addWithSeparators(
                new BreadcrumbItem(linkWithHref("home")),
                new BreadcrumbPage("Dashboard")
        );

        // 2 items + 1 separator = 3
        assertEquals(3, olChildCount(bc));
        // The middle child must be a separator
        assertTrue(bc.getElement().getChild(0).getChild(1).getAttribute("class").contains("breadcrumb__separator"),
                "middle child must be a separator");
    }

    @Test
    void addWithSeparators_threeItems_insertsTwoSeparators() {
        Breadcrumb bc = new Breadcrumb();
        bc.addWithSeparators(
                new BreadcrumbItem(linkWithHref("home")),
                new BreadcrumbItem(linkWithHref("components")),
                new BreadcrumbPage("Breadcrumb")
        );

        // 3 items + 2 separators = 5
        assertEquals(5, olChildCount(bc));
    }

    @Test
    void addWithSeparators_singleItem_noSeparatorAdded() {
        Breadcrumb bc = new Breadcrumb();
        bc.addWithSeparators(new BreadcrumbPage("Only"));

        assertEquals(1, olChildCount(bc), "no separator for a single item");
    }

    @Test
    void addWithSeparators_appendedToExistingItems_prependsSeparator() {
        Breadcrumb bc = new Breadcrumb();
        bc.add(new BreadcrumbItem(linkWithHref("home")));         // 1 existing item
        bc.addWithSeparators(new BreadcrumbPage("Current"));      // should add sep + item

        // 1 existing + 1 separator + 1 new = 3
        assertEquals(3, olChildCount(bc));
    }

    @Test
    void addWithSeparators_nullArray_doesNotThrow() {
        Breadcrumb bc = new Breadcrumb();
        assertDoesNotThrow(() -> bc.addWithSeparators((com.vaadin.flow.component.html.ListItem[]) null));
        assertEquals(0, olChildCount(bc));
    }

    @Test
    void addWithSeparators_emptyArray_doesNotThrow() {
        Breadcrumb bc = new Breadcrumb();
        assertDoesNotThrow(() -> bc.addWithSeparators());
        assertEquals(0, olChildCount(bc));
    }

    @Test
    void addWithSeparators_includesEllipsis_separatorsAroundIt() {
        Breadcrumb bc = new Breadcrumb();
        bc.addWithSeparators(
                new BreadcrumbItem(linkWithHref("home")),
                new BreadcrumbEllipsis(),
                new BreadcrumbItem(linkWithHref("components")),
                new BreadcrumbPage("Breadcrumb")
        );

        // 4 items + 3 separators = 7
        assertEquals(7, olChildCount(bc));
    }

    // =========================================================================
    // Breadcrumb — setWithSeparators
    // =========================================================================

    @Test
    void setWithSeparators_clearsExistingAndRepopulates() {
        Breadcrumb bc = new Breadcrumb();
        bc.add(new BreadcrumbItem(linkWithHref("old")));    // 1 old item

        bc.setWithSeparators(
                new BreadcrumbItem(linkWithHref("new-home")),
                new BreadcrumbPage("New")
        );

        // Old item gone; 2 new items + 1 separator = 3
        assertEquals(3, olChildCount(bc));
    }

    // =========================================================================
    // Breadcrumb — setSeparatorSupplier
    // =========================================================================

    @Test
    void setSeparatorSupplier_null_throwsIllegalArgument() {
        Breadcrumb bc = new Breadcrumb();
        assertThrows(IllegalArgumentException.class, () -> bc.setSeparatorSupplier(null));
    }

    @Test
    void setSeparatorSupplier_customFactory_usedByAddWithSeparators() {
        Breadcrumb bc = new Breadcrumb();
        // Custom separator: "<" span
        bc.setSeparatorSupplier(() -> new BreadcrumbSeparator(new Span("<")));

        bc.addWithSeparators(
                new BreadcrumbItem(linkWithHref("home")),
                new BreadcrumbPage("Current")
        );

        // 2 items + 1 custom separator = 3
        assertEquals(3, olChildCount(bc));
        // The separator must use 'breadcrumb__separator'
        assertTrue(bc.getElement().getChild(0).getChild(1).getAttribute("class").contains("breadcrumb__separator"));
    }

    // =========================================================================
    // BreadcrumbItem — constructor
    // =========================================================================

    @Test
    void breadcrumbItem_routerLink_hasBreadcrumbItemClass() {
        BreadcrumbItem item = new BreadcrumbItem(linkWithHref("home"));
        assertTrue(item.getClassNames().contains("breadcrumb__item"));
    }

    @Test
    void breadcrumbItem_routerLink_linkReceivesBreadcrumbLinkClass() {
        RouterLink link = linkWithHref("home");
        new BreadcrumbItem(link);   // constructor applies the class

        assertTrue(link.getClassNames().contains("breadcrumb__link"),
                "RouterLink must receive 'breadcrumb__link' class");
    }

    @Test
    void breadcrumbItem_routerLink_isFirstChild() {
        RouterLink link = linkWithHref("home");
        BreadcrumbItem item = new BreadcrumbItem(link);

        assertEquals(1, item.getElement().getChildCount(), "exactly one child (<a>)");
    }

    @Test
    void breadcrumbItem_componentConstructor_hasItemClassAndChild() {
        Span icon = new Span("★");
        BreadcrumbItem item = new BreadcrumbItem(icon);

        assertTrue(item.getClassNames().contains("breadcrumb__item"));
        assertEquals(1, item.getElement().getChildCount());
    }

    @Test
    void breadcrumbItem_doesNotHaveHardcodedFlexClass() {
        BreadcrumbItem item = new BreadcrumbItem(linkWithHref("home"));
        assertFalse(item.getClassNames().contains("flex"),
                "'flex' utility class must not be hardcoded — was removed in fix");
    }

    // =========================================================================
    // BreadcrumbItem — afterNavigation
    // =========================================================================

    @Test
    void afterNavigation_matchingFullPath_setsAriaCurrent() {
        RouterLink link = linkWithHref("settings/profile");
        BreadcrumbItem item = new BreadcrumbItem(link);

        item.afterNavigation(mockNavEvent("settings/profile"));

        assertEquals("page", link.getElement().getAttribute("aria-current"),
                "aria-current='page' must be set when path matches");
    }

    @Test
    void afterNavigation_nonMatchingPath_removesAriaCurrent() {
        RouterLink link = linkWithHref("settings/profile");
        link.getElement().setAttribute("aria-current", "page"); // pre-set
        BreadcrumbItem item = new BreadcrumbItem(link);

        item.afterNavigation(mockNavEvent("completely/different"));

        assertNull(link.getElement().getAttribute("aria-current"),
                "aria-current must be cleared when path does not match");
    }

    @Test
    void afterNavigation_firstSegmentOnlyMatch_doesNotSetAriaCurrent() {
        // Old bug: only compared getFirstSegment() — "admin/users" would match "admin"
        RouterLink link = linkWithHref("admin/users");
        BreadcrumbItem item = new BreadcrumbItem(link);

        item.afterNavigation(mockNavEvent("admin")); // only first segment matches

        assertNull(link.getElement().getAttribute("aria-current"),
                "aria-current must NOT be set when only the first segment matches");
    }

    @Test
    void afterNavigation_multiSegmentPath_matchesCorrectly() {
        RouterLink link = linkWithHref("admin/users/roles");
        BreadcrumbItem item = new BreadcrumbItem(link);

        item.afterNavigation(mockNavEvent("admin/users/roles"));

        assertEquals("page", link.getElement().getAttribute("aria-current"),
                "aria-current must be set for multi-segment paths");
    }

    @Test
    void afterNavigation_nullLink_noException() {
        // BreadcrumbItem(Component) creates item with link=null
        BreadcrumbItem item = new BreadcrumbItem(new Div());
        assertDoesNotThrow(() -> item.afterNavigation(mockNavEvent("any/path")),
                "afterNavigation must be a no-op when no link is present");
    }

    // =========================================================================
    // BreadcrumbPage — constructors
    // =========================================================================

    @Test
    void breadcrumbPage_string_hasItemClass() {
        BreadcrumbPage page = new BreadcrumbPage("Components");
        assertTrue(page.getClassNames().contains("breadcrumb__item"));
    }

    @Test
    void breadcrumbPage_string_innerSpanHasPageClass() {
        BreadcrumbPage page = new BreadcrumbPage("Components");
        String cls = page.getElement().getChild(0).getAttribute("class");
        assertNotNull(cls);
        assertTrue(cls.contains("breadcrumb__page"), "inner span must have 'breadcrumb__page' class");
    }

    @Test
    void breadcrumbPage_string_innerSpanHasAriaDisabled() {
        BreadcrumbPage page = new BreadcrumbPage("Components");
        assertEquals("true", page.getElement().getChild(0).getAttribute("aria-disabled"));
    }

    @Test
    void breadcrumbPage_string_innerSpanHasAriaCurrent() {
        BreadcrumbPage page = new BreadcrumbPage("Components");
        assertEquals("page", page.getElement().getChild(0).getAttribute("aria-current"));
    }

    @Test
    void breadcrumbPage_string_innerSpanHasRoleLink() {
        BreadcrumbPage page = new BreadcrumbPage("Components");
        assertEquals("link", page.getElement().getChild(0).getAttribute("role"));
    }

    @Test
    void breadcrumbPage_string_textIsCorrect() {
        BreadcrumbPage page = new BreadcrumbPage("Breadcrumb");
        assertEquals("Breadcrumb", page.getElement().getChild(0).getText());
    }

    @Test
    void breadcrumbPage_localizable_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        BreadcrumbPage[] holder = new BreadcrumbPage[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = new BreadcrumbPage(loc));

        assertEquals("TestUS", holder[0].getElement().getChild(0).getText());
    }

    @Test
    void breadcrumbPage_componentConstructor_addsChildrenToSpan() {
        Span icon  = new Span("icon");
        Span label = new Span("label");
        BreadcrumbPage page = new BreadcrumbPage(icon, label);

        assertTrue(page.getClassNames().contains("breadcrumb__item"));
        assertEquals(2, page.getElement().getChild(0).getChildCount(),
                "both components must be children of the inner span");
    }

    // =========================================================================
    // BreadcrumbPage — setText / setLocalizableText
    // =========================================================================

    @Test
    void breadcrumbPage_setText_updatesText() {
        BreadcrumbPage page = new BreadcrumbPage("Initial");
        page.setText("Updated");
        assertEquals("Updated", page.getElement().getChild(0).getText());
    }

    @Test
    void breadcrumbPage_setLocalizableText_updatesText() {
        BreadcrumbPage page = new BreadcrumbPage("Initial");
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        LocalizationTestUtils.withTestLocalizationContext(() -> page.setLocalizableText(loc));
        assertEquals("TestUS", page.getElement().getChild(0).getText());
    }

    // =========================================================================
    // BreadcrumbSeparator
    // =========================================================================

    @Test
    void breadcrumbSeparator_default_hasSeparatorClass() {
        BreadcrumbSeparator sep = new BreadcrumbSeparator();
        assertTrue(sep.getClassNames().contains("breadcrumb__separator"));
    }

    @Test
    void breadcrumbSeparator_default_hasAriaHidden() {
        BreadcrumbSeparator sep = new BreadcrumbSeparator();
        assertEquals("true", sep.getElement().getAttribute("aria-hidden"));
    }

    @Test
    void breadcrumbSeparator_default_hasRolePresentation() {
        BreadcrumbSeparator sep = new BreadcrumbSeparator();
        assertEquals("presentation", sep.getElement().getAttribute("role"));
    }

    @Test
    void breadcrumbSeparator_default_rendersSlashGlyph() {
        BreadcrumbSeparator sep = new BreadcrumbSeparator();
        // Single child is the "/" span
        assertEquals(1, sep.getElement().getChildCount());
        String childClass = sep.getElement().getChild(0).getAttribute("class");
        assertNotNull(childClass);
        assertTrue(childClass.contains("breadcrumb__separator-icon"),
                "default separator child must have 'breadcrumb__separator-icon' class");
    }

    @Test
    void breadcrumbSeparator_customComponent_usesCustomContent() {
        Span custom = new Span("›");
        BreadcrumbSeparator sep = new BreadcrumbSeparator(custom);

        assertTrue(sep.getClassNames().contains("breadcrumb__separator"));
        assertEquals(1, sep.getElement().getChildCount(), "one child (the custom component)");
    }

    @Test
    void breadcrumbSeparator_nullComponent_fallsBackToDefaultSlash() {
        BreadcrumbSeparator sep = new BreadcrumbSeparator((com.vaadin.flow.component.Component) null);

        assertEquals(1, sep.getElement().getChildCount());
        String cls = sep.getElement().getChild(0).getAttribute("class");
        assertTrue(cls != null && cls.contains("breadcrumb__separator-icon"),
                "null argument must fall back to default '/' glyph");
    }

    // =========================================================================
    // BreadcrumbEllipsis
    // =========================================================================

    @Test
    void breadcrumbEllipsis_default_hasItemClass() {
        BreadcrumbEllipsis ell = new BreadcrumbEllipsis();
        assertTrue(ell.getClassNames().contains("breadcrumb__item"));
    }

    @Test
    void breadcrumbEllipsis_default_innerSpanHasEllipsisClass() {
        BreadcrumbEllipsis ell = new BreadcrumbEllipsis();
        String cls = ell.getElement().getChild(0).getAttribute("class");
        assertNotNull(cls);
        assertTrue(cls.contains("breadcrumb__ellipsis"),
                "inner span must have 'breadcrumb__ellipsis' class");
    }

    @Test
    void breadcrumbEllipsis_default_innerSpanHasAriaHidden() {
        BreadcrumbEllipsis ell = new BreadcrumbEllipsis();
        assertEquals("true", ell.getElement().getChild(0).getAttribute("aria-hidden"));
    }

    @Test
    void breadcrumbEllipsis_default_innerSpanHasAriaLabelMore() {
        BreadcrumbEllipsis ell = new BreadcrumbEllipsis();
        assertEquals("More", ell.getElement().getChild(0).getAttribute("aria-label"));
    }

    @Test
    void breadcrumbEllipsis_default_innerSpanHasRolePresentation() {
        BreadcrumbEllipsis ell = new BreadcrumbEllipsis();
        assertEquals("presentation", ell.getElement().getChild(0).getAttribute("role"));
    }

    @Test
    void breadcrumbEllipsis_default_hasDotsChild() {
        BreadcrumbEllipsis ell = new BreadcrumbEllipsis();
        // ellipsis span → dots span
        assertEquals(1, ell.getElement().getChild(0).getChildCount());
        String dotsClass = ell.getElement().getChild(0).getChild(0).getAttribute("class");
        assertNotNull(dotsClass);
        assertTrue(dotsClass.contains("breadcrumb__ellipsis-icon"),
                "dots child must have 'breadcrumb__ellipsis-icon' class");
    }

    @Test
    void breadcrumbEllipsis_customComponent_usesCustomContent() {
        Span custom = new Span("...");
        BreadcrumbEllipsis ell = new BreadcrumbEllipsis(custom);

        assertTrue(ell.getClassNames().contains("breadcrumb__item"));
        // Custom content is wrapped inside the ellipsis span
        assertEquals(1, ell.getElement().getChild(0).getChildCount());
    }

    @Test
    void breadcrumbEllipsis_addEllipsisClickListener_addsClickableClass() {
        BreadcrumbEllipsis ell = new BreadcrumbEllipsis();
        ell.addEllipsisClickListener(e -> { /* reveal hidden items */ });

        String cls = ell.getElement().getChild(0).getAttribute("class");
        assertNotNull(cls);
        assertTrue(cls.contains("breadcrumb__ellipsis--clickable"),
                "'breadcrumb__ellipsis--clickable' class must be added when a listener is registered");
    }

    @Test
    void breadcrumbEllipsis_withoutListener_noClickableClass() {
        BreadcrumbEllipsis ell = new BreadcrumbEllipsis();
        String cls = ell.getElement().getChild(0).getAttribute("class");
        assertFalse(cls != null && cls.contains("breadcrumb__ellipsis--clickable"),
                "clickable class must be absent by default");
    }

    // =========================================================================
    // Full trail compositions
    // =========================================================================

    @Test
    void fullTrail_manual_separatorsAndPage() {
        Breadcrumb bc = new Breadcrumb(
                new BreadcrumbItem(linkWithHref("home")),
                new BreadcrumbSeparator(),
                new BreadcrumbItem(linkWithHref("components")),
                new BreadcrumbSeparator(),
                new BreadcrumbPage("Breadcrumb")
        );

        // 3 items + 2 separators = 5
        assertEquals(5, olChildCount(bc));
    }

    @Test
    void fullTrail_autoSeparators_homeComponentsPage() {
        Breadcrumb bc = new Breadcrumb();
        bc.addWithSeparators(
                new BreadcrumbItem(linkWithHref("home")),
                new BreadcrumbItem(linkWithHref("components")),
                new BreadcrumbPage("Breadcrumb")
        );

        assertEquals(5, olChildCount(bc));
    }

    @Test
    void fullTrail_withEllipsis_autoSeparators() {
        Breadcrumb bc = new Breadcrumb();
        bc.addWithSeparators(
                new BreadcrumbItem(linkWithHref("home")),
                new BreadcrumbEllipsis(),
                new BreadcrumbItem(linkWithHref("components")),
                new BreadcrumbPage("Breadcrumb")
        );

        // 4 items + 3 separators = 7
        assertEquals(7, olChildCount(bc));
    }

    @Test
    void fullTrail_customChevronSeparator() {
        Breadcrumb bc = new Breadcrumb();
        bc.setSeparatorSupplier(() -> new BreadcrumbSeparator(new Span("›")));
        bc.addWithSeparators(
                new BreadcrumbItem(linkWithHref("home")),
                new BreadcrumbItem(linkWithHref("docs")),
                new BreadcrumbPage("API")
        );

        assertEquals(5, olChildCount(bc));
        // Each separator (positions 1 and 3) must have breadcrumb__separator class
        assertTrue(bc.getElement().getChild(0).getChild(1).getAttribute("class").contains("breadcrumb__separator"));
        assertTrue(bc.getElement().getChild(0).getChild(3).getAttribute("class").contains("breadcrumb__separator"));
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Creates a {@link RouterLink} using the no-arg constructor (avoids VaadinService / Router)
     * and sets the href directly on the element so {@code getHref()} returns the given path.
     */
    private static RouterLink linkWithHref(String href) {
        RouterLink link = new RouterLink();
        link.getElement().setAttribute("href", href);
        return link;
    }

    /** Returns the number of children inside the breadcrumb's inner {@code <ol>} element. */
    private static int olChildCount(Breadcrumb bc) {
        return bc.getElement().getChild(0).getChildCount();
    }

    /**
     * Creates a mock {@link AfterNavigationEvent} whose {@code getLocation().getPath()}
     * returns the given path string.
     */
    private static AfterNavigationEvent mockNavEvent(String path) {
        AfterNavigationEvent event = mock(AfterNavigationEvent.class);
        Location location = mock(Location.class);
        when(event.getLocation()).thenReturn(location);
        when(location.getPath()).thenReturn(path);
        return event;
    }
}


