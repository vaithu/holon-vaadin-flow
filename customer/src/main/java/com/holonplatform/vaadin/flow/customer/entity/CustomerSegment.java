package com.holonplatform.vaadin.flow.customer.entity;

/**
 * Size / value classification of the customer — used for segmentation,
 * pricing tiers, and reporting.  Maps to the i18n key prefix
 * {@code customer.segment.*}.
 */
public enum CustomerSegment {

    /** Very small businesses: 1–9 employees / minimal revenue. */
    MICRO,

    /** Small businesses: 10–49 employees. */
    SMALL,

    /** Mid-market companies: 50–249 employees. */
    MEDIUM,

    /** Large organisations: 250–999 employees. */
    LARGE,

    /** Enterprise accounts: 1 000+ employees or strategic partnerships. */
    ENTERPRISE
}

