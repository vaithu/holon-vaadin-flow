package com.holonplatform.vaadin.flow.internal.lumo;

import com.vaadin.flow.theme.lumo.LumoUtility;

public enum Gap {
        PIXEL("gap-px"),
        XSMALL(LumoUtility.Gap.XSMALL),
        SMALL(LumoUtility.Gap.SMALL),
        MEDIUM(LumoUtility.Gap.MEDIUM),
        LARGE(LumoUtility.Gap.LARGE),
        XLARGE(LumoUtility.Gap.XLARGE);

        private final String className;

        private Gap(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }

        public ColumnGap getColumnGap() {
            return ColumnGap.valueOf(this.name());
        }

        public RowGap getRowGap() {
            return RowGap.valueOf(this.name());
        }
    }