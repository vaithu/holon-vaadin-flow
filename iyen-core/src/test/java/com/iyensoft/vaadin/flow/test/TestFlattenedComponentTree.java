package com.iyensoft.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.AssignmentPickerRow;
import com.iyensoft.vaadin.flow.components.EntityCreationForm;
import com.iyensoft.vaadin.flow.components.HeroStrip;
import com.iyensoft.vaadin.flow.components.LivePreviewCard;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the "flatten the component tree" invariants.
 *
 * <p>Each wrapper element removed here was one server-side {@code Component} plus one
 * state node plus one DOM node the browser had to lay out — multiplied by every
 * instance of the component. These tests fail if a redundant wrapper is reintroduced.</p>
 */
class TestFlattenedComponentTree {

    // ── AssignmentPickerRow: avatar carries its initials directly ──────────

    @Test
    void avatar_holdsInitialsAsItsOwnText_withoutAnInnerSpan() {
        AssignmentPickerRow row = new AssignmentPickerRow("Ada Lovelace", "Engineer");

        Div avatar = avatarOf(row);

        assertEquals("AL", avatar.getText());
        assertEquals(0, avatar.getChildren().count(),
                "the avatar must render its initials as text, not wrap them in a child element");
    }

    @Test
    void setName_updatesTheDerivedInitialsOnTheAvatarItself() {
        AssignmentPickerRow row = new AssignmentPickerRow("Ada Lovelace", null);

        row.setName("Grace Hopper");

        assertEquals("GH", avatarOf(row).getText());
    }

    @Test
    void setInitials_overridesTheDerivedInitials_andNullRestoresThem() {
        AssignmentPickerRow row = new AssignmentPickerRow("Ada Lovelace", null);

        row.setInitials("ZZ");
        assertEquals("ZZ", avatarOf(row).getText());

        row.setInitials(null);
        assertEquals("AL", avatarOf(row).getText());
    }

    @Test
    void setColorIndex_swapsTheColourModifierClass() {
        AssignmentPickerRow row = new AssignmentPickerRow("Ada Lovelace", null);
        Div avatar = avatarOf(row);

        assertTrue(avatar.getClassNames().contains("apr__avatar--color-0"));

        row.setColorIndex(3);

        assertFalse(avatar.getClassNames().contains("apr__avatar--color-0"),
                "the previous colour modifier must be removed");
        assertTrue(avatar.getClassNames().contains("apr__avatar--color-3"));
    }

    @Test
    void setColorIndex_wrapsOutOfRangeAndNegativeIndexes() {
        AssignmentPickerRow row = new AssignmentPickerRow("Ada Lovelace", null);
        Div avatar = avatarOf(row);

        row.setColorIndex(9); // 9 % 8 == 1
        assertTrue(avatar.getClassNames().contains("apr__avatar--color-1"));

        row.setColorIndex(-3); // abs(-3) % 8 == 3
        assertTrue(avatar.getClassNames().contains("apr__avatar--color-3"));
        assertFalse(avatar.getClassNames().contains("apr__avatar--color-1"));
    }

    // ── EntityCreationForm: main column sits directly on the root ──────────

    @Test
    void mainColumn_isADirectChildOfTheRoot_withNoBodyWrapper() {
        EntityCreationForm form = new EntityCreationForm();

        assertTrue(childrenOf(form).stream()
                        .anyMatch(c -> c.getElement().getClassList().contains("ecf__main")),
                ".ecf__main must be a direct child of the form root");

        assertFalse(descendantsOf(form).stream()
                        .anyMatch(c -> c.getElement().getClassList().contains("ecf__body")),
                "the .ecf__body wrapper existed only to add padding and must stay removed");
    }

    @Test
    void mainColumn_stillReceivesSteps() {
        EntityCreationForm form = new EntityCreationForm();

        Component mainCol = childrenOf(form).stream()
                .filter(c -> c.getElement().getClassList().contains("ecf__main"))
                .findFirst()
                .orElseThrow();

        assertEquals(0, mainCol.getChildren().count());
    }

    // ── HeroStrip: name / label carry their text directly ──────────────────

    @Test
    void heroStripName_holdsTheNameAsText_withNoWrapperSpan() {
        HeroStrip strip = new HeroStrip(HeroStrip.Variant.DEFAULT);
        strip.setHeader(new HeroStrip.Header(null, null, "Acme Corp", false, null));

        Component name = byClass(strip, "hstrip__name");

        assertEquals("Acme Corp", name.getElement().getText());
        assertEquals(0, name.getChildren().count(),
                "the name must be text on .hstrip__name, not a class-less inner Span");
    }

    @Test
    void heroStripName_keepsTheStar_afterTheNameText() {
        HeroStrip strip = new HeroStrip(HeroStrip.Variant.DEFAULT);
        strip.setHeader(new HeroStrip.Header(null, null, "Acme Corp", true, null));

        Component name = byClass(strip, "hstrip__name");

        assertEquals("Acme Corp", name.getElement().getText(),
                "setText() must run before add(), otherwise the star would be wiped");
        assertEquals(1, name.getChildren().count());
        assertEquals("★", name.getChildren().findFirst().orElseThrow().getElement().getText());
    }

    @Test
    void heroStripLabel_putsThePulseDotBeforeTheLabelText() {
        HeroStrip strip = new HeroStrip(HeroStrip.Variant.DEFAULT);
        strip.setCells(java.util.List.of(
                new HeroStrip.Cell("Open AR", "10", "1 overdue", true, HeroStrip.ValueVariant.OK)));

        Component label = byClass(strip, "hstrip__label");

        assertEquals("Open AR", label.getElement().getText());
        assertEquals(1, label.getChildren().count(), "only the decorative pulse dot may be a child");
        assertTrue(label.getChildren().findFirst().orElseThrow()
                        .getElement().getClassList().contains("hstrip__pulse"),
                "the pulse dot must be prepended, so it still renders before the label text");
    }

    @Test
    void heroStripLabel_withoutPulse_hasNoChildElementsAtAll() {
        HeroStrip strip = new HeroStrip(HeroStrip.Variant.DEFAULT);
        strip.setCells(java.util.List.of(
                new HeroStrip.Cell("Open AR", "10", "1 overdue", false, HeroStrip.ValueVariant.OK)));

        Component label = byClass(strip, "hstrip__label");

        assertEquals("Open AR", label.getElement().getText());
        assertEquals(0, label.getChildren().count());
    }

    // ── LivePreviewCard: title carries its text directly ───────────────────

    @Test
    void livePreviewCardHeader_holdsTextDirectly() {
        LivePreviewCard card = new LivePreviewCard("Preview");

        card.setHeader("Acme Corp");

        Component title = byClass(card, "lpc__title");
        assertEquals("Acme Corp", title.getElement().getText());
        assertEquals(0, title.getChildren().count());
    }

    @Test
    void livePreviewCardHeader_isReplacedByTheAccentTitle_leavingNoStaleText() {
        LivePreviewCard card = new LivePreviewCard("Preview");
        card.setHeader("Stale value");

        card.setTitle("Acme", " Corp", " GmbH");

        Component title = byClass(card, "lpc__title");
        assertEquals(3, title.getChildren().count());
        assertEquals("", title.getElement().getText(),
                "removeAll() must also drop the text node set by setHeader()");
    }

    @Test
    void livePreviewCardAccentTitle_isReplacedByAPlainHeader() {
        LivePreviewCard card = new LivePreviewCard("Preview");
        card.setTitle("Acme", " Corp", " GmbH");

        card.setHeader("Plain");

        Component title = byClass(card, "lpc__title");
        assertEquals("Plain", title.getElement().getText());
        assertEquals(0, title.getChildren().count(),
                "setText() must clear the previously added spans");
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private static Component byClass(Component root, String className) {
        return descendantsOf(root).stream()
                .filter(c -> c.getElement().getClassList().contains(className))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no ." + className + " element found"));
    }

    private static Div avatarOf(AssignmentPickerRow row) {
        return (Div) childrenOf(row).stream()
                .filter(c -> c.getElement().getClassList().contains("apr__avatar"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no .apr__avatar child found"));
    }

    private static java.util.List<Component> childrenOf(Component component) {
        return component.getChildren().toList();
    }

    private static java.util.List<Component> descendantsOf(Component component) {
        var all = new java.util.ArrayList<Component>();
        component.getChildren().forEach(child -> {
            all.add(child);
            all.addAll(descendantsOf(child));
        });
        return all;
    }
}



