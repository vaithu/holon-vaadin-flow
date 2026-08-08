package com.holonplatform.vaadin.flow.customer.i18n;

/**
 * Centralised i18n message-key constants for the Customer module.
 *
 * <p>Every user-facing string in the Customer UI is resolved through these keys
 * via Vaadin's {@code getTranslation(key)} call, which delegates to the
 * application's registered {@link com.vaadin.flow.i18n.I18NProvider}.</p>
 *
 * <h3>Bootstrapping</h3>
 * <p>Add the following entries to your application's {@code messages.properties}
 * (and the locale-specific variants, e.g. {@code messages_de.properties}).
 * Default English values are shown as comments.</p>
 *
 * <pre>
 * # ── Customer module ───────────────────────────────────────────────────────
 * customer.nav.title=Customers
 * customer.view.title=Customers
 * customer.view.subtitle=Manage customer accounts across your organisation
 * customer.view.new=New Customer
 * customer.view.edit=Edit
 * customer.view.delete=Delete
 * customer.view.save=Save
 * customer.view.cancel=Cancel
 * customer.view.reset=Reset
 * customer.view.search=Search customers\u2026
 * customer.view.confirm.delete.header=Delete customer?
 * customer.view.confirm.delete.text={0} will be permanently removed.
 * customer.view.confirm.delete.btn=Delete
 * customer.view.notify.saved={0} saved
 * customer.view.notify.deleted={0} deleted
 * customer.view.notify.error=Error: {0}
 * customer.view.select.placeholder=Select a customer
 * customer.view.empty.title=No customers yet
 * customer.view.empty.description=Create your first customer to get started
 * customer.view.records={0} records
 *
 * customer.breadcrumb.home=Home
 *
 * customer.section.core=Core
 * customer.section.identity=Identity
 * customer.section.contact=Contact
 * customer.section.billing=Billing Address
 * customer.section.shipping=Shipping Address
 * customer.section.business=Business Details
 * customer.section.other=Other Details
 * customer.section.notes=Notes &amp; Tags
 *
 * customer.field.display_name=Name
 * customer.field.type=Type
 * customer.field.status=Status
 * customer.field.customer_code=Customer Code
 * customer.field.payment_terms=Payment Terms
 * customer.field.salutation=Salutation
 * customer.field.first_name=First Name
 * customer.field.last_name=Last Name
 * customer.field.company_name=Company Name
 * customer.field.job_title=Job Title
 * customer.field.department=Department
 * customer.field.email=Email
 * customer.field.phone=Work Phone
 * customer.field.mobile=Mobile
 * customer.field.fax=Fax
 * customer.field.website=Website
 * customer.field.skype=Skype
 * customer.field.attention=Attention
 * customer.field.address_line1=Street
 * customer.field.address_line2=Street 2
 * customer.field.city=City
 * customer.field.state=State / Province
 * customer.field.postal_code=Zip Code
 * customer.field.country=Country
 * customer.field.tax_id=Tax ID
 * customer.field.vat_number=VAT Number
 * customer.field.segment=Segment
 * customer.field.industry=Industry
 * customer.field.currency=Currency
 * customer.field.language=Language
 * customer.field.notes=Notes
 * customer.field.tags=Tags
 * customer.field.referred_by=Referred By
 * customer.field.created_at=Created
 * customer.field.updated_at=Last Updated
 *
 * customer.shipping.same_as_billing=Same as Billing Address
 *
 * customer.type.INDIVIDUAL=Individual
 * customer.type.COMPANY=Business
 * customer.type.GOVERNMENT=Government
 * customer.type.NON_PROFIT=Non-Profit
 *
 * customer.status.PROSPECT=Prospect
 * customer.status.LEAD=Lead
 * customer.status.ACTIVE=Active
 * customer.status.INACTIVE=Inactive
 * customer.status.ARCHIVED=Archived
 *
 * customer.salutation.NONE=
 * customer.salutation.MR=Mr.
 * customer.salutation.MRS=Mrs.
 * customer.salutation.MS=Ms.
 * customer.salutation.DR=Dr.
 * customer.salutation.PROF=Prof.
 *
 * customer.payment_terms.DUE_ON_RECEIPT=Due on Receipt
 * customer.payment_terms.NET_15=Net 15
 * customer.payment_terms.NET_30=Net 30
 * customer.payment_terms.NET_45=Net 45
 * customer.payment_terms.NET_60=Net 60
 * customer.payment_terms.END_OF_MONTH=End of Month
 * customer.payment_terms.END_OF_NEXT_MONTH=End of Next Month
 *
 * customer.segment.MICRO=Micro
 * customer.segment.SMALL=Small
 * customer.segment.MEDIUM=Medium
 * customer.segment.LARGE=Large
 * customer.segment.ENTERPRISE=Enterprise
 * </pre>
 */
public final class CustomerI18n {

    private CustomerI18n() {}

    // ── Navigation / Page ─────────────────────────────────────────────────────
    public static final String NAV_TITLE           = "customer.nav.title";
    public static final String VIEW_TITLE          = "customer.view.title";
    public static final String VIEW_SUBTITLE       = "customer.view.subtitle";

    // ── CRUD Actions ──────────────────────────────────────────────────────────
    public static final String VIEW_NEW            = "customer.view.new";
    public static final String VIEW_EDIT           = "customer.view.edit";
    public static final String VIEW_DELETE         = "customer.view.delete";
    public static final String VIEW_SAVE           = "customer.view.save";
    public static final String VIEW_CANCEL         = "customer.view.cancel";
    public static final String VIEW_RESET          = "customer.view.reset";
    public static final String VIEW_ARCHIVE        = "customer.view.archive";
    public static final String VIEW_BACK           = "customer.view.back";

    // ── Search / Empty state ──────────────────────────────────────────────────
    public static final String VIEW_SEARCH         = "customer.view.search";
    public static final String VIEW_RECORDS        = "customer.view.records";
    public static final String VIEW_SELECT_PH      = "customer.view.select.placeholder";
    public static final String VIEW_EMPTY_TITLE    = "customer.view.empty.title";
    public static final String VIEW_EMPTY_DESC     = "customer.view.empty.description";

    // ── Confirm dialog ────────────────────────────────────────────────────────
    public static final String CONFIRM_DELETE_HDR  = "customer.view.confirm.delete.header";
    public static final String CONFIRM_DELETE_TEXT = "customer.view.confirm.delete.text";
    public static final String CONFIRM_DELETE_BTN  = "customer.view.confirm.delete.btn";

    // ── Notifications ─────────────────────────────────────────────────────────
    public static final String NOTIFY_SAVED        = "customer.view.notify.saved";
    public static final String NOTIFY_DELETED      = "customer.view.notify.deleted";
    public static final String NOTIFY_ERROR        = "customer.view.notify.error";

    // ── Breadcrumb ────────────────────────────────────────────────────────────
    public static final String BREADCRUMB_HOME     = "customer.breadcrumb.home";

    // ── Sections ──────────────────────────────────────────────────────────────
    public static final String SECTION_CORE        = "customer.section.core";
    public static final String SECTION_IDENTITY    = "customer.section.identity";
    public static final String SECTION_CONTACT     = "customer.section.contact";
    public static final String SECTION_ADDRESS     = "customer.section.address";
    public static final String SECTION_BILLING     = "customer.section.billing";
    public static final String SECTION_SHIPPING    = "customer.section.shipping";
    public static final String SECTION_BUSINESS    = "customer.section.business";
    public static final String SECTION_OTHER       = "customer.section.other";
    public static final String SECTION_NOTES       = "customer.section.notes";

    // ── Shipping shortcut ─────────────────────────────────────────────────────
    public static final String SHIPPING_SAME_AS_BILLING = "customer.shipping.same_as_billing";

    // ── Field labels ──────────────────────────────────────────────────────────
    public static final String FIELD_DISPLAY_NAME  = "customer.field.display_name";
    /** Placeholder hint for the display name field (e.g. "Auto-derived from name if left blank"). */
    public static final String FIELD_DISPLAY_NAME_PLACEHOLDER = "customer.field.display_name.placeholder";
    public static final String FIELD_TYPE          = "customer.field.type";
    public static final String FIELD_STATUS        = "customer.field.status";
    public static final String FIELD_CUSTOMER_CODE = "customer.field.customer_code";
    /** Placeholder hint for the customer code field (e.g. "e.g. CUST-001"). */
    public static final String FIELD_CUSTOMER_CODE_PLACEHOLDER = "customer.field.customer_code.placeholder";
    public static final String FIELD_PAYMENT_TERMS = "customer.field.payment_terms";
    public static final String FIELD_SALUTATION    = "customer.field.salutation";
    public static final String FIELD_FIRST_NAME    = "customer.field.first_name";
    public static final String FIELD_LAST_NAME     = "customer.field.last_name";
    public static final String FIELD_COMPANY_NAME  = "customer.field.company_name";
    public static final String FIELD_JOB_TITLE     = "customer.field.job_title";
    public static final String FIELD_DEPARTMENT    = "customer.field.department";
    public static final String FIELD_EMAIL         = "customer.field.email";
    public static final String FIELD_PHONE         = "customer.field.phone";
    public static final String FIELD_MOBILE        = "customer.field.mobile";
    public static final String FIELD_FAX           = "customer.field.fax";
    public static final String FIELD_WEBSITE       = "customer.field.website";
    public static final String FIELD_SKYPE         = "customer.field.skype";
    public static final String FIELD_ATTENTION     = "customer.field.attention";
    public static final String FIELD_ADDRESS_LINE1 = "customer.field.address_line1";
    public static final String FIELD_ADDRESS_LINE2 = "customer.field.address_line2";
    public static final String FIELD_CITY          = "customer.field.city";
    public static final String FIELD_STATE         = "customer.field.state";
    public static final String FIELD_POSTAL_CODE   = "customer.field.postal_code";
    public static final String FIELD_COUNTRY       = "customer.field.country";
    public static final String FIELD_TAX_ID        = "customer.field.tax_id";
    public static final String FIELD_VAT_NUMBER    = "customer.field.vat_number";
    public static final String FIELD_SEGMENT       = "customer.field.segment";
    public static final String FIELD_INDUSTRY      = "customer.field.industry";
    public static final String FIELD_CURRENCY      = "customer.field.currency";
    public static final String FIELD_LANGUAGE      = "customer.field.language";
    public static final String FIELD_NOTES         = "customer.field.notes";
    public static final String FIELD_TAGS          = "customer.field.tags";
    public static final String FIELD_REFERRED_BY   = "customer.field.referred_by";
    public static final String FIELD_CREATED_AT    = "customer.field.created_at";
    public static final String FIELD_UPDATED_AT    = "customer.field.updated_at";

    // ── Enum label helpers ────────────────────────────────────────────────────

    /** Returns the i18n key for a {@code CustomerType} enum constant. */
    public static String typeKey(com.holonplatform.vaadin.flow.customer.entity.CustomerType t) {
        return "customer.type." + t.name();
    }

    /** Returns the i18n key for a {@code CustomerStatus} enum constant. */
    public static String statusKey(com.holonplatform.vaadin.flow.customer.entity.CustomerStatus s) {
        return "customer.status." + s.name();
    }

    /** Returns the i18n key for a {@code CustomerSegment} enum constant. */
    public static String segmentKey(com.holonplatform.vaadin.flow.customer.entity.CustomerSegment seg) {
        return "customer.segment." + seg.name();
    }

    /** Returns the i18n key for a {@code CustomerSalutation} enum constant. */
    public static String salutationKey(com.holonplatform.vaadin.flow.customer.entity.CustomerSalutation s) {
        return "customer.salutation." + s.name();
    }

    /** Returns the i18n key for a {@code CustomerPaymentTerms} enum constant. */
    public static String paymentTermsKey(com.holonplatform.vaadin.flow.customer.entity.CustomerPaymentTerms pt) {
        return "customer.payment_terms." + pt.name();
    }
}

