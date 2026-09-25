package com.iyensoft.vaadin.flow.enums;

/**
     * Predefined branded color variants, matching a set of common Material-3
     * "colored app bar" palettes. Each recolors the bar's background, on-color
     * text/icon tokens, and (except {@link #GRADIENT}) is safe to combine with
     * any {@link Variant}. Backed by {@code material-app-bar.css}.
     */
    public enum MaterialAppBarColor {
        /** Default neutral surface (no override). */
        NEUTRAL("neutral"),
        INDIGO("indigo"),
        TEAL("teal"),
        EMERALD("emerald"),
        ORANGE("orange"),
        ROSE("rose"),
        PURPLE("purple"),
        SLATE("slate"),
        /** Warm amber — matches {@code ShellColor.AMBER} for AppShellLayout/SideNav parity. */
        AMBER("amber"),
        /** Expressive brand gradient. */
        GRADIENT("gradient");

        private final String className;

        MaterialAppBarColor(String className) {
            this.className = className;
        }

      public   String getClassName() {
            return className;
        }
    }
