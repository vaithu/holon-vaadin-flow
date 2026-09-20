package com.iyensoft.vaadin.flow.utils;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.css.CSSUtility;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.iyensoft.vaadin.flow.components.KeyValuePair;
import com.iyensoft.vaadin.flow.components.KeyValuePairs;
import com.iyensoft.vaadin.flow.components.Layout;
import com.iyensoft.vaadin.flow.components.Separator;

/**
 * UI helpers that operate on components owned by this library, kept separate
 * from {@link UIUtils} so that the inherited Holon packages never depend on
 * them.
 */
public final class LayoutUtils {

    private LayoutUtils() {
    }

    public static void clearContainer(Layout container) {
        container.removeAll();
    }

    public static void handleNoValuesFound(Layout container) {
        Components.configure(container)
                .fullSize()
                .add(UIUtils.createImage("no-values-found.png", "No values found"));
    }

    public static void handleNoRecordsFound(Layout container) {
        Components.configure(container)
                .fullSize()
                .add(UIUtils.createNoRecordsFoundImage());
    }

    public static Separator separator(SeparatorColor color) {
        return Separator.builder()
                .orientation(Separator.Orientation.VERTICAL)
                .decorative(true)
                .styleNames(color.getClassName(), CSSUtility.Bootstrap.D_NONE, CSSUtility.Bootstrap.D_SM_FLEX)
                .build();
    }

    public static void removeAll(Layout layout) {
        if (layout != null) {
            layout.removeAll();
        }
    }

    public static KeyValuePairs createKeyValuePairs(PropertyBox propertyBox) {
        KeyValuePairs keyValuePairs = new KeyValuePairs();
        propertyBox.forEach(property -> keyValuePairs.add(new KeyValuePair(
                property.getMessage() != null ? property.getMessage() : property.getName(),
                String.valueOf(propertyBox.getValue(property)))));
        return keyValuePairs;
    }
}
