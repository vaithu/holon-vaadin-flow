package com.holonplatform.vaadin.flow.customer.service;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.beans.BeanDatastoreHelper;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.customer.entity.Customer;
import com.holonplatform.vaadin.flow.customer.entity.CustomerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Application service for {@link Customer} CRUD operations.
 *
 * <p>All persistence is delegated to {@link BeanDatastoreHelper}, which wraps the
 * Holon {@code BeanDatastore} auto-configured from the application's JPA
 * {@code EntityManagerFactory}.  No manual {@code PropertyBox} conversions are needed.</p>
 *
 * <p>This service is registered as a Spring {@code @Service} and will be auto-discovered
 * by any {@code @SpringBootApplication} that component-scans
 * {@code com.holonplatform.vaadin.flow.customer}.</p>
 */
@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    // ── Holon property references (typed, used in filters + sorts) ────────────

    private static final DataTarget<?>         TARGET        = DataTarget.named("customer");
    private static final PathProperty<Long>    ID_PROP       = PathProperty.create("id",          Long.class);
    private static final StringProperty        DISPLAY_NAME  = StringProperty.create("displayName");
    private static final StringProperty        EMAIL_PROP    = StringProperty.create("email");
    private static final StringProperty        PHONE_PROP    = StringProperty.create("phone");
    private static final StringProperty        COMPANY_PROP  = StringProperty.create("companyName");

    // ── CRUD delegate ─────────────────────────────────────────────────────────

    private final BeanDatastoreHelper<Customer> helper;

    public CustomerService(Datastore datastore) {
        this.helper = BeanDatastoreHelper.of(datastore, Customer.class);
    }

    // ── Read operations ───────────────────────────────────────────────────────

    /**
     * Finds a single customer by its surrogate key.
     *
     * @param id the database ID
     * @return the customer wrapped in {@code Optional}, or empty if not found
     */
    public Optional<Customer> findById(Long id) {
        return helper.findOne(ID_PROP.eq(id));
    }

    /**
     * Returns a paginated, optionally-filtered stream of customers sorted by
     * {@code displayName} ascending.
     *
     * <p>The caller is responsible for closing the returned stream — or iterating
     * it fully so the underlying JDBC cursor is released.</p>
     *
     * @param offset     zero-based row offset
     * @param limit      maximum number of rows
     * @param searchText free-text filter matched against displayName, email,
     *                   phone, and companyName; {@code null} or blank means no filter
     */
    public Stream<Customer> fetch(int offset, int limit, String searchText) {
        var q = helper.getDatastore()
                .query(TARGET)
                .restrict(limit, offset)
                .sort(DISPLAY_NAME.asc());
        if (searchText != null && !searchText.isBlank()) {
            q = q.filter(searchFilter(searchText));
        }
        return q.stream(BeanProjection.of(Customer.class));
    }

    /**
     * Returns the total number of customers matching the optional search text.
     *
     * @param searchText same semantics as in {@link #fetch}
     */
    public long count(String searchText) {
        var q = helper.getDatastore().query(TARGET);
        if (searchText != null && !searchText.isBlank()) {
            q = q.filter(searchFilter(searchText));
        }
        return q.count();
    }

    /**
     * Returns all active customers sorted by display name — used for
     * lightweight selects / autocomplete inputs.
     */
    public List<Customer> findAllActive() {
        try (Stream<Customer> s = helper.findAll(
                PathProperty.create("status", CustomerStatus.class).eq(CustomerStatus.ACTIVE))) {
            return s.sorted((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()))
                    .toList();
        }
    }

    // ── Write operations ──────────────────────────────────────────────────────

    /**
     * Persists a customer (INSERT for new records, UPDATE for existing ones).
     * Sets audit timestamps and auto-derives {@link Customer#getDisplayName()} when blank.
     *
     * @param customer the entity to save
     * @return the persisted entity (may carry generated ID after INSERT)
     */
    @Transactional
    public Customer save(Customer customer) {
        // Auto-fill display name when the caller did not set it explicitly
        if (customer.getDisplayName() == null || customer.getDisplayName().isBlank()) {
            customer.setDisplayName(customer.deriveDisplayName());
        }
        if (customer.getCreatedAt() == null) {
            customer.setCreatedAt(LocalDateTime.now());
        }
        customer.setUpdatedAt(LocalDateTime.now());

        log.info("Saving customer: id={} name={}", customer.getId(), customer.getDisplayName());
        return helper.save(customer).getResult().orElse(customer);
    }

    /**
     * Permanently removes a customer record.
     *
     * @param customer the entity to delete
     */
    @Transactional
    public void delete(Customer customer) {
        log.info("Deleting customer: id={} name={}", customer.getId(), customer.getDisplayName());
        helper.delete(customer);
    }

    /**
     * Permanently removes a customer by its primary key.
     *
     * @param id the database ID
     */
    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    // ── Seed data ─────────────────────────────────────────────────────────────

    /**
     * Inserts representative sample data when the {@code customer} table is empty.
     * Designed to be called from a Spring {@code ApplicationRunner} or
     * {@code CommandLineRunner} during startup.
     */
    @Transactional
    public void seedIfEmpty() {
        if (helper.getDatastore().query(TARGET).count() > 0) {
            return;
        }

        var seeds = List.of(
            buildSeed("Alice Martin",   "INDIVIDUAL", "ACTIVE",   "alice@techcorp.io",   "+44 20 7946 0111", "London",     "UK",
                      "TechCorp Ltd",    "Product Manager",  "Technology", "LARGE",     "GBP", "en"),
            buildSeed("Bob Chen",       "INDIVIDUAL", "ACTIVE",   "bob@greenarch.com",   "+49 30 1234 5678", "Berlin",     "Germany",
                      "GreenArch GmbH",  "Senior Engineer",  "Engineering","MEDIUM",    "EUR", "de"),
            buildSeed("Carol Torres",   "INDIVIDUAL", "PROSPECT", "carol@studio42.es",   "+34 91 000 1111",  "Madrid",     "Spain",
                      "Studio 42",       "UX Designer",      "Design",     "SMALL",     "EUR", "es"),
            buildSeed("GovTech Dept.",  "GOVERNMENT", "ACTIVE",   "info@govtech.gov.au", "+61 2 9000 0000",  "Sydney",     "Australia",
                      null,              null,               "Government", "ENTERPRISE","AUD", "en"),
            buildSeed("HopeFund",       "NON_PROFIT", "LEAD",     "hello@hopefund.org",  "+1 415 000 5500",  "San Francisco","US",
                      null,              "Executive Director","Non-Profit","MICRO",     "USD", "en"),
            buildSeed("David Kim",      "INDIVIDUAL", "INACTIVE", "d.kim@acmecorp.kr",   "+82 2 1234 5678",  "Seoul",      "South Korea",
                      "Acme Corp KR",    "DevOps Lead",      "Technology", "MEDIUM",    "KRW", "ko"),
            buildSeed("Eva Müller",     "INDIVIDUAL", "ACTIVE",   "eva@datasci.de",      "+49 89 9876 5432", "Munich",     "Germany",
                      "DataSci AG",      "Data Scientist",   "Analytics",  "SMALL",     "EUR", "de"),
            buildSeed("Riviera Hotels", "COMPANY",    "ACTIVE",   "ops@riviera.com",     "+33 4 93 000 000", "Nice",       "France",
                      null,              null,               "Hospitality","LARGE",     "EUR", "fr"),
            buildSeed("Frank Rossi",    "INDIVIDUAL", "PROSPECT", "frank@startup.io",    "+39 02 1234 5678", "Rome",       "Italy",
                      "Startup.io",      "Founder & CEO",    "FinTech",    "MICRO",     "EUR", "it"),
            buildSeed("Grace Patel",    "INDIVIDUAL", "ACTIVE",   "grace@qalab.in",      "+91 22 9999 8888", "Mumbai",     "India",
                      "QA Labs Pvt",     "QA Lead",          "Technology", "SMALL",     "INR", "en")
        );

        helper.bulkInsert(seeds);
        log.info("Seeded {} demo customers", seeds.size());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static QueryFilter searchFilter(String text) {
        return DISPLAY_NAME.containsIgnoreCase(text)
                .or(EMAIL_PROP.containsIgnoreCase(text))
                .or(PHONE_PROP.containsIgnoreCase(text))
                .or(COMPANY_PROP.containsIgnoreCase(text));
    }

    private static Customer buildSeed(String display, String typeName, String statusName,
                                      String email, String phone, String city, String country,
                                      String company, String jobTitle, String industry,
                                      String segmentName, String currency, String language) {
        var c = new Customer(display,
                com.holonplatform.vaadin.flow.customer.entity.CustomerType.valueOf(typeName),
                com.holonplatform.vaadin.flow.customer.entity.CustomerStatus.valueOf(statusName));
        c.setEmail(email);
        c.setPhone(phone);
        c.setCity(city);
        c.setCountry(country);
        c.setCompanyName(company);
        c.setJobTitle(jobTitle);
        c.setIndustry(industry);
        c.setSegment(com.holonplatform.vaadin.flow.customer.entity.CustomerSegment.valueOf(segmentName));
        c.setCurrency(currency);
        c.setLanguage(language);
        return c;
    }
}
