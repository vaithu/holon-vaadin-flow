package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AccordionBuilder;
import com.holonplatform.vaadin.flow.components.builders.AlertBuilder;
import com.holonplatform.vaadin.flow.components.builders.AlertDialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.AlertModalBuilder;
import com.holonplatform.vaadin.flow.components.builders.AvatarBuilder;
import com.holonplatform.vaadin.flow.components.builders.AvatarGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.BulkItemPickerDialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonGroupBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDialog;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertModal;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkItemPickerDialog;
import com.holonplatform.vaadin.flow.vaadinplus.components.ButtonGroup;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.BreadcrumbBuilder;
import com.iyensoft.vaadin.flow.components.builders.DetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.iyensoft.vaadin.flow.components.builders.MasterBuilder;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.MobileGridColumnBuilder;
import com.iyensoft.vaadin.flow.components.builders.PanelBuilder;
import com.iyensoft.vaadin.flow.components.builders.SideNavBuilder;
import com.iyensoft.vaadin.flow.components.builders.TabsBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.tabs.Tabs;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests {@code withBuildPostProcessor} for all 17 builders updated in this change.
 */
class AllBuildersPostProcessorTest {

    // -----------------------------------------------------------------------
    // AccordionBuilder
    // -----------------------------------------------------------------------

    @Test
    void accordion_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Accordion result = AccordionBuilder.create()
                .withBuildPostProcessor(a -> {
                    called.set(true);
                    a.setId("accordion-id");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.getId()).hasValue("accordion-id");
    }

    @Test
    void accordion_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> AccordionBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // AlertBuilder
    // -----------------------------------------------------------------------

    @Test
    void alert_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Alert result = AlertBuilder.create(Alert.Variant.SUCCESS)
                .withBuildPostProcessor(a -> {
                    called.set(true);
                    a.addClassName("alert-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("alert-custom")).isTrue();
    }

    @Test
    void alert_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> AlertBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // AlertDialogBuilder
    // -----------------------------------------------------------------------

    @Test
    void alertDialog_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        AlertDialog result = AlertDialogBuilder.create()
                .withBuildPostProcessor(d -> {
                    called.set(true);
                    d.setId("alert-dialog-id");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.getId()).hasValue("alert-dialog-id");
    }

    @Test
    void alertDialog_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> AlertDialogBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // AlertModalBuilder
    // -----------------------------------------------------------------------

    @Test
    void alertModal_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        AlertModal result = AlertModalBuilder.create(Alert.Variant.DESTRUCTIVE)
                .withBuildPostProcessor(m -> {
                    called.set(true);
                    m.addClassName("modal-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("modal-custom")).isTrue();
    }

    @Test
    void alertModal_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> AlertModalBuilder.create(Alert.Variant.INFO).withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // AvatarBuilder
    // -----------------------------------------------------------------------

    @Test
    void avatar_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Avatar result = AvatarBuilder.create("John Doe")
                .withBuildPostProcessor(a -> {
                    called.set(true);
                    a.setId("avatar-id");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.getId()).hasValue("avatar-id");
    }

    @Test
    void avatar_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> AvatarBuilder.create("Test").withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // AvatarGroupBuilder
    // -----------------------------------------------------------------------

    @Test
    void avatarGroup_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        AvatarGroup result = AvatarGroupBuilder.create()
                .withBuildPostProcessor(g -> {
                    called.set(true);
                    g.addClassName("group-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("group-custom")).isTrue();
    }

    @Test
    void avatarGroup_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> AvatarGroupBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // BulkItemPickerDialogBuilder
    // -----------------------------------------------------------------------

    @Test
    void bulkItemPickerDialog_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        BulkItemPickerDialog result = BulkItemPickerDialogBuilder.create()
                .withBuildPostProcessor(d -> {
                    called.set(true);
                    d.addClassName("bulk-picker-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("bulk-picker-custom")).isTrue();
    }

    @Test
    void bulkItemPickerDialog_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> BulkItemPickerDialogBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // ButtonGroupBuilder
    // -----------------------------------------------------------------------

    @Test
    void buttonGroup_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        ButtonGroup result = ButtonGroupBuilder.create()
                .withBuildPostProcessor(g -> {
                    called.set(true);
                    g.addClassName("btn-group-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("btn-group-custom")).isTrue();
    }

    @Test
    void buttonGroup_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> ButtonGroupBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // BreadcrumbBuilder
    // -----------------------------------------------------------------------

    @Test
    void breadcrumb_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Breadcrumb result = BreadcrumbBuilder.create()
                .withBuildPostProcessor(b -> {
                    called.set(true);
                    b.addClassName("breadcrumb-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("breadcrumb-custom")).isTrue();
    }

    @Test
    void breadcrumb_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> BreadcrumbBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // PanelBuilder
    // -----------------------------------------------------------------------

    @Test
    void panel_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Panel result = PanelBuilder.create()
                .withBuildPostProcessor(p -> {
                    called.set(true);
                    p.addClassName("panel-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("panel-custom")).isTrue();
    }

    @Test
    void panel_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> PanelBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // SideNavBuilder
    // -----------------------------------------------------------------------

    @Test
    void sideNav_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        SideNav result = SideNavBuilder.create()
                .withBuildPostProcessor(n -> {
                    called.set(true);
                    n.setId("sidenav-id");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.getId()).hasValue("sidenav-id");
    }

    @Test
    void sideNav_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> SideNavBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // TabsBuilder
    // -----------------------------------------------------------------------

    @Test
    void tabs_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Tabs result = TabsBuilder.create()
                .withBuildPostProcessor(t -> {
                    called.set(true);
                    t.addClassName("tabs-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("tabs-custom")).isTrue();
    }

    @Test
    void tabs_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> TabsBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // LazyTabsBuilder
    // -----------------------------------------------------------------------

    @Test
    void lazyTabs_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Tabs result = LazyTabsBuilder.create()
                .withBuildPostProcessor(t -> {
                    called.set(true);
                    t.addClassName("lazy-tabs-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("lazy-tabs-custom")).isTrue();
    }

    @Test
    void lazyTabs_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> LazyTabsBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // MasterDetailBuilder (bean type)
    // -----------------------------------------------------------------------

    @Test
    void masterDetail_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        MasterDetailLayout<String> result = MasterDetailBuilder.create(String.class)
                .withBuildPostProcessor(md -> {
                    called.set(true);
                    md.addClassName("master-detail-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("master-detail-custom")).isTrue();
    }

    @Test
    void masterDetail_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> MasterDetailBuilder.create(String.class).withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // MobileGridColumnBuilder
    // -----------------------------------------------------------------------

    @Test
    void mobileGridColumn_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Layout result = MobileGridColumnBuilder.create()
                .withBuildPostProcessor(l -> {
                    called.set(true);
                    l.addClassName("mobile-grid-custom");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.hasClassName("mobile-grid-custom")).isTrue();
    }

    @Test
    void mobileGridColumn_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> MobileGridColumnBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // DetailBuilder
    // -----------------------------------------------------------------------

    @Test
    void detail_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Div result = DetailBuilder.create()
                .withBuildPostProcessor(d -> {
                    called.set(true);
                    d.setId("detail-id");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.getId()).hasValue("detail-id");
    }

    @Test
    void detail_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> DetailBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // MasterBuilder
    // -----------------------------------------------------------------------

    @Test
    void master_postProcessor_isApplied() {
        AtomicBoolean called = new AtomicBoolean();
        Div result = MasterBuilder.create()
                .withBuildPostProcessor(m -> {
                    called.set(true);
                    m.setId("master-id");
                })
                .build();
        assertThat(called).isTrue();
        assertThat(result.getId()).hasValue("master-id");
    }

    @Test
    void master_nullPostProcessor_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> MasterBuilder.create().withBuildPostProcessor(null));
    }

    // -----------------------------------------------------------------------
    // Shared: multiple post-processors are called in registration order
    // -----------------------------------------------------------------------

    @Test
    void multiplePostProcessors_areAppliedInOrder_onAccordion() {
        List<String> order = new ArrayList<>();
        AccordionBuilder.create()
                .withBuildPostProcessor(_ -> order.add("first"))
                .withBuildPostProcessor(_ -> order.add("second"))
                .withBuildPostProcessor(_ -> order.add("third"))
                .build();
        assertThat(order).containsExactly("first", "second", "third");
    }
}




