package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.iyensoft.vaadin.flow.internal.components.builders.DefaultAppShellLayoutBuilder;

class TestAppShellLayoutBuilder {

    @Test
    void menuConsumer_collectsItemsInOrder() throws Exception {
        var sut = new DefaultAppShellLayoutBuilder();

        sut.user(u -> u
                .name("Jane Smith")
                .menu(m -> m
                        .item("Profile")
                        .item(null)
                        .item("Sign out")));

        assertEquals(List.of("Profile", "Sign out"), menuItemsOf(sut));
    }

    @Test
    void menuConsumer_supportsItemBatching() throws Exception {
        var sut = new DefaultAppShellLayoutBuilder();

        sut.user(u -> u
                .name("Jane Smith")
                .menu(m -> m.items("Profile", null, "Sign out")));

        assertEquals(List.of("Profile", "Sign out"), menuItemsOf(sut));
    }

    @Test
    void menuConsumer_rejectsNullConfigurator() {
        var sut = new DefaultAppShellLayoutBuilder();

        assertThrows(NullPointerException.class, () -> sut.user(u -> u.menu(null)));
    }

    @SuppressWarnings("unchecked")
    private static List<String> menuItemsOf(DefaultAppShellLayoutBuilder sut) throws Exception {
        Class<?> type = sut.getClass();
        NoSuchFieldException lastError = null;
        while (type != null) {
            try {
                Field field = type.getDeclaredField("userMenuItems");
                field.setAccessible(true);
                return (List<String>) field.get(sut);
            } catch (NoSuchFieldException e) {
                lastError = e;
                type = type.getSuperclass();
            }
        }
        throw lastError;
    }
}
