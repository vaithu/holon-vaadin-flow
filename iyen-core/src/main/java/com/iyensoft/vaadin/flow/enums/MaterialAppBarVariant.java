package com.iyensoft.vaadin.flow.enums;

/** Material 3 app bar variants. */
    public enum MaterialAppBarVariant {
        SEARCH("search"),
        SMALL("small"),
        MEDIUM_FLEXIBLE("medium-flexible"),
        LARGE_FLEXIBLE("large-flexible");

        private final String className;

        MaterialAppBarVariant(String className) {
            this.className = className;
        }

       public String getClassName() {
            return className;
        }
    }
