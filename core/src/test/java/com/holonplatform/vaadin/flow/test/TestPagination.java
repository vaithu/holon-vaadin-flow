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

import com.holonplatform.vaadin.flow.components.ItemListingPaginationBar;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Pagination} component family and
 * {@link ItemListingPaginationBar} page-window algorithm.
 *
 * <p>Tests run without a live Vaadin UI or VaadinService.
 * Browser interaction (click navigation, scrollToIndex) is not verified here.</p>
 */
class TestPagination {

    // =========================================================================
    // Pagination — structure
    // =========================================================================

    @Test
    void pagination_hasBaseClass() {
        Pagination p = new Pagination();
        assertTrue(p.getClassNames().contains("pagination"));
    }

    @Test
    void pagination_hasRoleNavigation() {
        Pagination p = new Pagination();
        assertEquals("navigation", p.getElement().getAttribute("role"));
    }

    @Test
    void pagination_hasAriaLabel() {
        Pagination p = new Pagination();
        // aria-label defaults to "Page navigation" (localizable; falls back to this English default)
        assertEquals("Page navigation", p.getElement().getAttribute("aria-label"));
    }

    @Test
    void pagination_containsOneChild_thePaginationContent() {
        Pagination p = new Pagination();
        assertEquals(1, p.getElement().getChildCount());
        assertEquals("ul", p.getElement().getChild(0).getTag());
    }

    @Test
    void pagination_getContent_returnsNonNull() {
        assertNotNull(new Pagination().getContent());
    }

    // =========================================================================
    // PaginationContent
    // =========================================================================

    @Test
    void paginationContent_hasBaseClass() {
        PaginationContent c = new PaginationContent();
        assertTrue(c.getClassNames().contains("pagination__content"));
    }

    @Test
    void paginationContent_add_storesItems() {
        PaginationContent c = new PaginationContent();
        c.add(new PaginationItem(), new PaginationItem());
        assertEquals(2, c.getElement().getChildCount());
    }

    @Test
    void paginationContent_add_nullSkipped() {
        PaginationContent c = new PaginationContent();
        assertDoesNotThrow(() -> c.add(new PaginationItem(), null, new PaginationItem()));
        assertEquals(2, c.getElement().getChildCount());
    }

    @Test
    void paginationContent_add_nullArray_doesNotThrow() {
        PaginationContent c = new PaginationContent();
        assertDoesNotThrow(() -> c.add((PaginationItem[]) null));
    }

    @Test
    void paginationContent_clear_removesAllItems() {
        PaginationContent c = new PaginationContent();
        c.add(new PaginationItem(), new PaginationItem(), new PaginationItem());
        c.clear();
        assertEquals(0, c.getElement().getChildCount());
    }

    // =========================================================================
    // PaginationItem
    // =========================================================================

    @Test
    void paginationItem_hasBaseClass() {
        PaginationItem item = new PaginationItem();
        assertTrue(item.getClassNames().contains("pagination__item"));
    }

    @Test
    void paginationItem_withContent_hasOneChild() {
        PaginationItem item = new PaginationItem(new PaginationEllipsis());
        assertEquals(1, item.getElement().getChildCount());
    }

    @Test
    void paginationItem_nullContent_isEmpty() {
        PaginationItem item = new PaginationItem(null);
        assertEquals(0, item.getElement().getChildCount());
    }

    // =========================================================================
    // PaginationLink
    // =========================================================================

    @Test
    void paginationLink_hasBaseClass() {
        PaginationLink link = new PaginationLink(3, false);
        assertTrue(link.getClassNames().contains("pagination__link"));
    }

    @Test
    void paginationLink_inactive_noActiveClass() {
        PaginationLink link = new PaginationLink(3, false);
        assertFalse(link.getClassNames().contains("pagination__link--active"));
    }

    @Test
    void paginationLink_active_hasActiveClass() {
        PaginationLink link = new PaginationLink(3, true);
        assertTrue(link.getClassNames().contains("pagination__link--active"));
    }

    @Test
    void paginationLink_active_hasAriaCurrentPage() {
        PaginationLink link = new PaginationLink(3, true);
        assertEquals("page", link.getElement().getAttribute("aria-current"));
    }

    @Test
    void paginationLink_hasAriaLabel() {
        PaginationLink link = new PaginationLink(5, false);
        assertEquals("Go to page 5", link.getElement().getAttribute("aria-label"));
    }

    @Test
    void paginationLink_getPage_returnsCorrectNumber() {
        PaginationLink link = new PaginationLink(7, false);
        assertEquals(7, link.getPage());
    }

    @Test
    void paginationLink_setDisabled_addsDisabledClass() {
        PaginationLink link = new PaginationLink(2, false);
        link.setDisabled(true);
        assertTrue(link.getClassNames().contains("pagination__link--disabled"));
        assertEquals("true", link.getElement().getAttribute("aria-disabled"));
    }

    @Test
    void paginationLink_setDisabled_false_removesClass() {
        PaginationLink link = new PaginationLink(2, false);
        link.setDisabled(true);
        link.setDisabled(false);
        assertFalse(link.getClassNames().contains("pagination__link--disabled"));
        assertNull(link.getElement().getAttribute("aria-disabled"));
    }

    // =========================================================================
    // PaginationPrevious
    // =========================================================================

    @Test
    void paginationPrevious_hasBaseClass() {
        PaginationPrevious prev = new PaginationPrevious();
        assertTrue(prev.getClassNames().contains("pagination__previous"));
    }

    @Test
    void paginationPrevious_hasAriaLabel() {
        PaginationPrevious prev = new PaginationPrevious();
        assertEquals("Go to previous page", prev.getElement().getAttribute("aria-label"));
    }

    @Test
    void paginationPrevious_hasTwoChildren_iconAndLabel() {
        PaginationPrevious prev = new PaginationPrevious();
        assertEquals(2, prev.getElement().getChildCount());
    }

    @Test
    void paginationPrevious_setDisabled_addsDisabledClass() {
        PaginationPrevious prev = new PaginationPrevious();
        prev.setDisabled(true);
        assertTrue(prev.getClassNames().contains("pagination__link--disabled"));
        assertEquals("true", prev.getElement().getAttribute("aria-disabled"));
    }

    @Test
    void paginationPrevious_setDisabled_false_removesClass() {
        PaginationPrevious prev = new PaginationPrevious();
        prev.setDisabled(true);
        prev.setDisabled(false);
        assertFalse(prev.getClassNames().contains("pagination__link--disabled"));
    }

    // =========================================================================
    // PaginationNext
    // =========================================================================

    @Test
    void paginationNext_hasBaseClass() {
        PaginationNext next = new PaginationNext();
        assertTrue(next.getClassNames().contains("pagination__next"));
    }

    @Test
    void paginationNext_hasAriaLabel() {
        PaginationNext next = new PaginationNext();
        assertEquals("Go to next page", next.getElement().getAttribute("aria-label"));
    }

    @Test
    void paginationNext_hasTwoChildren_labelAndIcon() {
        PaginationNext next = new PaginationNext();
        assertEquals(2, next.getElement().getChildCount());
    }

    @Test
    void paginationNext_setDisabled_addsDisabledClass() {
        PaginationNext next = new PaginationNext();
        next.setDisabled(true);
        assertTrue(next.getClassNames().contains("pagination__link--disabled"));
    }

    // =========================================================================
    // PaginationEllipsis
    // =========================================================================

    @Test
    void paginationEllipsis_hasBaseClass() {
        PaginationEllipsis e = new PaginationEllipsis();
        assertTrue(e.getClassNames().contains("pagination__ellipsis"));
    }

    @Test
    void paginationEllipsis_hasAriaHidden() {
        PaginationEllipsis e = new PaginationEllipsis();
        assertEquals("true", e.getElement().getAttribute("aria-hidden"));
    }

    @Test
    void paginationEllipsis_hasOneChild_icon() {
        PaginationEllipsis e = new PaginationEllipsis();
        assertEquals(1, e.getElement().getChildCount());
    }

    // =========================================================================
    // ItemListingPaginationBar.computePageWindow — algorithm unit tests
    // =========================================================================

    // --- Single / zero page ---

    @Test
    void window_zeroPages_returnsEmpty() {
        List<Integer> w = ItemListingPaginationBar.computePageWindow(1, 0, 1);
        assertTrue(w.isEmpty());
    }

    @Test
    void window_onePage_returnsJustPage1() {
        List<Integer> w = ItemListingPaginationBar.computePageWindow(1, 1, 1);
        assertEquals(List.of(1), w);
    }

    // --- Small totals (all pages shown, no ellipsis) ---

    @Test
    void window_5pages_current3_showsAll() {
        List<Integer> w = ItemListingPaginationBar.computePageWindow(3, 5, 1);
        assertEquals(List.of(1, 2, 3, 4, 5), w);
    }

    @Test
    void window_7pages_current4_showsAll() {
        // threshold = 7, so exactly 7 pages shows all
        List<Integer> w = ItemListingPaginationBar.computePageWindow(4, 7, 1);
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7), w);
    }

    // --- Large totals: left edge ---

    @Test
    void window_10pages_current1_leftEdge() {
        // Current=1: window=[1,2], no left ellipsis, right ellipsis before 10
        // expected: [1, 2, 3, -1, 10]
        List<Integer> w = ItemListingPaginationBar.computePageWindow(1, 10, 1);
        assertEquals(1, w.get(0));
        // last element must be 10
        assertEquals(10, w.get(w.size() - 1));
        // must contain -1 (ellipsis) somewhere
        assertTrue(w.contains(-1));
        // must not start with ellipsis
        assertNotEquals(-1, (int) w.get(1)); // page 2 should be directly after 1
    }

    @Test
    void window_10pages_current2() {
        List<Integer> w = ItemListingPaginationBar.computePageWindow(2, 10, 1);
        assertEquals(1, w.get(0));
        assertEquals(10, w.get(w.size() - 1));
        assertTrue(w.contains(2));
        assertTrue(w.contains(-1)); // right ellipsis
    }

    // --- Large totals: middle ---

    @Test
    void window_10pages_current5_bothEllipses() {
        // expected: [1, -1, 4, 5, 6, -1, 10]
        List<Integer> w = ItemListingPaginationBar.computePageWindow(5, 10, 1);
        assertEquals(1,  w.get(0));
        assertEquals(10, w.get(w.size() - 1));
        assertEquals(-1, (int) w.get(1));                   // left ellipsis
        assertEquals(-1, (int) w.get(w.size() - 2));         // right ellipsis
        assertTrue(w.contains(4));
        assertTrue(w.contains(5));
        assertTrue(w.contains(6));
    }

    // --- Large totals: right edge ---

    @Test
    void window_10pages_current10_rightEdge() {
        // expected: [1, -1, 8, 9, 10]
        List<Integer> w = ItemListingPaginationBar.computePageWindow(10, 10, 1);
        assertEquals(1,  w.get(0));
        assertEquals(10, w.get(w.size() - 1));
        assertTrue(w.contains(-1));  // left ellipsis
        assertNotEquals(-1, (int) w.get(w.size() - 2)); // no right ellipsis
        assertTrue(w.contains(9));
    }

    @Test
    void window_10pages_current9() {
        List<Integer> w = ItemListingPaginationBar.computePageWindow(9, 10, 1);
        assertEquals(1,  w.get(0));
        assertEquals(10, w.get(w.size() - 1));
        assertTrue(w.contains(8));
        assertTrue(w.contains(9));
    }

    // --- Window never contains duplicate ellipsis adjacently or out-of-order pages ---

    @Test
    void window_noDuplicatePages() {
        for (int page = 1; page <= 10; page++) {
            List<Integer> w = ItemListingPaginationBar.computePageWindow(page, 10, 1);
            long distinct = w.stream().filter(v -> v != -1).distinct().count();
            long total    = w.stream().filter(v -> v != -1).count();
            assertEquals(distinct, total, "Duplicate page number at page " + page);
        }
    }

    @Test
    void window_firstElementAlwaysPage1() {
        for (int page = 1; page <= 10; page++) {
            List<Integer> w = ItemListingPaginationBar.computePageWindow(page, 10, 1);
            assertFalse(w.isEmpty());
            assertEquals(1, (int) w.get(0), "First element must be 1 for current=" + page);
        }
    }

    @Test
    void window_lastElementAlwaysTotalPages() {
        for (int page = 1; page <= 10; page++) {
            List<Integer> w = ItemListingPaginationBar.computePageWindow(page, 10, 1);
            assertFalse(w.isEmpty());
            assertEquals(10, (int) w.get(w.size() - 1),
                    "Last element must be 10 for current=" + page);
        }
    }

    @Test
    void window_currentPageAlwaysPresent() {
        for (int page = 1; page <= 10; page++) {
            List<Integer> w = ItemListingPaginationBar.computePageWindow(page, 10, 1);
            assertTrue(w.contains(page), "Current page " + page + " missing from window");
        }
    }

    // =========================================================================
    // Full composition — manual Pagination assembly
    // =========================================================================

    @Test
    void fullComposition_manualPagination() {
        Pagination nav = new Pagination();
        PaginationContent content = nav.getContent();

        PaginationPrevious prev = new PaginationPrevious();
        prev.setDisabled(true);
        content.add(new PaginationItem(prev));

        content.add(new PaginationItem(new PaginationLink(1, true)));
        content.add(new PaginationItem(new PaginationEllipsis()));
        content.add(new PaginationItem(new PaginationLink(10, false)));

        PaginationNext next = new PaginationNext();
        content.add(new PaginationItem(next));

        // nav > ul > 5 li items
        assertEquals(5, nav.getContent().getElement().getChildCount());
    }
}

