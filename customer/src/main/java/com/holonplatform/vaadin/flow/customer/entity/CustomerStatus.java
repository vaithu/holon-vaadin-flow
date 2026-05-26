package com.holonplatform.vaadin.flow.customer.entity;

/**
 * Lifecycle stage of a customer record.
 * Maps to the i18n key prefix {@code customer.status.*}.
 */
public enum CustomerStatus {

    /** Initial contact — not yet qualified. */
    PROSPECT,

    /** Qualified opportunity actively being pursued. */
    LEAD,

    /** Paying, active customer. */
    ACTIVE,

    /** Former customer, temporarily inactive. */
    INACTIVE,

    /** Record is archived / soft-deleted. Excluded from normal queries by convention. */
    ARCHIVED
}

