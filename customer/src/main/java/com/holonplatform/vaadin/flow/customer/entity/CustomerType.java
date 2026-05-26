package com.holonplatform.vaadin.flow.customer.entity;

/**
 * Classifies the customer as an individual person or an organisational entity.
 * Maps to the i18n key prefix {@code customer.type.*}.
 */
public enum CustomerType {

    /** A private individual or sole trader. */
    INDIVIDUAL,

    /** A registered company or corporation. */
    COMPANY,

    /** A government body or public authority. */
    GOVERNMENT,

    /** A non-profit or charitable organisation. */
    NON_PROFIT
}

