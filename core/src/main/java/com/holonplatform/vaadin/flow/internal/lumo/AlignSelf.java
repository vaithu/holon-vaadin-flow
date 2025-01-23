package com.holonplatform.vaadin.flow.internal.lumo;

import com.vaadin.flow.theme.lumo.LumoUtility;

public enum AlignSelf {
        AUTO(LumoUtility.AlignSelf.AUTO),
        BASELINE(LumoUtility.AlignSelf.BASELINE),
        CENTER(LumoUtility.AlignSelf.CENTER),
        END(LumoUtility.AlignSelf.END),
        START(LumoUtility.AlignSelf.START),
        STRETCH(LumoUtility.AlignSelf.STRETCH);

        private final String className;

        private AlignSelf(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }
    }