package com.holonplatform.vaadin.flow.customer.entity;

import com.holonplatform.core.beans.Identifier;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * Core Customer entity for SaaS applications.
 *
 * <h3>Field groupings</h3>
 * <ul>
 *   <li><b>Core</b>      — id, customerCode, displayName, type, status, paymentTerms, createdAt, updatedAt</li>
 *   <li><b>Identity</b>  — salutation, firstName, lastName, companyName, jobTitle, department</li>
 *   <li><b>Contact</b>   — email, phone, mobile, fax, website, skype</li>
 *   <li><b>Billing Address</b>  — billingAttention, addressLine1, addressLine2, city, state, postalCode, country</li>
 *   <li><b>Shipping Address</b> — shippingAttention, shippingAddressLine1, shippingAddressLine2, shippingCity, shippingState, shippingPostalCode, shippingCountry</li>
 *   <li><b>Business</b>  — taxId, vatNumber, segment, industry, currency, language</li>
 *   <li><b>Extra</b>     — notes, tags, referredBy</li>
 * </ul>
 *
 * <p>All optional fields are nullable so callers may persist only the data they
 * have — the module is designed to satisfy the widest possible set of SaaS use-cases
 * without forcing the operator to fill every field.</p>
 *
 * <p>Consuming applications must content this package to their {@code @EntityScan}:
 * <pre>{@code @EntityScan("com.holonplatform.vaadin.flow.customer.entity")}</pre>
 */
@Entity(name = "customer")
@Table(name = "customer", indexes = {
        @Index(name = "idx_customer_status",       columnList = "status"),
        @Index(name = "idx_customer_type",         columnList = "type"),
        @Index(name = "idx_customer_display_name", columnList = "display_name"),
        @Index(name = "idx_customer_email",        columnList = "email"),
        @Index(name = "idx_customer_code",         columnList = "customer_code")
})
public class Customer {

    // ── PK ────────────────────────────────────────────────────────────────────

    /**
     * Surrogate primary key.  {@link Identifier} marks it for
     * {@code BeanDatastoreHelper} so INSERT vs UPDATE is inferred correctly.
     */
    @Identifier
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Core ──────────────────────────────────────────────────────────────────

    /**
     * Human-readable display name shown in listings and headings.
     * Auto-derived from {@link #firstName} + {@link #lastName} for individuals,
     * or from {@link #companyName} for organisations, via
     * {@link #deriveDisplayName()}.
     */
    @NotBlank
    @Size(max = 200)
    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;

    /** INDIVIDUAL, COMPANY, GOVERNMENT, or NON_PROFIT. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private CustomerType type = CustomerType.INDIVIDUAL;

    /** PROSPECT → LEAD → ACTIVE → INACTIVE → ARCHIVED. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CustomerStatus status = CustomerStatus.PROSPECT;

    /**
     * Internal reference code auto-assigned by the service (e.g. "CUST-00042").
     * Unique, indexed, visible in listings and invoices.
     */
    @Size(max = 50)
    @Column(name = "customer_code", length = 50, unique = true)
    private String customerCode;

    /** Payment terms that apply to invoices raised for this customer. */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_terms", length = 30)
    private CustomerPaymentTerms paymentTerms;

    // ── Identity ──────────────────────────────────────────────────────────────

    /** Formal salutation / honorific (Mr, Mrs, Ms, Dr, Prof). */
    @Enumerated(EnumType.STRING)
    @Column(name = "salutation", length = 10)
    private CustomerSalutation salutation;

    /** Given name — primarily used for INDIVIDUAL contacts. */
    @Size(max = 100)
    @Column(name = "first_name", length = 100)
    private String firstName;

    /** Family name — primarily used for INDIVIDUAL contacts. */
    @Size(max = 100)
    @Column(name = "last_name", length = 100)
    private String lastName;

    /** Legal entity name — used for COMPANY / GOVERNMENT / NON_PROFIT. */
    @Size(max = 200)
    @Column(name = "company_name", length = 200)
    private String companyName;

    /** Job title or role within their organisation. */
    @Size(max = 150)
    @Column(name = "job_title", length = 150)
    private String jobTitle;

    /** Department within the organisation (e.g. "Accounts Payable", "Procurement"). */
    @Size(max = 100)
    @Column(name = "department", length = 100)
    private String department;

    // ── Contact ───────────────────────────────────────────────────────────────

    /** Primary email address. */
    @Email
    @Size(max = 255)
    @Column(name = "email", length = 255)
    private String email;

    /** Primary office / landline phone number. */
    @Size(max = 50)
    @Column(name = "phone", length = 50)
    private String phone;

    /** Mobile / cell phone number. */
    @Size(max = 50)
    @Column(name = "mobile", length = 50)
    private String mobile;

    /** Fax number. */
    @Size(max = 50)
    @Column(name = "fax", length = 50)
    private String fax;

    /** Company or personal website. */
    @Size(max = 255)
    @Column(name = "website", length = 255)
    private String website;

    /** Skype handle or other IM identifier. */
    @Size(max = 100)
    @Column(name = "skype", length = 100)
    private String skype;

    // ── Billing Address ───────────────────────────────────────────────────────

    /** Attention line for billing address (person/department to address mail to). */
    @Size(max = 200)
    @Column(name = "billing_attention", length = 200)
    private String billingAttention;

    /** Street address line 1 (billing). */
    @Size(max = 255)
    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    /** Suite, apartment, floor, PO Box, etc. (billing). */
    @Size(max = 255)
    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    /** City / locality (billing). */
    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    /** State, province, or region (billing). */
    @Size(max = 100)
    @Column(name = "state", length = 100)
    private String state;

    /** ZIP / postal code (billing). */
    @Size(max = 20)
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /** Full country name or ISO 3166-1 alpha-2 code (billing). */
    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country;

    // ── Shipping Address ──────────────────────────────────────────────────────

    /** Attention line for shipping address. */
    @Size(max = 200)
    @Column(name = "shipping_attention", length = 200)
    private String shippingAttention;

    /** Street address line 1 (shipping). */
    @Size(max = 255)
    @Column(name = "shipping_address_line1", length = 255)
    private String shippingAddressLine1;

    /** Suite, apartment, floor, PO Box, etc. (shipping). */
    @Size(max = 255)
    @Column(name = "shipping_address_line2", length = 255)
    private String shippingAddressLine2;

    /** City / locality (shipping). */
    @Size(max = 100)
    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    /** State, province, or region (shipping). */
    @Size(max = 100)
    @Column(name = "shipping_state", length = 100)
    private String shippingState;

    /** ZIP / postal code (shipping). */
    @Size(max = 20)
    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    /** Full country name or ISO 3166-1 alpha-2 code (shipping). */
    @Size(max = 100)
    @Column(name = "shipping_country", length = 100)
    private String shippingCountry;

    // ── Business Details ──────────────────────────────────────────────────────

    /** National tax identification number. */
    @Size(max = 50)
    @Column(name = "tax_id", length = 50)
    private String taxId;

    /** VAT registration number (EU / international). */
    @Size(max = 50)
    @Column(name = "vat_number", length = 50)
    private String vatNumber;

    /** Customer size/value segment. */
    @Enumerated(EnumType.STRING)
    @Column(name = "segment", length = 20)
    private CustomerSegment segment;

    /** Industry vertical (e.g. "Healthcare", "FinTech", "Retail"). */
    @Size(max = 100)
    @Column(name = "industry", length = 100)
    private String industry;

    /** Preferred billing currency — ISO 4217 code (e.g. "USD", "EUR"). */
    @Size(max = 3)
    @Column(name = "currency", length = 3)
    private String currency;

    /** Preferred communication language — ISO 639-1 code (e.g. "en", "de"). */
    @Size(max = 10)
    @Column(name = "language", length = 10)
    private String language;

    // ── Extra / Optional ─────────────────────────────────────────────────────

    /** Free-form internal notes, visible only to staff. */
    @Column(name = "notes", length = 2000)
    private String notes;

    /** Comma-separated labels for flexible categorisation (e.g. "vip,beta-tester"). */
    @Size(max = 500)
    @Column(name = "tags", length = 500)
    private String tags;

    /** How this customer was acquired (e.g. "Partner: Acme Corp", "Campaign: Q3"). */
    @Size(max = 200)
    @Column(name = "referred_by", length = 200)
    private String referredBy;

    // ── Audit ─────────────────────────────────────────────────────────────────

    /** Timestamp of record creation — set once by the service layer. */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Timestamp of last update — refreshed on every save. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Constructors ──────────────────────────────────────────────────────────

    /** Required by JPA. */
    public Customer() {}

    /**
     * Convenience constructor for the minimum viable record.
     *
     * @param displayName human-readable name shown in listings
     * @param type        customer classification
     * @param status      initial lifecycle stage
     */
    public Customer(String displayName, CustomerType type, CustomerStatus status) {
        this.displayName = displayName;
        this.type        = type;
        this.status      = status;
        this.createdAt   = LocalDateTime.now();
        this.updatedAt   = LocalDateTime.now();
    }

    // ── Domain logic ──────────────────────────────────────────────────────────

    /**
     * Derives a sensible display name from available identity fields.
     * <ul>
     *   <li>INDIVIDUAL → "{firstName} {lastName}" (or email as fallback)</li>
     *   <li>Others     → companyName (or email as fallback)</li>
     * </ul>
     * Called by {@code CustomerService.save()} when {@link #displayName} is blank.
     */
    public String deriveDisplayName() {
        if (type == CustomerType.INDIVIDUAL) {
            String name = "";
            if (firstName != null && !firstName.isBlank()) name = firstName.trim();
            if (lastName  != null && !lastName.isBlank())
                name = name.isEmpty() ? lastName.trim() : name + " " + lastName.trim();
            return name.isEmpty() ? (email != null && !email.isBlank() ? email : "—") : name;
        } else {
            return (companyName != null && !companyName.isBlank())
                    ? companyName.trim()
                    : (email != null && !email.isBlank() ? email : "—");
        }
    }

    /**
     * Returns a compact one-line address string, or an empty string when no
     * address fields have been filled.  Used in listing subtitles.
     */
    public String formatAddress() {
        var sb = new StringBuilder();
        if (city        != null && !city.isBlank())    sb.append(city.trim());
        if (state       != null && !state.isBlank())   sb.append(sb.isEmpty() ? "" : ", ").append(state.trim());
        if (country     != null && !country.isBlank()) sb.append(sb.isEmpty() ? "" : ", ").append(country.trim());
        return sb.toString();
    }

    /** Returns {@code true} when at least one address field is non-blank. */
    public boolean hasAddress() {
        return (addressLine1 != null && !addressLine1.isBlank())
            || (city         != null && !city.isBlank())
            || (country      != null && !country.isBlank());
    }

    /** Returns {@code true} when at least one business detail field is non-blank. */
    public boolean hasBusinessDetails() {
        return (taxId    != null && !taxId.isBlank())
            || (vatNumber != null && !vatNumber.isBlank())
            || (segment  != null)
            || (industry != null && !industry.isBlank());
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public Long getId()                                  { return id; }
    public void setId(Long id)                          { this.id = id; }

    public String getDisplayName()                       { return displayName; }
    public void setDisplayName(String displayName)      { this.displayName = displayName; }

    public CustomerType getType()                        { return type; }
    public void setType(CustomerType type)              { this.type = type; }

    public CustomerStatus getStatus()                    { return status; }
    public void setStatus(CustomerStatus status)        { this.status = status; }

    public String getCustomerCode()                      { return customerCode; }
    public void setCustomerCode(String customerCode)    { this.customerCode = customerCode; }

    public CustomerPaymentTerms getPaymentTerms()                        { return paymentTerms; }
    public void setPaymentTerms(CustomerPaymentTerms paymentTerms)      { this.paymentTerms = paymentTerms; }

    public CustomerSalutation getSalutation()                            { return salutation; }
    public void setSalutation(CustomerSalutation salutation)            { this.salutation = salutation; }

    public String getFirstName()                         { return firstName; }
    public void setFirstName(String firstName)          { this.firstName = firstName; }

    public String getLastName()                          { return lastName; }
    public void setLastName(String lastName)            { this.lastName = lastName; }

    public String getCompanyName()                       { return companyName; }
    public void setCompanyName(String companyName)      { this.companyName = companyName; }

    public String getJobTitle()                          { return jobTitle; }
    public void setJobTitle(String jobTitle)            { this.jobTitle = jobTitle; }

    public String getDepartment()                        { return department; }
    public void setDepartment(String department)        { this.department = department; }

    public String getEmail()                             { return email; }
    public void setEmail(String email)                  { this.email = email; }

    public String getPhone()                             { return phone; }
    public void setPhone(String phone)                  { this.phone = phone; }

    public String getMobile()                            { return mobile; }
    public void setMobile(String mobile)                { this.mobile = mobile; }

    public String getFax()                               { return fax; }
    public void setFax(String fax)                      { this.fax = fax; }

    public String getWebsite()                           { return website; }
    public void setWebsite(String website)              { this.website = website; }

    public String getSkype()                             { return skype; }
    public void setSkype(String skype)                  { this.skype = skype; }

    public String getBillingAttention()                  { return billingAttention; }
    public void setBillingAttention(String billingAttention) { this.billingAttention = billingAttention; }

    public String getAddressLine1()                      { return addressLine1; }
    public void setAddressLine1(String addressLine1)    { this.addressLine1 = addressLine1; }

    public String getAddressLine2()                      { return addressLine2; }
    public void setAddressLine2(String addressLine2)    { this.addressLine2 = addressLine2; }

    public String getCity()                              { return city; }
    public void setCity(String city)                    { this.city = city; }

    public String getState()                             { return state; }
    public void setState(String state)                  { this.state = state; }

    public String getPostalCode()                        { return postalCode; }
    public void setPostalCode(String postalCode)        { this.postalCode = postalCode; }

    public String getCountry()                           { return country; }
    public void setCountry(String country)              { this.country = country; }

    public String getShippingAttention()                 { return shippingAttention; }
    public void setShippingAttention(String shippingAttention) { this.shippingAttention = shippingAttention; }

    public String getShippingAddressLine1()              { return shippingAddressLine1; }
    public void setShippingAddressLine1(String v)       { this.shippingAddressLine1 = v; }

    public String getShippingAddressLine2()              { return shippingAddressLine2; }
    public void setShippingAddressLine2(String v)       { this.shippingAddressLine2 = v; }

    public String getShippingCity()                      { return shippingCity; }
    public void setShippingCity(String shippingCity)    { this.shippingCity = shippingCity; }

    public String getShippingState()                     { return shippingState; }
    public void setShippingState(String shippingState)  { this.shippingState = shippingState; }

    public String getShippingPostalCode()                { return shippingPostalCode; }
    public void setShippingPostalCode(String v)         { this.shippingPostalCode = v; }

    public String getShippingCountry()                   { return shippingCountry; }
    public void setShippingCountry(String shippingCountry) { this.shippingCountry = shippingCountry; }

    public String getTaxId()                             { return taxId; }
    public void setTaxId(String taxId)                  { this.taxId = taxId; }

    public String getVatNumber()                         { return vatNumber; }
    public void setVatNumber(String vatNumber)          { this.vatNumber = vatNumber; }

    public CustomerSegment getSegment()                  { return segment; }
    public void setSegment(CustomerSegment segment)     { this.segment = segment; }

    public String getIndustry()                          { return industry; }
    public void setIndustry(String industry)            { this.industry = industry; }

    public String getCurrency()                          { return currency; }
    public void setCurrency(String currency)            { this.currency = currency; }

    public String getLanguage()                          { return language; }
    public void setLanguage(String language)            { this.language = language; }

    public String getNotes()                             { return notes; }
    public void setNotes(String notes)                  { this.notes = notes; }

    public String getTags()                              { return tags; }
    public void setTags(String tags)                    { this.tags = tags; }

    public String getReferredBy()                        { return referredBy; }
    public void setReferredBy(String referredBy)        { this.referredBy = referredBy; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()                  { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)  { this.updatedAt = updatedAt; }

    /**
     * Returns {@code true} when all shipping address fields are blank — i.e. the
     * shipping address is effectively the same as or absent from the billing address.
     */
    public boolean hasShippingAddress() {
        return (shippingAddressLine1 != null && !shippingAddressLine1.isBlank())
            || (shippingCity         != null && !shippingCity.isBlank())
            || (shippingCountry      != null && !shippingCountry.isBlank());
    }

    /**
     * Copies billing address fields into the shipping address fields.
     * Useful for a UI "same as billing" checkbox action.
     */
    public void copyBillingToShipping() {
        this.shippingAttention    = this.billingAttention;
        this.shippingAddressLine1 = this.addressLine1;
        this.shippingAddressLine2 = this.addressLine2;
        this.shippingCity         = this.city;
        this.shippingState        = this.state;
        this.shippingPostalCode   = this.postalCode;
        this.shippingCountry      = this.country;
    }

    @Override
    public String toString() {
        return "Customer[id=" + id + ", displayName=" + displayName + ", status=" + status + "]";
    }
}

