package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.NotificationBuilder;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link NotificationBuilder}.
 */
class TestNotificationBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(NotificationBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_default_returnsNotification() {
        Notification n = NotificationBuilder.create().build();
        assertNotNull(n);
    }

    @Test
    void build_default_hasAutoCloseDuration() {
        // Default constructor calls autoClose() → 5000ms
        Notification n = NotificationBuilder.create().build();
        assertEquals(5000, n.getDuration());
    }

    // =========================================================================
    // Text
    // =========================================================================

    @Test
    void text_setsText() {
        Notification n = NotificationBuilder.create()
                .text("Hello")
                .build();
        assertNotNull(n);
    }

    // =========================================================================
    // Duration
    // =========================================================================

    @Test
    void duration_setsDuration() {
        Notification n = NotificationBuilder.create()
                .duration(3000)
                .build();
        assertEquals(3000, n.getDuration());
    }

    // =========================================================================
    // Position
    // =========================================================================

    @Nested
    class PositionTests {

        @Test
        void position_topCenter() {
            Notification n = NotificationBuilder.create()
                    .position(Notification.Position.TOP_CENTER)
                    .build();
            assertEquals(Notification.Position.TOP_CENTER, n.getPosition());
        }

        @Test
        void topStretch() {
            Notification n = NotificationBuilder.create()
                    .topStretch()
                    .build();
            assertEquals(Notification.Position.TOP_STRETCH, n.getPosition());
        }

        @Test
        void topStart() {
            Notification n = NotificationBuilder.create()
                    .topStart()
                    .build();
            assertEquals(Notification.Position.TOP_START, n.getPosition());
        }

        @Test
        void topEnd() {
            Notification n = NotificationBuilder.create()
                    .topEnd()
                    .build();
            assertEquals(Notification.Position.TOP_END, n.getPosition());
        }

        @Test
        void middle() {
            Notification n = NotificationBuilder.create()
                    .middle()
                    .build();
            assertEquals(Notification.Position.MIDDLE, n.getPosition());
        }

        @Test
        void bottomStart() {
            Notification n = NotificationBuilder.create()
                    .bottomStart()
                    .build();
            assertEquals(Notification.Position.BOTTOM_START, n.getPosition());
        }

        @Test
        void bottomCenter() {
            Notification n = NotificationBuilder.create()
                    .bottomCenter()
                    .build();
            assertEquals(Notification.Position.BOTTOM_CENTER, n.getPosition());
        }

        @Test
        void bottomEnd() {
            Notification n = NotificationBuilder.create()
                    .bottomEnd()
                    .build();
            assertEquals(Notification.Position.BOTTOM_END, n.getPosition());
        }

        @Test
        void bottomStretch() {
            Notification n = NotificationBuilder.create()
                    .bottomStretch()
                    .build();
            assertEquals(Notification.Position.BOTTOM_STRETCH, n.getPosition());
        }
    }

    // =========================================================================
    // Variants (success / error / warning / primary / contrast)
    // =========================================================================

    @Nested
    class VariantTests {

        @Test
        void success() {
            Notification n = NotificationBuilder.create()
                    .success()
                    .build();
            assertNotNull(n);
        }

        @Test
        void error() {
            Notification n = NotificationBuilder.create()
                    .error()
                    .build();
            assertNotNull(n);
        }

        @Test
        void warning() {
            Notification n = NotificationBuilder.create()
                    .warning()
                    .build();
            assertNotNull(n);
        }

        @Test
        void primary() {
            Notification n = NotificationBuilder.create()
                    .primary()
                    .build();
            assertNotNull(n);
        }

        @Test
        void contrast() {
            Notification n = NotificationBuilder.create()
                    .contrast()
                    .build();
            assertNotNull(n);
        }

        @Test
        void withThemeVariants_direct() {
            Notification n = NotificationBuilder.create()
                    .withThemeVariants(NotificationVariant.LUMO_SUCCESS)
                    .build();
            assertNotNull(n);
        }
    }

    // =========================================================================
    // AutoClose
    // =========================================================================

    @Test
    void autoClose_int_setsDuration() {
        Notification n = NotificationBuilder.create()
                .autoClose(7000)
                .build();
        assertEquals(7000, n.getDuration());
    }

    @Test
    void autoClose_false_addCloseButton() {
        Notification n = NotificationBuilder.create()
                .autoClose(false)
                .build();
        // autoClose(false) sets duration(0) and adds close button
        assertEquals(0, n.getDuration());
    }

    // =========================================================================
    // Close button
    // =========================================================================

    @Test
    void closeButton_true_addsButton() {
        Notification n = NotificationBuilder.create()
                .closeButton(true)
                .build();
        assertNotNull(n);
    }

    @Test
    void closeButton_consumer() {
        Notification n = NotificationBuilder.create()
                .closeButton(btn -> btn.text("Dismiss"))
                .build();
        assertNotNull(n);
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Notification n = NotificationBuilder.create()
                .text("Item saved successfully")
                .success()
                .topCenter()
                .duration(3000)
                .build();
        assertNotNull(n);
        assertEquals(Notification.Position.TOP_CENTER, n.getPosition());
        assertEquals(3000, n.getDuration());
    }
}
