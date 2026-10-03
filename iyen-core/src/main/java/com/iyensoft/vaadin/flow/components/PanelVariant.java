package com.iyensoft.vaadin.flow.components;

/** Visual variants supported by {@link Panel}. */
public final class PanelVariant {

    private PanelVariant() {
    }

    /** Background colors available for a panel. */
    public enum Background {
        BG("bg"),
        SURFACE("surface"),
        SURFACE_2("surface-2"),
        PRIMARY("primary"),
        PRIMARY_SOFT("primary-soft"),
        PRIMARY_2("primary-2"),
        SUCCESS("success"),
        SUCCESS_SOFT("success-soft"),
        WARN("warn"),
        WARN_SOFT("warn-soft"),
        DANGER("danger"),
        DANGER_SOFT("danger-soft"),
        VIOLET("violet"),
        VIOLET_SOFT("violet-soft"),
        TEAL("teal"),
        TEAL_SOFT("teal-soft"),
        GOLD("gold"),
        GOLD_SOFT("gold-soft");

        private final String className;

        Background(String className) {
            this.className = "iyen-panel--bg-" + className;
        }

        public String getClassName() {
            return className;
        }
    }
}
