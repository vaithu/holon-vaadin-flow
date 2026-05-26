package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.AvatarBuilder;
import com.holonplatform.vaadin.flow.components.builders.AvatarColor;
import com.holonplatform.vaadin.flow.components.builders.AvatarConfigurator;
import com.holonplatform.vaadin.flow.components.builders.AvatarGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.AvatarGroupConfigurator;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.avatar.AvatarVariant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AvatarBuilder}, {@link AvatarGroupBuilder}, {@link AvatarColor}, and
 * {@link AvatarConfigurator} / {@link AvatarGroupConfigurator} infrastructure.
 *
 * <p>Tests run without a Vaadin UI/Session - only DOM-level assertions via
 * {@code component.getClassNames()}, {@code component.getName()}, etc.</p>
 */
class TestAvatarBuilder {

    // =========================================================================
    // AvatarColor enum
    // =========================================================================

    @Test
    void avatarColor_indices_areZeroToSix() {
        int[] expected = {0, 1, 2, 3, 4, 5, 6};
        AvatarColor[] values = AvatarColor.values();
        assertEquals(7, values.length);
        for (int i = 0; i < values.length; i++) {
            assertEquals(expected[i], values[i].getIndex());
        }
    }

    @Test
    void avatarColor_forId_returnsStableColor() {
        assertEquals(AvatarColor.BLUE,    AvatarColor.forId(0L));
        assertEquals(AvatarColor.GREEN,   AvatarColor.forId(1L));
        assertEquals(AvatarColor.PINK,    AvatarColor.forId(2L));
        assertEquals(AvatarColor.ORANGE,  AvatarColor.forId(3L));
        assertEquals(AvatarColor.VIOLET,  AvatarColor.forId(4L));
        assertEquals(AvatarColor.INDIGO,  AvatarColor.forId(5L));
        assertEquals(AvatarColor.RED,     AvatarColor.forId(6L));
        // Wraps around
        assertEquals(AvatarColor.BLUE,    AvatarColor.forId(7L));
    }

    @Test
    void avatarColor_forId_handlesNegativeIds() {
        // Math.abs(-1) % 7 = 1 => GREEN
        assertNotNull(AvatarColor.forId(-1L));
        assertNotNull(AvatarColor.forId(-42L));
    }

    @Test
    void avatarColor_forId_handleMaxLong() {
        assertNotNull(AvatarColor.forId(Long.MAX_VALUE));
    }

    // =========================================================================
    // AvatarBuilder – anonymous
    // =========================================================================

    @Test
    void builder_create_anonymousAvatar_hasNoName() {
        Avatar avatar = AvatarBuilder.create().build();
        assertNotNull(avatar);
        assertNull(avatar.getName());
    }

    // =========================================================================
    // AvatarBuilder – named
    // =========================================================================

    @Test
    void builder_create_namedAvatar_setsName() {
        Avatar avatar = AvatarBuilder.create("Jane Smith").build();
        assertEquals("Jane Smith", avatar.getName());
    }

    @Test
    void builder_name_overridesInitialName() {
        Avatar avatar = AvatarBuilder.create("Jane").name("Alice").build();
        assertEquals("Alice", avatar.getName());
    }

    @Test
    void builder_name_null_clearsName() {
        Avatar avatar = AvatarBuilder.create("Jane").name((String) null).build();
        assertNull(avatar.getName());
    }

    // =========================================================================
    // AvatarBuilder – abbreviation
    // =========================================================================

    @Test
    void builder_abbreviation_setsAbbreviationOverride() {
        Avatar avatar = AvatarBuilder.create("Augusta Ada King").abbreviation("AK").build();
        assertEquals("AK", avatar.getAbbreviation());
    }

    @Test
    void builder_abbreviation_null_clearsAbbreviation() {
        Avatar avatar = AvatarBuilder.create("Test").abbreviation("T").abbreviation((String) null).build();
        assertNull(avatar.getAbbreviation());
    }

    // =========================================================================
    // AvatarBuilder – image URL
    // =========================================================================

    @Test
    void builder_image_setsImageUrl() {
        Avatar avatar = AvatarBuilder.create("Alice")
                .image("https://example.com/alice.jpg").build();
        assertEquals("https://example.com/alice.jpg", avatar.getImage());
    }

    @Test
    void builder_createWithImageUrl_setsNameAndImage() {
        Avatar avatar = AvatarBuilder.create("Bob", "https://example.com/bob.jpg").build();
        assertEquals("Bob", avatar.getName());
        assertEquals("https://example.com/bob.jpg", avatar.getImage());
    }

    // =========================================================================
    // AvatarBuilder – color index
    // =========================================================================

    @Test
    void builder_colorIndex_rawInt_setsIndex() {
        Avatar avatar = AvatarBuilder.create("Alice").colorIndex(3).build();
        assertEquals(Integer.valueOf(3), avatar.getColorIndex());
    }

    @Test
    void builder_colorIndex_enum_setsCorrectIndex() {
        Avatar avatar = AvatarBuilder.create("Alice").colorIndex(AvatarColor.VIOLET).build();
        assertEquals(Integer.valueOf(AvatarColor.VIOLET.getIndex()), avatar.getColorIndex());
    }

    @Test
    void builder_colorIndex_allEnumValues_setCorrectly() {
        for (AvatarColor color : AvatarColor.values()) {
            Avatar avatar = AvatarBuilder.create("Test").colorIndex(color).build();
            assertEquals(Integer.valueOf(color.getIndex()), avatar.getColorIndex());
        }
    }

    // =========================================================================
    // AvatarBuilder – variant (IconBadge-style background)
    // =========================================================================

    @Test
    void builder_variant_success_addsBadgeClassNames() {
        Avatar avatar = AvatarBuilder.create("OK").variant(Alert.Variant.SUCCESS).build();
        assertTrue(avatar.getClassNames().contains("avatar--badge"));
        assertTrue(avatar.getClassNames().contains("avatar--badge-success"));
    }

    @Test
    void builder_variant_destructive_addsBadgeDestructiveClass() {
        Avatar avatar = AvatarBuilder.create("Err").variant(Alert.Variant.DESTRUCTIVE).build();
        assertTrue(avatar.getClassNames().contains("avatar--badge"));
        assertTrue(avatar.getClassNames().contains("avatar--badge-destructive"));
    }

    @Test
    void builder_variant_warning_addsBadgeWarningClass() {
        Avatar avatar = AvatarBuilder.create("Warn").variant(Alert.Variant.WARNING).build();
        assertTrue(avatar.getClassNames().contains("avatar--badge-warning"));
    }

    @Test
    void builder_variant_info_addsBadgeInfoClass() {
        Avatar avatar = AvatarBuilder.create("Info").variant(Alert.Variant.INFO).build();
        assertTrue(avatar.getClassNames().contains("avatar--badge-info"));
    }

    @Test
    void builder_variant_default_addsBadgeDefaultClass() {
        Avatar avatar = AvatarBuilder.create("Def").variant(Alert.Variant.DEFAULT).build();
        assertTrue(avatar.getClassNames().contains("avatar--badge-default"));
    }

    // =========================================================================
    // AvatarBuilder – theme variants
    // =========================================================================

    @Test
    void builder_withThemeVariants_applyVariant() {
        Avatar avatar = AvatarBuilder.create("XS")
                .withThemeVariants(AvatarVariant.LUMO_XSMALL).build();
        // Vaadin AvatarVariant applies theme attribute; the Avatar should carry the part
        var themes = avatar.getElement().getThemeList();
        assertTrue(themes.contains("xsmall"));
    }

    @Test
    void builder_withThemeVariants_large() {
        Avatar avatar = AvatarBuilder.create("LG")
                .withThemeVariants(AvatarVariant.LUMO_LARGE).build();
        assertTrue(avatar.getElement().getThemeList().contains("large"));
    }

    // =========================================================================
    // AvatarBuilder – aria-label / aria-labelledby
    // =========================================================================

    @Test
    void builder_ariaLabel_setsAttribute() {
        Avatar avatar = AvatarBuilder.create("Dave")
                .ariaLabel("Dave's profile picture").build();
        assertEquals("Dave's profile picture",
                avatar.getElement().getAttribute("aria-label"));
    }

    @Test
    void builder_ariaLabelledBy_setsAttribute() {
        Avatar avatar = AvatarBuilder.create("Eve")
                .ariaLabelledBy("name-span-id").build();
        assertEquals("name-span-id",
                avatar.getElement().getAttribute("aria-labelledby"));
    }

    // =========================================================================
    // AvatarBuilder – Localizable name (i18n, non-deferred)
    // =========================================================================

    @Test
    void builder_localizableName_resolvesWithLocalizationContext() {
        Avatar avatar = LocalizationTestUtils.withTestLocalizationContext(() ->
                AvatarBuilder.create()
                        .name(Localizable.builder()
                                .message("Test Default")
                                .messageCode("test.code")
                                .build())
                        .build());
        // LocalizationTestUtils resolves "test.code" → "TestUS" in Locale.US
        assertEquals("TestUS", avatar.getName());
    }

    @Test
    void builder_localizableName_fallsBackToDefault_whenNoContext() {
        Avatar avatar = AvatarBuilder.create()
                .name(Localizable.builder()
                        .message("Fallback Name")
                        .messageCode("unknown.code")
                        .build())
                .build();
        assertEquals("Fallback Name", avatar.getName());
    }

    @Test
    void builder_nameShorthand_buildsFluently() {
        Avatar avatar = AvatarBuilder.create()
                .name("Bob Evans", "user.bob.fullname")
                .build();
        // No localization context → falls back to default
        assertEquals("Bob Evans", avatar.getName());
    }

    // =========================================================================
    // AvatarBuilder – Localizable abbreviation (i18n, non-deferred)
    // =========================================================================

    @Test
    void builder_localizableAbbreviation_fallsBackToDefault() {
        Avatar avatar = AvatarBuilder.create("Alice")
                .abbreviation("AL", "user.alice.abbrev")
                .build();
        assertEquals("AL", avatar.getAbbreviation());
    }

    // =========================================================================
    // AvatarBuilder – Localizable aria-label (i18n)
    // =========================================================================

    @Test
    void builder_localizableAriaLabel_resolvesWithLocalizationContext() {
        Avatar avatar = LocalizationTestUtils.withTestLocalizationContext(() ->
                AvatarBuilder.create("Test")
                        .ariaLabel(Localizable.builder()
                                .message("Test Default Label")
                                .messageCode("test.code")
                                .build())
                        .build());
        assertEquals("TestUS", avatar.getElement().getAttribute("aria-label"));
    }

    // =========================================================================
    // AvatarConfigurator.configure – configure existing Avatar
    // =========================================================================

    @Test
    void configurator_configure_existingAvatar() {
        Avatar existing = new Avatar("Alice");
        AvatarConfigurator.configure(existing).colorIndex(AvatarColor.GREEN);
        assertEquals(Integer.valueOf(AvatarColor.GREEN.getIndex()), existing.getColorIndex());
    }

    // =========================================================================
    // AvatarGroupBuilder
    // =========================================================================

    @Test
    void groupBuilder_create_returnsEmptyGroup() {
        AvatarGroup group = AvatarGroupBuilder.create().build();
        assertNotNull(group);
    }

    @Test
    void groupBuilder_add_singleItem() {
        AvatarGroup group = AvatarGroupBuilder.create()
                .add(new AvatarGroup.AvatarGroupItem("Alice"))
                .build();
        assertNotNull(group);
        assertEquals(1, group.getItems().size());
        assertEquals("Alice", group.getItems().getFirst().getName());
    }

    @Test
    void groupBuilder_add_multipleItems() {
        AvatarGroup group = AvatarGroupBuilder.create()
                .add(
                    new AvatarGroup.AvatarGroupItem("Alice"),
                    new AvatarGroup.AvatarGroupItem("Bob"),
                    new AvatarGroup.AvatarGroupItem("Carol")
                )
                .build();
        assertEquals(3, group.getItems().size());
    }

    @Test
    void groupBuilder_maxItemsVisible_setsLimit() {
        AvatarGroup group = AvatarGroupBuilder.create()
                .maxItemsVisible(3)
                .add(
                    new AvatarGroup.AvatarGroupItem("A"),
                    new AvatarGroup.AvatarGroupItem("B"),
                    new AvatarGroup.AvatarGroupItem("C"),
                    new AvatarGroup.AvatarGroupItem("D"),
                    new AvatarGroup.AvatarGroupItem("E")
                )
                .build();
        assertEquals(Integer.valueOf(3), group.getMaxItemsVisible());
        assertEquals(5, group.getItems().size()); // all items still added
    }

    @Test
    void groupBuilder_createWithItems_staticFactory() {
        var item1 = new AvatarGroup.AvatarGroupItem("Alice");
        var item2 = new AvatarGroup.AvatarGroupItem("Bob");
        AvatarGroup group = AvatarGroupBuilder.create(item1, item2).build();
        assertEquals(2, group.getItems().size());
    }

    @Test
    void groupBuilder_createEmpty_staticFactory_hasNoItems() {
        AvatarGroup group = AvatarGroupBuilder.create().build();
        assertEquals(0, group.getItems().size());
    }

    // =========================================================================
    // AvatarGroupConfigurator.configure – configure existing AvatarGroup
    // =========================================================================

    @Test
    void groupConfigurator_configure_existingGroup() {
        AvatarGroup existing = new AvatarGroup();
        AvatarGroupConfigurator.configure(existing)
                .add(new AvatarGroup.AvatarGroupItem("Frank"))
                .maxItemsVisible(2);
        assertEquals(1, existing.getItems().size());
        assertEquals(Integer.valueOf(2), existing.getMaxItemsVisible());
    }
}


