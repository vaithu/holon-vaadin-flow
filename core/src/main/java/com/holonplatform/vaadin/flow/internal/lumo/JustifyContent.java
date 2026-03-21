package com.holonplatform.vaadin.flow.internal.lumo;

import com.vaadin.flow.theme.lumo.LumoUtility;

public enum JustifyContent {
        AROUND(LumoUtility.JustifyContent.AROUND),
        BETWEEN(LumoUtility.JustifyContent.BETWEEN),
        CENTER(LumoUtility.JustifyContent.CENTER),
        END(LumoUtility.JustifyContent.END),
        EVENLY(LumoUtility.JustifyContent.EVENLY),
        START(LumoUtility.JustifyContent.START);

        private final String className;

        private JustifyContent(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }
    }