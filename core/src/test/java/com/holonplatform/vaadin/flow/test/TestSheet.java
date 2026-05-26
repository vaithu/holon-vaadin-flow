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
import com.holonplatform.vaadin.flow.components.builders.SheetBuilder;
import com.holonplatform.vaadin.flow.components.builders.SheetConfigurator;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.holonplatform.vaadin.flow.vaadinplus.components.SheetDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.SheetTitle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Sheet} component and its builder / configurator infrastructure.
 *
 * <p>Tests run without a live Vaadin UI. Browser-side behaviour (CSS transitions, History API,
 * {@code @ClientCallable}) requires an integration test; these unit tests verify the server-side
 * state machine only.</p>
 */
class TestSheet {

    // =========================================================================
    // Side enum
    // =========================================================================

    @Test
    void side_getCssClass_returnsCorrectModifier() {
        assertEquals("sheet--bottom", Sheet.Side.BOTTOM.getCssClass());
        assertEquals("sheet--left",   Sheet.Side.LEFT.getCssClass());
        assertEquals("sheet--right",  Sheet.Side.RIGHT.getCssClass());
    }

    // =========================================================================
    // Constructor — default state
    // =========================================================================

    @Test
    void constructor_default_isBottomSide() {
        Sheet sheet = new Sheet();
        assertEquals(Sheet.Side.BOTTOM, sheet.getSide());
        assertTrue(sheet.getClassNames().contains("sheet"));
        assertTrue(sheet.getClassNames().contains("sheet--bottom"));
    }

    @Test
    void constructor_isNotOpenByDefault() {
        Sheet sheet = new Sheet();
        assertFalse(sheet.isOpen());
        assertFalse(sheet.getClassNames().contains("sheet--open"));
    }

    @Test
    void constructor_closeOnBackdropClickIsTrue() {
        Sheet sheet = new Sheet();
        assertTrue(sheet.isCloseOnBackdropClick());
    }

    @Test
    void constructor_historyEnabledIsTrue() {
        Sheet sheet = new Sheet();
        assertTrue(sheet.isHistoryEnabled());
    }

    @Test
    void constructor_titleAndDescriptionAreNullByDefault() {
        Sheet sheet = new Sheet();
        assertNull(sheet.getSheetTitle());
        assertNull(sheet.getSheetDescription());
    }

    @Test
    void constructor_explicitSide_left() {
        Sheet sheet = new Sheet(Sheet.Side.LEFT);
        assertEquals(Sheet.Side.LEFT, sheet.getSide());
        assertTrue(sheet.getClassNames().contains("sheet--left"));
        assertFalse(sheet.getClassNames().contains("sheet--bottom"));
    }

    @Test
    void constructor_explicitSide_right() {
        Sheet sheet = new Sheet(Sheet.Side.RIGHT);
        assertEquals(Sheet.Side.RIGHT, sheet.getSide());
        assertTrue(sheet.getClassNames().contains("sheet--right"));
    }

    // =========================================================================
    // open() / close() / isOpen()
    // =========================================================================

    @Test
    void close_removesOpenClass() {
        Sheet sheet = new Sheet();
        // Manually add the open class (simulating open without a live UI)
        sheet.addClassName("sheet--open");
        assertTrue(sheet.isOpen());

        sheet.close();
        assertFalse(sheet.isOpen());
        assertFalse(sheet.getClassNames().contains("sheet--open"));
    }

    @Test
    void open_withoutUi_addsOpenClassOnly() {
        // Without a live UI, open() should not throw — it just adds the class
        Sheet sheet = new Sheet();
        // UI.getCurrent() is null in tests; open() guards against NPE
        sheet.open();
        assertTrue(sheet.isOpen());
    }

    @Test
    void open_thenClose_cycleworks() {
        Sheet sheet = new Sheet();
        sheet.open();
        assertTrue(sheet.isOpen());
        sheet.close();
        assertFalse(sheet.isOpen());
        sheet.open();
        assertTrue(sheet.isOpen());
    }

    // =========================================================================
    // closeFromHistory — back-button bridge
    // =========================================================================

    @Test
    void closeFromHistory_whenOpen_closesSheet() {
        Sheet sheet = new Sheet();
        sheet.addClassName("sheet--open"); // simulate open without live UI

        // Simulate back button
        sheet.closeFromHistory();

        assertFalse(sheet.isOpen());
    }

    @Test
    void closeFromHistory_whenAlreadyClosed_isNoOp() {
        AtomicInteger callCount = new AtomicInteger(0);
        Sheet sheet = new Sheet();
        sheet.setOnClose(callCount::incrementAndGet);
        // Sheet is NOT open — stale history entry

        sheet.closeFromHistory();

        assertFalse(sheet.isOpen());
        assertEquals(0, callCount.get(), "callback must NOT fire for stale history entry");
    }

    // =========================================================================
    // Callbacks
    // =========================================================================

    @Test
    void onClose_firesOnClose() {
        AtomicBoolean fired = new AtomicBoolean(false);
        Sheet sheet = new Sheet();
        sheet.setOnClose(() -> fired.set(true));
        sheet.addClassName("sheet--open");

        sheet.close();

        assertTrue(fired.get());
    }

    @Test
    void onOpen_firesOnOpen() {
        AtomicBoolean fired = new AtomicBoolean(false);
        Sheet sheet = new Sheet();
        sheet.setOnOpen(() -> fired.set(true));

        sheet.open(); // no live UI — just adds class

        assertTrue(fired.get());
    }

    @Test
    void onClose_doesNotFireWhenNeverSet() {
        Sheet sheet = new Sheet();
        sheet.addClassName("sheet--open");
        assertDoesNotThrow(sheet::close);
    }

    // =========================================================================
    // Side API
    // =========================================================================

    @Test
    void setSide_swapsCssClass() {
        Sheet sheet = new Sheet(Sheet.Side.BOTTOM);
        assertTrue(sheet.getClassNames().contains("sheet--bottom"));

        sheet.setSide(Sheet.Side.LEFT);
        assertFalse(sheet.getClassNames().contains("sheet--bottom"));
        assertTrue(sheet.getClassNames().contains("sheet--left"));
        assertEquals(Sheet.Side.LEFT, sheet.getSide());
    }

    @Test
    void setSide_right_appliesCorrectClass() {
        Sheet sheet = new Sheet();
        sheet.setSide(Sheet.Side.RIGHT);
        assertTrue(sheet.getClassNames().contains("sheet--right"));
        assertEquals(Sheet.Side.RIGHT, sheet.getSide());
    }

    // =========================================================================
    // Title API
    // =========================================================================

    @Test
    void setTitle_string_setsTitle() {
        Sheet sheet = new Sheet();
        sheet.setTitle("Filter options");
        assertNotNull(sheet.getSheetTitle());
        assertEquals("Filter options", sheet.getSheetTitle().getText());
    }

    @Test
    void setTitle_localizable_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        Sheet[] holder = new Sheet[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            holder[0] = new Sheet();
            holder[0].setTitle(loc);
        });
        assertEquals("TestUS", holder[0].getSheetTitle().getText());
    }

    @Test
    void setTitle_replacing_removesOld() {
        Sheet sheet = new Sheet();
        sheet.setTitle("First");
        sheet.setTitle("Second");
        assertEquals("Second", sheet.getSheetTitle().getText());
    }

    @Test
    void setTitle_null_clearsTitle() {
        Sheet sheet = new Sheet();
        sheet.setTitle("Title");
        sheet.setTitle((SheetTitle) null);
        assertNull(sheet.getSheetTitle());
    }

    // =========================================================================
    // Description API
    // =========================================================================

    @Test
    void setDescription_string_setsDescription() {
        Sheet sheet = new Sheet();
        sheet.setDescription("Narrow down your results.");
        assertNotNull(sheet.getSheetDescription());
        assertEquals("Narrow down your results.", sheet.getSheetDescription().getText());
    }

    @Test
    void setDescription_localizable_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        Sheet[] holder = new Sheet[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> {
            holder[0] = new Sheet();
            holder[0].setDescription(loc);
        });
        assertEquals("TestUS", holder[0].getSheetDescription().getText());
    }

    @Test
    void setDescription_null_clearsDescription() {
        Sheet sheet = new Sheet();
        sheet.setDescription("Desc");
        sheet.setDescription((SheetDescription) null);
        assertNull(sheet.getSheetDescription());
    }

    // =========================================================================
    // Content API
    // =========================================================================

    @Test
    void setContent_addsComponents() {
        Sheet sheet = new Sheet();
        sheet.setContent(new Button("Apply"), new Button("Reset"));
        assertNotNull(sheet);
    }

    @Test
    void clearContent_doesNotThrow() {
        Sheet sheet = new Sheet();
        sheet.setContent(new Div());
        sheet.clearContent();
        assertNotNull(sheet);
    }

    // =========================================================================
    // Behaviour API
    // =========================================================================

    @Test
    void setCloseOnBackdropClick_false_storesValue() {
        Sheet sheet = new Sheet();
        sheet.setCloseOnBackdropClick(false);
        assertFalse(sheet.isCloseOnBackdropClick());
    }

    @Test
    void setHistoryEnabled_false_storesValue() {
        Sheet sheet = new Sheet();
        sheet.setHistoryEnabled(false);
        assertFalse(sheet.isHistoryEnabled());
    }

    // =========================================================================
    // SheetTitle sub-component
    // =========================================================================

    @Test
    void sheetTitle_string_setsText() {
        SheetTitle t = new SheetTitle("Settings");
        assertEquals("Settings", t.getText());
        assertTrue(t.getClassNames().contains("sheet__title"));
    }

    @Test
    void sheetTitle_localizable_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        SheetTitle[] holder = new SheetTitle[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> holder[0] = new SheetTitle(loc));
        assertEquals("TestUS", holder[0].getText());
    }

    @Test
    void sheetTitle_components_addsChildren() {
        SheetTitle t = new SheetTitle(new Span("icon"), new Span("label"));
        assertEquals(2, t.getComponentCount());
    }

    // =========================================================================
    // SheetDescription sub-component
    // =========================================================================

    @Test
    void sheetDescription_string_setsText() {
        SheetDescription d = new SheetDescription("Choose your preferences.");
        assertEquals("Choose your preferences.", d.getText());
        assertTrue(d.getClassNames().contains("sheet__description"));
    }

    @Test
    void sheetDescription_setLocalizableText_updatesText() {
        SheetDescription d = new SheetDescription("initial");
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        LocalizationTestUtils.withTestLocalizationContext(() -> d.setLocalizableText(loc));
        assertEquals("TestUS", d.getText());
    }

    // =========================================================================
    // SheetBuilder — factories
    // =========================================================================

    @Test
    void sheetBuilder_create_returnsBuilder() {
        SheetBuilder builder = SheetBuilder.create();
        assertNotNull(builder);
        Sheet sheet = builder.build();
        assertNotNull(sheet);
        assertEquals(Sheet.Side.BOTTOM, sheet.getSide());
    }

    @Test
    void sheetBuilder_createWithSide_appliesSide() {
        Sheet sheet = SheetBuilder.create(Sheet.Side.LEFT).build();
        assertEquals(Sheet.Side.LEFT, sheet.getSide());
        assertTrue(sheet.getClassNames().contains("sheet--left"));
    }

    @Test
    void sheet_staticBuilderFactory() {
        Sheet sheet = Sheet.builder().build();
        assertNotNull(sheet);
        assertEquals(Sheet.Side.BOTTOM, sheet.getSide());
    }

    @Test
    void sheet_staticBuilderFactoryWithSide() {
        Sheet sheet = Sheet.builder(Sheet.Side.RIGHT).build();
        assertEquals(Sheet.Side.RIGHT, sheet.getSide());
    }

    // =========================================================================
    // SheetBuilder — fluent chaining
    // =========================================================================

    @Test
    void sheetBuilder_fullChain_allPropertiesSet() {
        AtomicBoolean openFired  = new AtomicBoolean(false);
        AtomicBoolean closeFired = new AtomicBoolean(false);

        Sheet sheet = SheetBuilder.create(Sheet.Side.BOTTOM)
                .title("Cart summary")
                .description("Review your items before checkout.")
                .content(new Div())
                .closeOnBackdropClick(true)
                .historyEnabled(false)
                .onOpen(() -> openFired.set(true))
                .onClose(() -> closeFired.set(true))
                .build();

        assertEquals(Sheet.Side.BOTTOM, sheet.getSide());
        assertEquals("Cart summary", sheet.getSheetTitle().getText());
        assertEquals("Review your items before checkout.", sheet.getSheetDescription().getText());
        assertFalse(sheet.isHistoryEnabled());
        assertTrue(sheet.isCloseOnBackdropClick());
        assertFalse(openFired.get(), "onOpen must not fire at build time");
        assertFalse(closeFired.get(), "onClose must not fire at build time");
    }

    @Test
    void sheetBuilder_localizableTitle() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        Sheet[] holder = new Sheet[1];
        LocalizationTestUtils.withTestLocalizationContext(() ->
                holder[0] = SheetBuilder.create().title(loc).build()
        );
        assertEquals("TestUS", holder[0].getSheetTitle().getText());
    }

    @Test
    void sheetBuilder_idAndStyleName() {
        Sheet sheet = SheetBuilder.create()
                .id("filter-sheet")
                .styleName("my-sheet")
                .build();
        assertEquals("filter-sheet", sheet.getId().orElse(null));
        assertTrue(sheet.getClassNames().contains("my-sheet"));
    }

    @Test
    void sheetBuilder_width() {
        Sheet sheet = SheetBuilder.create().width("100%").build();
        assertEquals("100%", sheet.getWidth());
    }

    @Test
    void sheetBuilder_prebuiltSubComponents() {
        SheetTitle title = new SheetTitle("Pre-built");
        SheetDescription desc = new SheetDescription("Pre-built desc");

        Sheet sheet = SheetBuilder.create()
                .title(title)
                .description(desc)
                .build();

        assertSame(title, sheet.getSheetTitle());
        assertSame(desc,  sheet.getSheetDescription());
    }

    // =========================================================================
    // SheetConfigurator.configure
    // =========================================================================

    @Test
    void sheetConfigurator_configure_returnsConfigurator() {
        SheetConfigurator.BaseSheetConfigurator cfg = SheetConfigurator.configure(new Sheet());
        assertNotNull(cfg);
    }

    @Test
    void sheetConfigurator_configure_mutatesExistingSheet() {
        Sheet sheet = new Sheet();
        SheetConfigurator.configure(sheet)
                .side(Sheet.Side.RIGHT)
                .title("Details")
                .description("Full item details")
                .historyEnabled(false)
                .id("detail-sheet");

        assertEquals(Sheet.Side.RIGHT, sheet.getSide());
        assertEquals("Details", sheet.getSheetTitle().getText());
        assertFalse(sheet.isHistoryEnabled());
        assertEquals("detail-sheet", sheet.getId().orElse(null));
    }

    // =========================================================================
    // Multiple sheets open simultaneously
    // =========================================================================

    @Test
    void multipleSheets_independentOpenCloseState() {
        Sheet bottom = new Sheet(Sheet.Side.BOTTOM);
        Sheet left   = new Sheet(Sheet.Side.LEFT);
        Sheet right  = new Sheet(Sheet.Side.RIGHT);

        bottom.open();
        left.open();

        assertTrue(bottom.isOpen());
        assertTrue(left.isOpen());
        assertFalse(right.isOpen());

        bottom.close();
        assertFalse(bottom.isOpen());
        assertTrue(left.isOpen());   // left unaffected
    }

    @Test
    void multipleSheets_lifoClose_via_closeFromHistory() {
        AtomicInteger closeOrder = new AtomicInteger(0);
        AtomicInteger firstClosedAt  = new AtomicInteger(-1);
        AtomicInteger secondClosedAt = new AtomicInteger(-1);

        Sheet first  = new Sheet(Sheet.Side.BOTTOM);
        Sheet second = new Sheet(Sheet.Side.BOTTOM);
        first.setOnClose(() -> firstClosedAt.set(closeOrder.incrementAndGet()));
        second.setOnClose(() -> secondClosedAt.set(closeOrder.incrementAndGet()));

        // Simulate both opened
        first.addClassName("sheet--open");
        second.addClassName("sheet--open");

        // Back button closes second first (LIFO)
        second.closeFromHistory();
        first.closeFromHistory();

        assertEquals(1, secondClosedAt.get(), "second must close first");
        assertEquals(2, firstClosedAt.get(),  "first must close second");
    }

    // =========================================================================
    // fullscreenOnMobile
    // =========================================================================

    @Test
    void setFullscreenOnMobile_defaultIsFalse() {
        Sheet sheet = new Sheet();
        assertFalse(sheet.isFullscreenOnMobile());
        assertFalse(sheet.getClassNames().contains("sheet--fullscreen-mobile"));
    }

    @Test
    void setFullscreenOnMobile_true_addsCssModifier() {
        Sheet sheet = new Sheet();
        sheet.setFullscreenOnMobile(true);
        assertTrue(sheet.isFullscreenOnMobile());
        assertTrue(sheet.getClassNames().contains("sheet--fullscreen-mobile"));
    }

    @Test
    void setFullscreenOnMobile_false_removesCssModifier() {
        Sheet sheet = new Sheet();
        sheet.setFullscreenOnMobile(true);
        sheet.setFullscreenOnMobile(false);
        assertFalse(sheet.isFullscreenOnMobile());
        assertFalse(sheet.getClassNames().contains("sheet--fullscreen-mobile"));
    }

    // =========================================================================
    // Back / Close button visibility
    // =========================================================================

    @Test
    void showBackButton_defaultIsTrue() {
        Sheet sheet = new Sheet();
        assertTrue(sheet.isShowBackButton());
    }

    @Test
    void setShowBackButton_false_storesValue() {
        Sheet sheet = new Sheet();
        sheet.setShowBackButton(false);
        assertFalse(sheet.isShowBackButton());
    }

    @Test
    void setShowBackButton_toggleRoundTrip() {
        Sheet sheet = new Sheet();
        sheet.setShowBackButton(false);
        sheet.setShowBackButton(true);
        assertTrue(sheet.isShowBackButton());
    }

    @Test
    void showCloseButton_defaultIsTrue() {
        Sheet sheet = new Sheet();
        assertTrue(sheet.isShowCloseButton());
    }

    @Test
    void setShowCloseButton_false_storesValue() {
        Sheet sheet = new Sheet();
        sheet.setShowCloseButton(false);
        assertFalse(sheet.isShowCloseButton());
    }

    @Test
    void setShowCloseButton_toggleRoundTrip() {
        Sheet sheet = new Sheet();
        sheet.setShowCloseButton(false);
        sheet.setShowCloseButton(true);
        assertTrue(sheet.isShowCloseButton());
    }

    // =========================================================================
    // close() idempotency
    // =========================================================================

    @Test
    void close_onAlreadyClosedSheet_isNoOp() {
        AtomicInteger callCount = new AtomicInteger(0);
        Sheet sheet = new Sheet();
        sheet.setOnClose(callCount::incrementAndGet);

        // Sheet is not open — close() must be a no-op
        sheet.close();
        assertEquals(0, callCount.get(), "onClose must not fire when sheet is already closed");
    }

    @Test
    void close_secondCall_afterFirstClose_isNoOp() {
        AtomicInteger callCount = new AtomicInteger(0);
        Sheet sheet = new Sheet();
        sheet.setOnClose(callCount::incrementAndGet);
        sheet.addClassName("sheet--open");

        sheet.close();   // first close — fires callback
        sheet.close();   // second close — sheet already closed, must be no-op

        assertEquals(1, callCount.get(), "onClose must fire exactly once");
    }

    // =========================================================================
    // Lazy content
    // =========================================================================

    @Test
    void lazyContent_invokedOnFirstOpen() {
        AtomicInteger supplierCallCount = new AtomicInteger(0);
        Sheet sheet = new Sheet();
        sheet.setLazyContent(() -> {
            supplierCallCount.incrementAndGet();
            return new com.vaadin.flow.component.Component[]{ new Div() };
        });

        sheet.open();

        assertEquals(1, supplierCallCount.get(), "supplier must be called exactly once on first open");
    }

    @Test
    void lazyContent_supplierDiscardedAfterFirstOpen() {
        AtomicInteger supplierCallCount = new AtomicInteger(0);
        Sheet sheet = new Sheet();
        sheet.setLazyContent(() -> {
            supplierCallCount.incrementAndGet();
            return new com.vaadin.flow.component.Component[]{ new Div() };
        });

        // First open populates content and discards the supplier reference
        sheet.open();
        // Close and re-open — supplier must NOT be called again
        sheet.addClassName("sheet--open");
        sheet.close();
        sheet.open();

        assertEquals(1, supplierCallCount.get(), "supplier must fire exactly once across multiple opens");
    }

    @Test
    void lazyContent_setContent_supersedes_lazySupplier() {
        AtomicBoolean supplierFired = new AtomicBoolean(false);
        Sheet sheet = new Sheet();
        sheet.setLazyContent(() -> {
            supplierFired.set(true);
            return new com.vaadin.flow.component.Component[]{ new Div() };
        });
        // Explicit content clears the lazy supplier
        sheet.setContent(new Span("explicit"));

        sheet.open();

        assertFalse(supplierFired.get(), "lazy supplier must NOT fire after setContent() supersedes it");
    }

    @Test
    void lazyContent_nullResult_doesNotThrow() {
        Sheet sheet = new Sheet();
        sheet.setLazyContent(() -> null);
        assertDoesNotThrow(sheet::open);
    }

    // =========================================================================
    // onOpen — not fired at build/construction time
    // =========================================================================

    @Test
    void onOpen_doesNotFireAtConstructionTime() {
        AtomicBoolean fired = new AtomicBoolean(false);
        Sheet sheet = new Sheet();
        sheet.setOnOpen(() -> fired.set(true));
        assertFalse(fired.get(), "onOpen must not fire at construction time");
    }

    @Test
    void onClose_firesViaHistory() {
        AtomicBoolean fired = new AtomicBoolean(false);
        Sheet sheet = new Sheet();
        sheet.setOnClose(() -> fired.set(true));
        sheet.addClassName("sheet--open");

        sheet.closeFromHistory();

        assertTrue(fired.get());
    }

    // =========================================================================
    // detach()
    // =========================================================================

    @Test
    void detach_whenNotAttached_doesNotThrow() {
        Sheet sheet = new Sheet();
        assertDoesNotThrow(sheet::detach);
    }

    // =========================================================================
    // SheetTitle — setLocalizableText update
    // =========================================================================

    @Test
    void sheetTitle_setLocalizableText_updatesExistingTitle() {
        SheetTitle title = new SheetTitle("initial");
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        LocalizationTestUtils.withTestLocalizationContext(() -> title.setLocalizableText(loc));
        assertEquals("TestUS", title.getText());
    }

    // =========================================================================
    // SheetDescription — component constructor
    // =========================================================================

    @Test
    void sheetDescription_components_addsChildren() {
        SheetDescription d = new SheetDescription(new Span("note"), new Span("hint"));
        assertEquals(2, d.getComponentCount());
        assertTrue(d.getClassNames().contains("sheet__description"));
    }

    @Test
    void sheetDescription_localizable_constructor_resolvesText() {
        Localizable loc = Localizable.builder().message("Fallback").messageCode("test.code").build();
        SheetDescription[] holder = new SheetDescription[1];
        LocalizationTestUtils.withTestLocalizationContext(() -> holder[0] = new SheetDescription(loc));
        assertEquals("TestUS", holder[0].getText());
    }

    // =========================================================================
    // SheetBuilder — fullscreenOnMobile, backButton, closeButton, lazyContent
    // =========================================================================

    @Test
    void sheetBuilder_fullscreenOnMobile_true_appliesModifier() {
        Sheet sheet = SheetBuilder.create()
                .fullscreenOnMobile(true)
                .build();
        assertTrue(sheet.isFullscreenOnMobile());
        assertTrue(sheet.getClassNames().contains("sheet--fullscreen-mobile"));
    }

    @Test
    void sheetBuilder_backButton_false_hidesButton() {
        Sheet sheet = SheetBuilder.create()
                .backButton(false)
                .build();
        assertFalse(sheet.isShowBackButton());
    }

    @Test
    void sheetBuilder_closeButton_false_hidesButton() {
        Sheet sheet = SheetBuilder.create()
                .closeButton(false)
                .build();
        assertFalse(sheet.isShowCloseButton());
    }

    @Test
    void sheetBuilder_lazyContent_invokedOnFirstOpen() {
        AtomicInteger callCount = new AtomicInteger(0);
        Sheet sheet = SheetBuilder.create()
                .lazyContent(() -> {
                    callCount.incrementAndGet();
                    return new com.vaadin.flow.component.Component[]{ new Div() };
                })
                .build();

        sheet.open();

        assertEquals(1, callCount.get(), "lazyContent supplier must be called exactly once on first open");
    }

    // =========================================================================
    // SheetConfigurator — fullscreenOnMobile, backButton, closeButton
    // =========================================================================

    @Test
    void sheetConfigurator_fullscreenOnMobile_true() {
        Sheet sheet = new Sheet();
        SheetConfigurator.configure(sheet).fullscreenOnMobile(true);
        assertTrue(sheet.isFullscreenOnMobile());
        assertTrue(sheet.getClassNames().contains("sheet--fullscreen-mobile"));
    }

    @Test
    void sheetConfigurator_backButton_false() {
        Sheet sheet = new Sheet();
        SheetConfigurator.configure(sheet).backButton(false);
        assertFalse(sheet.isShowBackButton());
    }

    @Test
    void sheetConfigurator_closeButton_false() {
        Sheet sheet = new Sheet();
        SheetConfigurator.configure(sheet).closeButton(false);
        assertFalse(sheet.isShowCloseButton());
    }
}

