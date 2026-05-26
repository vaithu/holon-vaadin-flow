package com.holonplatform.vaadin.flow.customer.entity;

/**
 * Payment terms defining when a customer invoice is due.
 * Maps to the i18n key prefix {@code customer.payment_terms.*}.
 */
public enum CustomerPaymentTerms {

    /** Payment is due immediately upon receipt of invoice. */
    DUE_ON_RECEIPT,

    /** Payment is due within 15 days. */
    NET_15,

    /** Payment is due within 30 days. */
    NET_30,

    /** Payment is due within 45 days. */
    NET_45,

    /** Payment is due within 60 days. */
    NET_60,

    /** Payment is due at the end of the current month. */
    END_OF_MONTH,

    /** Payment is due at the end of the following month. */
    END_OF_NEXT_MONTH
}

