package com.holonplatform.vaadin.flow.internal.lumo;

import com.vaadin.flow.theme.lumo.LumoUtility;

public enum FlexWrap {
        NOWRAP(LumoUtility.FlexWrap.NOWRAP),
        WRAP(LumoUtility.FlexWrap.WRAP),
        WRAP_REVERSE(LumoUtility.FlexWrap.WRAP_REVERSE);

        private final String className;

        private FlexWrap(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }
    }