package com.holonplatform.vaadin.flow.internal.lumo;

import com.vaadin.flow.theme.lumo.LumoUtility;

public enum RowGap {
        PIXEL("gap-y-px"),
        XSMALL(LumoUtility.Gap.Row.XSMALL),
        SMALL(LumoUtility.Gap.Row.SMALL),
        MEDIUM(LumoUtility.Gap.Row.MEDIUM),
        LARGE(LumoUtility.Gap.Row.LARGE),
        XLARGE(LumoUtility.Gap.Row.XLARGE);

        private final String className;

        private RowGap(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }
    }