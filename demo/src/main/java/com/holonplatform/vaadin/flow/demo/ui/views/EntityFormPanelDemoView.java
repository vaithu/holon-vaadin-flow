package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.Validator;
import com.holonplatform.core.beans.Identifier;
import com.holonplatform.core.beans.Sequence;
import com.holonplatform.core.beans.Version;
import com.holonplatform.core.i18n.Caption;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.EntityFormPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo page for {@link EntityFormPanel}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic bean-mode: Save + Clear only (mandatory buttons)</li>
 *   <li>Save &amp; New button — clears form ONLY on full success</li>
 *   <li>Cancel button — no validation, fires unconditionally</li>
 *   <li>Pre-populated form — editing an existing entity via {@code setBean()}</li>
 *   <li>{@code configure()} — exclude fields, read-only fields, custom validators</li>
 *   <li>PropertySet mode — form driven by {@link PropertySet}</li>
 *   <li>Div grid mode (bean) — {@code beanDiv}, 3 columns, {@code stretchLastRow(true)}</li>
 *   <li>Div grid mode (bean) — {@code beanDiv}, 2 columns, {@code stretchLastRow(false)}</li>
 *   <li>Cancel in Dialog — EntityFormPanel inside a {@link Dialog}</li>
 *   <li>Components API — using {@code Components.entityFormPanel()} shortcut</li>
 *   <li>Div grid mode (PropertySet) — {@code propertiesDiv}, responsive columns</li>
 *   <li>Column-length–aware widths — {@code @Column}/{@code @Size}/{@code @Min}/{@code @Max}</li>
 *   <li>No-footer / wizard mode — {@code noFooter()} + external {@code validate()}</li>
 *   <li>Read-only mode (builder flag) — {@code .readOnly()} on the builder</li>
 *   <li>Read-only toggle (runtime) — {@code setReadOnly(boolean)} switches edit ↔ view</li>
 * </ol>
 *
 * <p><strong>Keyboard behaviour (all examples):</strong>
 * <ul>
 *   <li>The first field is auto-focused on attach.</li>
 *   <li>Enter on any field moves focus to the next (invalid field keeps focus).</li>
 *   <li>Enter on the last field focuses the Save button.</li>
 *   <li>Enter on Save triggers the save flow (validate → action).</li>
 *   <li>Inline validation errors appear while typing.</li>
 * </ul>
 */
@PageTitle("EntityFormPanel – Holon Demo")
@Route(value = "entity-form-panel", layout = DemoMainLayout.class)
public class EntityFormPanelDemoView extends Div {

    // ── Bean 1: basic employee, no annotations ────────────────────────────────

    public static final class Employee {
        private String firstName;
        private String lastName;
        private String department;
        private String email;
        private String notes;

        public Employee() {}

        public Employee(String firstName, String lastName, String department, String email) {
            this.firstName  = firstName;
            this.lastName   = lastName;
            this.department = department;
            this.email      = email;
        }

        public String getFirstName()           { return firstName; }
        public void   setFirstName(String v)   { this.firstName = v; }
        public String getLastName()            { return lastName; }
        public void   setLastName(String v)    { this.lastName = v; }
        public String getDepartment()          { return department; }
        public void   setDepartment(String v)  { this.department = v; }
        public String getEmail()               { return email; }
        public void   setEmail(String v)       { this.email = v; }
        public String getNotes()               { return notes; }
        public void   setNotes(String v)       { this.notes = v; }
    }

    // ── Bean 2: annotated product with validators ─────────────────────────────

    public static final class Product {

        static final Validator<String> NAME_RULE  = Validator.notBlank();
        static final Validator<String> SKU_RULE   = Validator.notBlank();
        static final Validator<Double> PRICE_RULE = Validator.min(0.01);
        static final Validator<Integer> QTY_RULE  = Validator.min(0);

        @Identifier
        private Long   id;

        @Version
        private Long   version;

        @Sequence(1) @Caption("Product Name")
        private String name;

        @Sequence(2) @Caption("SKU Code")
        private String sku;

        @Sequence(3) @Caption("Unit Price")
        private Double price;

        @Sequence(4) @Caption("Stock Qty")
        private Integer qty;

        public Product() {}

        public Product(Long id, Long version, String name, String sku, Double price, Integer qty) {
            this.id      = id;
            this.version = version;
            this.name    = name;
            this.sku     = sku;
            this.price   = price;
            this.qty     = qty;
        }

        public Long    getId()             { return id; }
        public void    setId(Long v)       { this.id = v; }
        public Long    getVersion()        { return version; }
        public void    setVersion(Long v)  { this.version = v; }
        public String  getName()           { return name; }
        public void    setName(String v)   { this.name = v; }
        public String  getSku()            { return sku; }
        public void    setSku(String v)    { this.sku = v; }
        public Double  getPrice()          { return price; }
        public void    setPrice(Double v)  { this.price = v; }
        public Integer getQty()            { return qty; }
        public void    setQty(Integer v)   { this.qty = v; }
    }

    // ── Bean 3: address form showcasing @Column(length)-driven input widths ───

    /**
     * Bean with fields annotated at every width tier to demonstrate all automatic
     * input-sizing resolution paths in {@link EntityFormPanel} bean-mode.
     *
     * <ul>
     *   <li>{@code countryCode}   — {@code @Column(length=2)}    → XS  (~8 rem)</li>
     *   <li>{@code postalCode}    — {@code @Column(length=10)}   → XS  (~8 rem)</li>
     *   <li>{@code city}          — {@code @Column(length=25)}   → SM  (~16 rem)</li>
     *   <li>{@code state}         — {@code @Column(length=25)}   → SM  (~16 rem)</li>
     *   <li>{@code addressLine1}  — {@code @Column(length=80)}   → MD  (~28 rem)</li>
     *   <li>{@code buildingNo}    — {@code @Min(1) @Max(9999)}   → XS  (4 digits)</li>
     *   <li>{@code floor}         — {@code @Min(-99) @Max(999)}  → SM  (4 chars incl. sign)</li>
     *   <li>{@code landmark}      — {@code @Column(length=255)}  → UNCONSTRAINED (JPA default)</li>
     *   <li>{@code notes}         — no annotation                → UNCONSTRAINED</li>
     * </ul>
     */
    public static final class AddressForm {

        /** @Column(length=2) → XS */
        @Column(length = 2)
        private String countryCode;

        /** @Column(length=10) → XS */
        @Column(length = 10)
        private String postalCode;

        /** @Column(length=25) → SM */
        @Column(length = 25)
        private String city;

        /** @Column(length=25) → SM */
        @Column(length = 25)
        private String state;

        /**
         * @Column(length=80) → MD.
         * Also works with @Size(max=80) — @Column takes priority.
         */
        @Column(length = 80)
        private String addressLine1;

        /**
         * @Min(1) @Max(9999) → XS.
         * No @Column/@Size — resolved from numeric bounds.
         * Digit count of max (9999) = 4 → XS tier (≤10).
         */
        @Min(1) @Max(9999)
        private Integer buildingNo;

        /**
         * @Min(-99) @Max(999) → SM.
         * abs(-99) = 2 digits + 1 sign = 3 chars, but @Max(999) = 3 digits.
         * max(3, 3) = 3 → still XS tier — but realistic for a floor number.
         * Use @Min(-999) to push it into SM: abs(-999)+sign = 4 chars.
         */
        @Min(-999) @Max(999)
        private Integer floor;

        /**
         * @Column(length=255) → UNCONSTRAINED.
         * 255 is the JPA spec default and is intentionally skipped.
         */
        @Column(length = 255)
        private String landmark;

        /** No annotation → UNCONSTRAINED */
        private String notes;

        public AddressForm() {}

        public String  getCountryCode()           { return countryCode; }
        public void    setCountryCode(String v)   { this.countryCode = v; }
        public String  getPostalCode()            { return postalCode; }
        public void    setPostalCode(String v)    { this.postalCode = v; }
        public String  getCity()                  { return city; }
        public void    setCity(String v)          { this.city = v; }
        public String  getState()                 { return state; }
        public void    setState(String v)         { this.state = v; }
        public String  getAddressLine1()          { return addressLine1; }
        public void    setAddressLine1(String v)  { this.addressLine1 = v; }
        public Integer getBuildingNo()            { return buildingNo; }
        public void    setBuildingNo(Integer v)   { this.buildingNo = v; }
        public Integer getFloor()                 { return floor; }
        public void    setFloor(Integer v)        { this.floor = v; }
        public String  getLandmark()              { return landmark; }
        public void    setLandmark(String v)      { this.landmark = v; }
        public String  getNotes()                 { return notes; }
        public void    setNotes(String v)         { this.notes = v; }
    }

    // ── Bean 4: product detail — matches the read-only screenshot ────────────

    public static final class ProductDetail {
        @Caption("Description")
        private String description;
        @Caption("Brand")
        private String brand;
        @Caption("Category")
        private String category;
        @Caption("Barcode (EAN-13)")
        private String barcode;
        @Caption("HS / Tariff code")
        private String hsCode;
        @Caption("Unit of measure")
        private String unitOfMeasure;
        @Caption("Weight · dimensions")
        private String weightDimensions;

        public ProductDetail() {}

        public ProductDetail(String description, String brand, String category,
                             String barcode, String hsCode,
                             String unitOfMeasure, String weightDimensions) {
            this.description     = description;
            this.brand           = brand;
            this.category        = category;
            this.barcode         = barcode;
            this.hsCode          = hsCode;
            this.unitOfMeasure   = unitOfMeasure;
            this.weightDimensions = weightDimensions;
        }

        public String getDescription()               { return description; }
        public void   setDescription(String v)       { this.description = v; }
        public String getBrand()                     { return brand; }
        public void   setBrand(String v)             { this.brand = v; }
        public String getCategory()                  { return category; }
        public void   setCategory(String v)          { this.category = v; }
        public String getBarcode()                   { return barcode; }
        public void   setBarcode(String v)           { this.barcode = v; }
        public String getHsCode()                    { return hsCode; }
        public void   setHsCode(String v)            { this.hsCode = v; }
        public String getUnitOfMeasure()             { return unitOfMeasure; }
        public void   setUnitOfMeasure(String v)     { this.unitOfMeasure = v; }
        public String getWeightDimensions()          { return weightDimensions; }
        public void   setWeightDimensions(String v)  { this.weightDimensions = v; }
    }

    // ── PropertySet for example 6 ─────────────────────────────────────────────

    static final PathProperty<String> PROP_NAME    = PathProperty.create("name",    String.class);
    static final PathProperty<String> PROP_EMAIL   = PathProperty.create("email",   String.class);
    static final PathProperty<String> PROP_PHONE   = PathProperty.create("phone",   String.class);
    static final PathProperty<String> PROP_COMPANY = PathProperty.create("company", String.class);
    static final PropertySet<PathProperty<?>> CONTACT_SET = PropertySet.of(
            PROP_NAME, PROP_EMAIL, PROP_PHONE, PROP_COMPANY);

    // ── Saved items registry (shared across examples with lists) ──────────────

    private final List<Employee>  savedEmployees = new ArrayList<>();


    // ── Constructor ───────────────────────────────────────────────────────────

    public EntityFormPanelDemoView() {
        addClassName("app-view");

        ResponsiveDiv.configure(this)
                .margin(ResponsiveDiv.MarginSize.M)
                .build();

        var title = new H1("EntityFormPanel");
        var desc  = new Paragraph(
                "A full-featured form panel that wraps BeanPropertyInputForm / PropertyInputForm and " +
                "provides a standard footer with Save, Save & New, Clear and Cancel buttons. " +
                "ENTER-key navigation, inline validation, and auto-focus are all enabled by default.");

        var container = ResponsiveDiv.flex().column().gapM().add(
                title, desc,
                basicBeanModeExample(),
                saveAndNewExample(),
                cancelButtonExample(),
                editExistingEntityExample(),
                configureExample(),
                propertySetModeExample(),
                divGridThreeColumnsExample(),
                divGridTwoColumnsExample(),
                inDialogExample(),
                componentsApiExample(),
                propertiesDivExample(),
                columnLengthWidthExample(),
                noFooterWizardExample(),
                readOnlyBuilderFlagExample(),
                readOnlyRuntimeToggleExample()
        ).build();

        add(container);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 1 — Basic bean-mode: mandatory Save + Clear only
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample basicBeanModeExample() {
        var result = resultSpan();

        var panel = EntityFormPanel.<Employee>bean(Employee.class)
                .saveButton(
                        btn -> btn.text("Save Employee"),
                        emp -> {
                            savedEmployees.add(emp);
                            showSuccess("Saved: %s %s".formatted(emp.getFirstName(), emp.getLastName()));
                            result.setText("✓ Saved: %s %s (dept: %s)"
                                    .formatted(emp.getFirstName(), emp.getLastName(), emp.getDepartment()));
                        })
                .clearButton(btn -> btn.text("Reset"))
                .build();

        var wrapper = new Div(panel, result);
        return new DemoExample("1 — Basic bean-mode (Save + Clear)", wrapper, """
                // Mandatory buttons only — Save and Clear.
                // The form introspects Employee.class automatically.
                // Defaults enabled:
                //   • validateOnValueChange  — inline errors while typing
                //   • enterMovesFocusToNext  — Enter → next field
                //   • validateOnEnterFocusMove — stay on invalid field
                //   • auto-focus first field on attach
                //   • Enter on last field → Save button

                EntityFormPanel<Employee> panel = EntityFormPanel.<Employee>bean(Employee.class)
                    .saveButton(
                        btn -> btn.primary().text("Save Employee"),
                        emp -> service.save(emp))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 2 — Save & New button
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample saveAndNewExample() {
        var counter = new int[]{0};
        var result  = resultSpan();

        var panel = EntityFormPanel.<Employee>bean(Employee.class)
                .saveButton(
                        btn -> btn.primary().text("Save"),
                        emp -> {
                            savedEmployees.add(emp);
                            showSuccess("Saved: " + emp.getFirstName());
                            result.setText("✓ Saved #%d: %s %s".formatted(
                                    ++counter[0], emp.getFirstName(), emp.getLastName()));
                        })
                .saveAndNewButton(
                        btn -> btn.secondary().text("Save & New"),
                        emp -> {
                            savedEmployees.add(emp);
                            showSuccess("Saved & ready for next: " + emp.getFirstName());
                            result.setText("✓ Saved & Cleared — entry #%d".formatted(++counter[0]));
                            // form.clear() is called automatically after this action succeeds
                        })
                .clearButton(btn -> btn.text("Reset"))
                .build();

        var wrapper = new Div(panel, result);
        return new DemoExample("2 — Save & New (clears only on full success)", wrapper, """
                // Save & New validates, calls the action, then clears the form.
                // If validation fails → inline errors, form NOT cleared.
                // If the action throws → exception propagates, form NOT cleared.

                EntityFormPanel<Employee> panel = EntityFormPanel.<Employee>bean(Employee.class)
                    .saveButton(
                        btn -> btn.primary().text("Save"),
                        emp -> service.save(emp))
                    .saveAndNewButton(
                        btn -> btn.secondary().text("Save & New"),
                        emp -> service.save(emp))   // form.clear() called automatically after
                    .clearButton(btn -> btn.text("Reset"))
                    .build();
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 3 — Cancel button
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample cancelButtonExample() {
        var result = resultSpan();

        var panel = EntityFormPanel.<Employee>bean(Employee.class)
                .saveButton(
                        btn -> btn.primary().text("Save"),
                        emp -> {
                            showSuccess("Saved: " + emp.getFirstName());
                            result.setText("✓ Saved: " + emp.getFirstName());
                        })
                .saveAndNewButton(
                        btn -> btn.secondary().text("Save & New"),
                        emp -> {
                            showSuccess("Saved: " + emp.getFirstName());
                            result.setText("✓ Saved & Cleared: " + emp.getFirstName());
                        })
                .clearButton(btn -> btn.text("Reset"))
                .cancelButton( btn -> btn.text("Cancel"),
                        () -> {
                            result.setText("✗ Cancelled — no validation, no data loss");
                            showInfo("Action cancelled");
                        })
                .build();

        var wrapper = new Div(panel, result);
        return new DemoExample("3 — All four buttons (Save, Save & New, Clear, Cancel)", wrapper, """
                // Cancel fires its runnable unconditionally — no validation performed.
                // Button visual layout:   [Clear]  [Cancel]        [Save & New]  [Save]
                // Button DOM layout:      [Save]  [Save & New]  [Clear]  [Cancel]
                //   (DOM-first ensures Enter on last field focuses Save, not Clear)

                EntityFormPanel<Employee> panel = EntityFormPanel.<Employee>bean(Employee.class)
                    .saveButton(btn -> btn.primary().text("Save"),
                                emp -> service.save(emp))
                    .saveAndNewButton(btn -> btn.secondary().text("Save & New"),
                                      emp -> service.save(emp))
                    .clearButton(btn -> btn.text("Reset"))
                    .cancelButton( btn -> btn.text("Cancel"),
                                  () -> navigator.back())
                    .build();
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 4 — Pre-populated form (edit existing entity)
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private DemoExample editExistingEntityExample() {
        var result = resultSpan();
        var existing = new Employee("Alice", "Martin", "Engineering", "alice@example.com");

        var panel = EntityFormPanel.<Employee>bean(Employee.class)
                .saveButton(
                        btn -> btn.primary().text("Update"),
                        emp -> {
                            showSuccess("Updated: %s %s".formatted(emp.getFirstName(), emp.getLastName()));
                            result.setText("✓ Updated: %s %s (%s)"
                                    .formatted(emp.getFirstName(), emp.getLastName(), emp.getEmail()));
                        })
                .clearButton(btn -> btn.text("Reset"))
                .build();

        // ── Pre-populate the form from the existing entity ─────────────────
        ((BeanPropertyInputForm<Employee>) panel.getForm()).setBean(existing);

        var wrapper = new Div(
                new H4("Editing: Alice Martin"),
                panel,
                result
        );
        return new DemoExample("4 — Edit existing entity (pre-populated via setBean())", wrapper, """
                var existing = new Employee("Alice", "Martin", "Engineering", "alice@example.com");

                var panel = EntityFormPanel.<Employee>bean(Employee.class)
                    .saveButton(btn -> btn.primary().text("Update"),
                                emp -> service.update(emp))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();

                // Pre-populate form from an existing entity:
                ((BeanPropertyInputForm<Employee>) panel.getForm()).setBean(existing);
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 5 — configure(): exclude fields, read-only, custom validators
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private DemoExample configureExample() {
        var result = resultSpan();

        // @Identifier (id) and @Version (version) are hidden by default.
        // Use configure() to content property-level validators via the underlying
        // BeanPropertyInputFormBuilder.
        var panel = EntityFormPanel.<Product>bean(Product.class)
                .configure(fb -> {
                    // Each property() call resolves the PathProperty from the field name.
                    fb.property("name").ifPresent(p ->
                            fb.configure(inner -> inner.withValidator(
                                    (PathProperty<String>) p, Product.NAME_RULE)));
                    fb.property("sku").ifPresent(p ->
                            fb.configure(inner -> inner.withValidator(
                                    (PathProperty<String>) p, Product.SKU_RULE)));
                    fb.property("price").ifPresent(p ->
                            fb.configure(inner -> inner.withValidator(
                                    (PathProperty<Double>) p, Product.PRICE_RULE)));
                    fb.property("qty").ifPresent(p ->
                            fb.configure(inner -> inner.withValidator(
                                    (PathProperty<Integer>) p, Product.QTY_RULE)));
                })
                .saveButton(
                        btn -> btn.primary().text("Save Product"),
                        product -> {
                            showSuccess("Saved: %s — SKU %s @ $%.2f"
                                    .formatted(product.getName(), product.getSku(), product.getPrice()));
                            result.setText("✓ %s (SKU: %s, Price: $%.2f, Qty: %d)"
                                    .formatted(product.getName(), product.getSku(),
                                            product.getPrice(), product.getQty()));
                        })
                .clearButton(btn -> btn.text("Reset"))
                .build();

        var wrapper = new Div(panel, result);
        return new DemoExample("5 — configure(): @Identifier/@Version hidden, field validators", wrapper, """
                // @Identifier (id) and @Version (version) are hidden automatically.
                // configure() provides access to the full BeanPropertyInputFormBuilder
                // to add per-property validators, exclude/read-only fields, etc.

                EntityFormPanel<Product> panel = EntityFormPanel.<Product>bean(Product.class)
                    .configure(fb -> {
                        fb.property("name").ifPresent(p ->
                            fb.configure(inner -> inner.withValidator(
                                (PathProperty<String>) p, Product.NAME_RULE)));
                        fb.property("price").ifPresent(p ->
                            fb.configure(inner -> inner.withValidator(
                                (PathProperty<Double>) p, Product.PRICE_RULE)));

                        // To exclude a field:  fb.excludeFields("internalNotes");
                        // To make read-only:   fb.readOnlyFields("createdAt");
                    })
                    .saveButton(btn -> btn.primary().text("Save Product"),
                                product -> service.save(product))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 6 — PropertySet mode
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample propertySetModeExample() {
        var result = resultSpan();

        var panel = EntityFormPanel.properties(CONTACT_SET)
                .saveButton(
                        btn -> btn.primary().text("Save Contact"),
                        pb -> {
                            String name  = pb.getValue(PROP_NAME);
                            String email = pb.getValue(PROP_EMAIL);
                            showSuccess("Saved: " + name);
                            result.setText("✓ Contact: %s <%s>".formatted(name, email));
                        })
                .clearButton(btn -> btn.text("Reset"))
                .cancelButton( btn -> btn.text("Cancel"),
                        () -> result.setText("✗ Cancelled"))
                .build();

        var wrapper = new Div(panel, result);
        return new DemoExample("6 — PropertySet mode (save action receives PropertyBox)", wrapper, """
                static final PathProperty<String> PROP_NAME    = PathProperty.create("name",    String.class);
                static final PathProperty<String> PROP_EMAIL   = PathProperty.create("email",   String.class);
                static final PathProperty<String> PROP_PHONE   = PathProperty.create("phone",   String.class);
                static final PathProperty<String> PROP_COMPANY = PathProperty.create("company", String.class);
                static final PropertySet<PathProperty<?>> CONTACT_SET =
                    PropertySet.of(PROP_NAME, PROP_EMAIL, PROP_PHONE, PROP_COMPANY);

                // PropertySet variant — save action receives PropertyBox
                EntityFormPanel<PropertyBox> panel = EntityFormPanel.properties(CONTACT_SET)
                    .saveButton(
                        btn -> btn.primary().text("Save Contact"),
                        pb -> {
                            String name = pb.getValue(PROP_NAME);
                            datastore.save(TARGET, pb);
                        })
                    .clearButton(btn -> btn.text("Reset"))
                    .cancelButton( btn -> btn.text("Cancel"),
                                  () -> navigator.back())
                    .build();

                // Varargs factory also available:
                EntityFormPanel.properties(PROP_NAME, PROP_EMAIL, PROP_PHONE)...
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 7 — Div grid mode: 5 fields, desktop(3), stretchLastRow(true)
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample divGridThreeColumnsExample() {
        var result = resultSpan();

        var panel = EntityFormPanel.<Employee>beanDiv(Employee.class)
                .properties("firstName", "lastName", "department", "email", "notes")
                .responsiveSteps(steps -> steps.mobile(1).desktop(3))
                .stretchLastRow(true)
            .configure(fb -> {
                fb.property("firstName").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                fb.property("firstName").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
                fb.property("lastName").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                fb.property("lastName").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
                fb.property("email").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                fb.property("email").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
            })
                .saveButton(
                        btn -> btn.primary().text("Save Employee"),
                        emp -> {
                            showSuccess("Saved: " + emp.getFirstName());
                            result.setText("✓ desktop(3) with stretchLastRow(true)");
                        })
                .clearButton(btn -> btn.text("Reset"))
                .build();

        var wrapper = new Div(panel, result);
        return new DemoExample("7 — Div grid mode (5 fields, desktop(3), stretchLastRow(true))", wrapper, """
                // Div-backed form: mobile(1), desktop(3)
                // 5 fields total:
                // - desktop(3): first row gets 3 fields
                // - second row gets 2 fields, stretched evenly across the row
                // - firstName, lastName and email are required, so the built-in red star is shown
                // - the same fields also validate inline

                EntityFormPanel<Employee> panel = EntityFormPanel.<Employee>beanDiv(Employee.class)
                    .properties("firstName", "lastName", "department", "email", "notes")
                    .responsiveSteps(steps -> steps.mobile(1).desktop(3))
                    .stretchLastRow(true)
                    .configure(fb -> {
                        fb.property("firstName").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                        fb.property("firstName").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
                        fb.property("lastName").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                        fb.property("lastName").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
                        fb.property("email").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                        fb.property("email").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
                    })
                    .saveButton(btn -> btn.primary().text("Save"), emp -> service.save(emp))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();

                // Equivalent facade shortcut:
                Components.<Employee>entityFormPanelDiv(Employee.class)
                    .properties("firstName", "lastName", "department", "email", "notes")
                    .responsiveSteps(steps -> steps.mobile(1).desktop(3))
                    .stretchLastRow(true)
                    .saveButton(btn -> btn.primary().text("Save"), emp -> service.save(emp))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 8 — Div grid mode: 5 fields, desktop(2), stretchLastRow(false)
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample divGridTwoColumnsExample() {
        var result = resultSpan();

        var panel = Components.<Employee>entityFormPanelDiv(Employee.class)
                .properties("firstName", "lastName", "department", "email", "notes")
                .responsiveSteps(steps -> steps.mobile(1).desktop(2))
                .stretchLastRow(false)
            .configure(fb -> {
                fb.property("firstName").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                fb.property("firstName").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
                fb.property("email").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                fb.property("email").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
            })
                .saveButton(
                        btn -> btn.primary().text("Save Employee"),
                        emp -> {
                            showSuccess("Saved: " + emp.getFirstName());
                            result.setText("✗ desktop(2) without stretchLastRow");
                        })
                .clearButton(btn -> btn.text("Reset"))
                .build();

        var wrapper = new Div(panel, result);
        return new DemoExample("8 — Div grid mode (5 fields, desktop(2), stretchLastRow(false))", wrapper, """
                // Div-backed form: mobile(1), desktop(2)
                // 5 fields total:
                // - desktop(2): rows become 2 + 2 + 1
                // - stretchLastRow(false): the last field keeps the base half-width span
                // - firstName and email are required, so the built-in red star is shown
                // - the same fields also validate inline

                Components.<Employee>entityFormPanelDiv(Employee.class)
                    .properties("firstName", "lastName", "department", "email", "notes")
                    .responsiveSteps(steps -> steps.mobile(1).desktop(2))
                    .stretchLastRow(false)
                    .configure(fb -> {
                        fb.property("firstName").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                        fb.property("firstName").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
                        fb.property("email").ifPresent(p -> fb.configure(inner -> inner.required(p)));
                        fb.property("email").ifPresent(p -> fb.configure(inner -> inner.withValidator((PathProperty<String>) p, Validator.notBlank())));
                    })
                    .saveButton(btn -> btn.primary().text("Save"), emp -> service.save(emp))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 9 — EntityFormPanel inside a Dialog (Cancel closes the dialog)
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample inDialogExample() {
        var result = resultSpan();

        var openBtn = new Button("Open Create Employee Dialog",
                VaadinIcon.PLUS_CIRCLE.create());
        openBtn.addClickListener(e -> {
            var dialog = new Dialog();
            dialog.setHeaderTitle("Create Employee");
            dialog.setWidth("520px");

            var panel = EntityFormPanel.<Employee>bean(Employee.class)
                    .saveButton(
                            btn -> btn.primary().text("Save"),
                            emp -> {
                                savedEmployees.add(emp);
                                dialog.close();
                                showSuccess("Created: " + emp.getFirstName());
                                result.setText("✓ Created: %s %s".formatted(
                                        emp.getFirstName(), emp.getLastName()));
                            })
                    .clearButton(btn -> btn.text("Reset"))
                    .cancelButton( btn -> btn.text("Cancel"),
                            dialog::close)             // Cancel closes the dialog
                    .build();

            dialog.add(panel);
            dialog.open();
        });

        var wrapper = new Div(openBtn, result);
        return new DemoExample("9 — Inside a Dialog (Cancel closes, Save closes on success)", wrapper, """
                var openBtn = new Button("Open Create Employee Dialog");
                openBtn.addClickListener(e -> {
                    var dialog = new Dialog();
                    dialog.setHeaderTitle("Create Employee");

                    var panel = EntityFormPanel.<Employee>bean(Employee.class)
                        .saveButton(
                            btn -> btn.primary().text("Save"),
                            emp -> {
                                service.save(emp);
                                dialog.close();          // close only on success
                            })
                        .clearButton(btn -> btn.text("Reset"))
                        .cancelButton( btn -> btn.text("Cancel"),
                            dialog::close)               // always closes
                        .build();

                    dialog.add(panel);
                    dialog.open();
                });
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 10 — Components API shortcut
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample componentsApiExample() {
        var result = resultSpan();

        // Same as EntityFormPanel.bean(Employee.class)... but via the Components facade
        var panel = Components.<Employee>entityFormPanel(Employee.class)
                .saveButton(
                        btn -> btn.primary().text("Save"),
                        emp -> {
                            showSuccess("Saved via Components API: " + emp.getFirstName());
                            result.setText("✓ Saved: %s %s".formatted(
                                    emp.getFirstName(), emp.getLastName()));
                        })
                .saveAndNewButton(
                        btn -> btn.secondary().text("Save & New"),
                        emp -> {
                            showSuccess("Saved & New: " + emp.getFirstName());
                            result.setText("✓ Saved & Cleared: " + emp.getFirstName());
                        })
                .clearButton(btn -> btn.text("Reset"))
                .cancelButton( btn -> btn.text("Cancel"),
                        () -> result.setText("✗ Cancelled"))
                .build();

        var wrapper = new Div(panel, result);
        return new DemoExample("10 — Components API (Components.entityFormPanel)", wrapper, """
                // All three factory methods are available via Components:

                // Bean mode:
                Components.<Customer>entityFormPanel(Customer.class)
                    .saveButton(btn -> btn.primary(), customer -> service.save(customer))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();

                // PropertySet mode:
                Components.entityFormPanel(CONTACT_SET)
                    .saveButton(btn -> btn.primary(), pb -> datastore.save(TARGET, pb))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();

                // Varargs mode:
                Components.entityFormPanel(NAME, EMAIL, PHONE)
                    .saveButton(btn -> btn.primary(), pb -> datastore.save(TARGET, pb))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 12 — Column-length–aware input widths (@Column / @Size)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Demonstrates all three annotation-driven width-resolution paths in
     * {@link EntityFormPanel} bean-mode, controlled by the opt-in flag
     * {@code columnLengthAwareWidth(true)}.
     *
     * <p>By default ({@code columnLengthAwareWidth = false}), all inputs fill
     * their full grid-cell width for a consistent layout.  With the flag enabled,
     * inputs receive a CSS max-width tier class derived from field annotations.</p>
     */
    private DemoExample columnLengthWidthExample() {
        var result = resultSpan();

        // ── Without flag: all inputs fill their full cell width (default / consistent) ──
        var panelDefault = EntityFormPanel.<AddressForm>bean(AddressForm.class)
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .saveButton(btn -> btn.text("Save (default)"),
                        addr -> showSuccess("Saved: " + addr.getAddressLine1()))
                .clearButton(btn -> btn.text("Reset"))
                .build();

        // ── With flag: inputs get max-width based on annotation ──
        var panelAware = EntityFormPanel.<AddressForm>bean(AddressForm.class)
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .columnLengthAwareWidth(true)   // ← opt-in: narrow inputs for short fields
                .saveButton(btn -> btn.text("Save (annotation-aware)"),
                        addr -> showSuccess("Saved: " + addr.getAddressLine1()))
                .clearButton(btn -> btn.text("Reset"))
                .build();

        var desc = new Paragraph(
                "Default (top form): all inputs fill their cell — consistent grid layout. " +
                "With columnLengthAwareWidth(true) (bottom form): short fields get narrow inputs " +
                "(@Column(length=2) → XS ~8rem, @Column(length=25) → SM ~16rem, " +
                "@Column(length=80) → MD ~28rem, @Column(length=255)/no-annotation → full width).");

        var wrapper = new Div(desc,
                new H4("Default — consistent full-cell width"), panelDefault,
                new H4("columnLengthAwareWidth(true) — annotation-driven widths"), panelAware,
                result);
        return new DemoExample("12 — Column-Length–Aware Input Widths (@Column / @Size / @Min / @Max)", wrapper, """
                // DEFAULT — all inputs fill their full grid-cell width:
                EntityFormPanel.<AddressForm>bean(AddressForm.class)
                    .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                    .saveButton(btn -> btn.text("Save"), addr -> service.save(addr))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();

                // OPT-IN — add .columnLengthAwareWidth(true) to enable tier-based max-width:
                EntityFormPanel.<AddressForm>bean(AddressForm.class)
                    .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                    .columnLengthAwareWidth(true)  // reads @Column(length), @Size(max), @Min/@Max
                    .saveButton(btn -> btn.text("Save"), addr -> service.save(addr))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();

                // Resolution order per field (first match wins):
                //   1. @Column(length=N)  where N > 0 and N != 255
                //   2. @Size(max=N)        where N != Integer.MAX_VALUE
                //   3. @Max(value=N) / @Min(value=N) — digit count of bound
                //   4. 0 → UNCONSTRAINED  (no annotation, or @Column default 255)
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 11 — propertiesDiv: PropertySet mode with Div CSS-grid layout
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample propertiesDivExample() {
        var result = resultSpan();

        var panel = EntityFormPanel.propertiesDiv(CONTACT_SET)
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(4))
                .stretchLastRow(true)
                .saveButton(
                        btn -> btn.primary().text("Save Contact"),
                        pb -> {
                            String name  = pb.getValue(PROP_NAME);
                            String email = pb.getValue(PROP_EMAIL);
                            showSuccess("Saved: " + name);
                            result.setText("✓ propertiesDiv — %s <%s>".formatted(name, email));
                        })
                .clearButton(btn -> btn.text("Reset"))
                .cancelButton(btn -> btn.text("Cancel"),
                        () -> result.setText("✗ Cancelled"))
                .build();

        var wrapper = new Div(panel, result);
        return new DemoExample("11 — propertiesDiv (PropertySet + Div CSS-grid, 4 cols desktop)", wrapper, """
                // Same as properties() but uses a CSS-grid Div instead of FormLayout.
                // Combine with responsiveSteps() and stretchLastRow() as with beanDiv().

                EntityFormPanel<PropertyBox> panel = EntityFormPanel.propertiesDiv(CONTACT_SET)
                    .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(4))
                    .stretchLastRow(true)
                    .saveButton(
                        btn -> btn.primary().text("Save Contact"),
                        pb -> datastore.save(TARGET, pb))
                    .clearButton(btn -> btn.text("Reset"))
                    .cancelButton(btn -> btn.text("Cancel"), () -> navigator.back())
                    .build();

                // Varargs variant:
                EntityFormPanel.propertiesDiv(PROP_NAME, PROP_EMAIL, PROP_PHONE)
                    .responsiveSteps(steps -> steps.mobile(1).desktop(3))
                    .saveButton(btn -> btn.primary().text("Save"), pb -> datastore.save(TARGET, pb))
                    .clearButton(btn -> btn.text("Reset"))
                    .build();
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 13 — noFooter() / wizard mode: external validate() trigger
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample noFooterWizardExample() {
        var result = resultSpan();

        var panel = EntityFormPanel.<Employee>bean(Employee.class)
                .noFooter()
                .build();

        var nextBtn = new Button("Next (validate externally)", VaadinIcon.ARROW_FORWARD.create());
        nextBtn.addClickListener(e -> {
            if (panel.validate()) {
                Employee emp = (Employee) ((com.holonplatform.vaadin.flow.components.BeanPropertyInputForm<?>) panel.getForm()).getBean();
                showSuccess("Valid — advancing to next step: " + emp.getFirstName());
                result.setText("✓ Step valid: %s %s".formatted(emp.getFirstName(), emp.getLastName()));
            } else {
                result.setText("✗ Validation failed — inline errors shown above");
            }
        });

        var wrapper = new Div(
                new Paragraph("The panel has no built-in footer. A wizard 'Next' button drives " +
                        "validation via panel.validate()."),
                panel,
                nextBtn,
                result);
        return new DemoExample("13 — noFooter() / wizard mode (external validate())", wrapper, """
                // Use noFooter() in multi-step wizards where navigation is driven externally.
                // The panel renders the form only — no Save/Clear buttons.
                // Call panel.validate() from the wizard's Next button.

                EntityFormPanel<Employee> panel = EntityFormPanel.<Employee>bean(Employee.class)
                    .noFooter()
                    .build();

                // In a wizard Next handler:
                nextBtn.addClickListener(e -> {
                    if (panel.validate()) {
                        // form is valid — advance to next step
                        Employee emp = (Employee) ((BeanPropertyInputForm<?>) panel.getForm()).getBean();
                        wizardFrame.next();
                    }
                    // validation failed → inline errors are already displayed
                });

                // Or use panel.getForm().getValue() directly (throws ValidationException on failure):
                try {
                    Employee emp = beanForm.getBean();   // cast panel.getForm()
                    wizardFrame.next();
                } catch (Validator.ValidationException ex) {
                    // inline errors already shown — just swallow
                }
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 14 — readOnly() builder flag: display-only form, no footer
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample readOnlyBuilderFlagExample() {

        var sensor = new ProductDetail(
                "PT-SEN-T2 is a ruggedized industrial temperature sensor with IP67-rated stainless steel " +
                "housing. Range −40°C to +125°C, ±0.5°C accuracy, 4–20 mA analog output. M12 connector, " +
                "2 m cable. Designed for harsh environments including outdoor enclosures and wash-down areas.",
                "PrahaTech",
                "Sensors > Industrial",
                "8594214082317",
                "9025.19",
                "each (ea)",
                "180 g · Ø 18 × 110 mm");

        // .readOnly() implies noFooter() — saveButton/clearButton are not required.
        // All inputs are set to readonly on build; the footer is suppressed entirely.
        // Description is explicitly bound to a TextArea so the full text is visible.
        var panel = EntityFormPanel.<ProductDetail>beanDiv(ProductDetail.class)
                .properties("description", "brand", "category",
                             "barcode", "hsCode", "unitOfMeasure", "weightDimensions")
                .responsiveSteps(steps -> steps.mobile(1).desktop(2))
                .bind("description", Input.stringArea().build())
                .readOnly()
                .build();

        panel.setBean(sensor);   // populate fields for display

        return new DemoExample("14 — readOnly() builder flag (display-only, no footer)", panel, """
                // .readOnly() puts every input into readonly mode and hides the footer.
                // saveButton() and clearButton() are NOT required when .readOnly() is set.
                // Bind description to TextArea so the full text wraps across multiple lines.
                // @Caption annotations on the bean supply the labels automatically.

                ProductDetail sensor = loadFromDatabase(id);

                EntityFormPanel<ProductDetail> panel =
                    EntityFormPanel.<ProductDetail>beanDiv(ProductDetail.class)
                        .properties("description", "brand", "category",
                                    "barcode", "hsCode", "unitOfMeasure", "weightDimensions")
                        .responsiveSteps(steps -> steps.mobile(1).desktop(2))
                        .bind("description", Input.stringArea().build())  // multiline display
                        .readOnly()          // ← all inputs readonly, footer hidden
                        .build();

                panel.setBean(sensor);       // populate for display
                """);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Example 15 — setReadOnly(boolean): runtime toggle between edit and view
    // ─────────────────────────────────────────────────────────────────────────

    private DemoExample readOnlyRuntimeToggleExample() {
        var result = resultSpan();

        var record = new ProductDetail(
                "Compact industrial pressure transmitter with 4–20 mA output and IO-Link interface. " +
                "Stainless steel wetted parts, M12 process connection. IP67 protection class.",
                "SensoTech",
                "Sensors > Pressure",
                "4012345678901",
                "9026.20",
                "piece (pce)",
                "95 g · 55 × 28 mm");

        // Build the panel with a real footer — we will toggle readonly at runtime.
        // Use an array so the save-action lambda can capture the panel reference.
        @SuppressWarnings("unchecked")
        EntityFormPanel<ProductDetail>[] panelRef   = new EntityFormPanel[1];
        Button[]                          toggleRef  = new Button[1];

        EntityFormPanel<ProductDetail> panel = EntityFormPanel.<ProductDetail>beanDiv(ProductDetail.class)
                .properties("description", "brand", "category",
                             "barcode", "hsCode", "unitOfMeasure", "weightDimensions")
                .responsiveSteps(steps -> steps.mobile(1).desktop(2))
                .bind("description", Input.stringArea().build())
                .saveButton(btn -> btn.primary().text("Save Changes"),
                        saved -> {
                            showSuccess("Saved: " + saved.getBrand() + " — " + saved.getCategory());
                            result.setText("✓ Saved — switching back to read-only view");
                            panelRef[0].setReadOnly(true);
                            if (toggleRef[0] != null) toggleRef[0].setText("✏ Edit");
                        })
                .clearButton(btn -> btn.text("Reset"))
                .build();

        panelRef[0] = panel;

        panel.setBean(record);      // pre-populate
        panel.setReadOnly(true);    // start in view mode

        Button toggleBtn = new Button("✏ Edit", VaadinIcon.EDIT.create());
        toggleRef[0] = toggleBtn;
        toggleBtn.addClickListener(e -> {
            boolean nowReadOnly = !panel.isReadOnly();
            panel.setReadOnly(nowReadOnly);
            toggleBtn.setText(nowReadOnly ? "✏ Edit" : "✖ Cancel Edit");
            result.setText(nowReadOnly
                    ? "(switched to view mode)"
                    : "(edit mode — modify fields and click Save Changes)");
        });

        var wrapper = new Div(
                new Paragraph("Click Edit to enter edit mode. Save will persist and return to view mode."),
                toggleBtn,
                panel,
                result);

        return new DemoExample("15 — setReadOnly(boolean): runtime edit ↔ view toggle", wrapper, """
                // Build the panel normally (with footer buttons).
                EntityFormPanel<ProductDetail> panel =
                    EntityFormPanel.<ProductDetail>beanDiv(ProductDetail.class)
                        .properties("description", "brand", "category",
                                    "barcode", "hsCode", "unitOfMeasure", "weightDimensions")
                        .responsiveSteps(steps -> steps.mobile(1).desktop(2))
                        .autoLabels(true)
                        .saveButton(btn -> btn.primary().text("Save Changes"),
                            saved -> {
                                service.save(saved);
                                panel.setReadOnly(true);   // back to view mode after save
                            })
                        .clearButton(btn -> btn.text("Reset"))
                        .build();

                panel.setBean(record);      // pre-populate with existing data
                panel.setReadOnly(true);    // start in view mode

                // An Edit button toggles the mode:
                editBtn.addClickListener(e -> {
                    boolean isReadOnly = panel.isReadOnly();
                    panel.setReadOnly(!isReadOnly);
                    editBtn.setText(isReadOnly ? "✖ Cancel Edit" : "✏ Edit");
                });
                """);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Span resultSpan() {
        var s = new Span("(result will appear here)");
        s.addClassName("demo-result-span");
        return s;
    }

    private static void showSuccess(String msg) {
        var n = Notification.show(msg, 3000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showInfo(String msg) {
        var n = Notification.show(msg, 2500, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }
}

