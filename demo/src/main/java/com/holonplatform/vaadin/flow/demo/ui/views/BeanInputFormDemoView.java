package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.Validator;
import com.holonplatform.core.beans.Identifier;
import com.holonplatform.core.beans.Sequence;
import com.holonplatform.core.beans.Version;
import com.holonplatform.core.i18n.Caption;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


/**
 * Demo page for {@link BeanPropertyInputForm}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic auto-introspected form (FormLayout) — submit + getBean()</li>
 *   <li>setBean() / getBean() round-trip</li>
 *   <li>excludeFields() — hide specific fields from the form</li>
 *   <li>readOnlyFields() — mark individual fields as read-only</li>
 *   <li>configure() escape hatch — content validators via PropertyInputFormBuilder</li>
 *   <li>@Identifier + @Caption annotations — auto-hidden ID, custom labels</li>
 *   <li>@Identifier + @Version — auto-hide behaviour, round-trip value preservation, opt-in reveal</li>
 *   <li>VerticalLayout layout variant</li>
 * </ol>
 */
@PageTitle("BeanInputForm – Holon Demo")
@Route(value = "bean-input-form", layout = DemoMainLayout.class)
public class BeanInputFormDemoView extends Div {

    // ── Bean 1: basic employee (no annotations) ───────────────────────────────

    public static final class Employee {
        private String name;
        private String department;
        private String role;
        private double salary;
        private boolean active;

        public Employee() {}

        public Employee(String name, String department, String role, double salary, boolean active) {
            this.name = name;
            this.department = department;
            this.role = role;
            this.salary = salary;
            this.active = active;
        }

        public String getName()            { return name; }
        public void   setName(String v)    { this.name = v; }
        public String getDepartment()      { return department; }
        public void   setDepartment(String v) { this.department = v; }
        public String getRole()            { return role; }
        public void   setRole(String v)    { this.role = v; }
        public double getSalary()          { return salary; }
        public void   setSalary(double v)  { this.salary = v; }
        public boolean isActive()          { return active; }
        public void   setActive(boolean v) { this.active = v; }
    }

    // ── Bean 2: annotated customer (uses @Identifier, @Caption, @Sequence) ───

    public static final class Customer {
        @Identifier
        private long id;

        @Sequence(1)
        @Caption("Full Name")
        private String fullName;

        @Sequence(2)
        @Caption("Email Address")
        private String email;

        @Sequence(3)
        @Caption("Phone")
        private String phone;

        @Sequence(4)
        @Caption("Account Tier")
        private String tier;

        public Customer() {}

        public Customer(long id, String fullName, String email, String phone, String tier) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.tier = tier;
        }

        public long   getId()               { return id; }
        public void   setId(long v)         { this.id = v; }
        public String getFullName()         { return fullName; }
        public void   setFullName(String v) { this.fullName = v; }
        public String getEmail()            { return email; }
        public void   setEmail(String v)    { this.email = v; }
        public String getPhone()            { return phone; }
        public void   setPhone(String v)    { this.phone = v; }
        public String getTier()             { return tier; }
        public void   setTier(String v)     { this.tier = v; }
    }

    // ── Bean 3: versioned entity (@Identifier + @Version) ────────────────────

    public static final class Order {
        /** Hidden by default — @Identifier; preserved in PropertyBox but not rendered. */
        @Identifier
        @Sequence(1)
        private Long id;

        /** Hidden by default — @Version; preserved for optimistic-locking, not rendered. */
        @Version
        @Sequence(2)
        private Long version;

        @Sequence(3)
        @Caption("Customer Name")
        private String customerName;

        @Sequence(4)
        @Caption("Product")
        private String product;

        @Sequence(5)
        @Caption("Quantity")
        private int quantity;

        public Order() {}

        public Order(Long id, Long version, String customerName, String product, int quantity) {
            this.id = id;
            this.version = version;
            this.customerName = customerName;
            this.product = product;
            this.quantity = quantity;
        }

        public Long   getId()                   { return id; }
        public void   setId(Long v)             { this.id = v; }
        public Long   getVersion()              { return version; }
        public void   setVersion(Long v)        { this.version = v; }
        public String getCustomerName()         { return customerName; }
        public void   setCustomerName(String v) { this.customerName = v; }
        public String getProduct()              { return product; }
        public void   setProduct(String v)      { this.product = v; }
        public int    getQuantity()             { return quantity; }
        public void   setQuantity(int v)        { this.quantity = v; }
    }

    // ── Bean 4: product (co-located validator constants + @Caption) ───────────

    /**
     * Validation rules are declared as {@code static final} constants directly on the
     * bean class so the form builder can reference them without duplicating rule logic.
     */
    public static final class Product {

        /** Name must not be blank. */
        static final Validator<String> NAME_RULE = Validator.notBlank();
        /** SKU must not be blank. */
        static final Validator<String> SKU_RULE  = Validator.notBlank();
        /** Price must be greater than zero. */
        static final Validator<Double> PRICE_RULE =
                Validator.min(Double.valueOf("0.01"));
        /** Quantity must be zero or positive. */
        static final Validator<Integer> QTY_RULE =
                Validator.min(Integer.valueOf(0));

        @Sequence(1) @Caption("Product Name")
        private String name;
        @Sequence(2) @Caption("Description")
        private String description;
        @Sequence(3) @Caption("Unit Price (€)")
        private double price;
        @Sequence(4) @Caption("Stock Quantity")
        private int    quantity;
        @Sequence(5) @Caption("SKU Code")
        private String sku;

        public Product() {}

        public String getName()            { return name; }
        public void   setName(String v)    { this.name = v; }
        public String getDescription()     { return description; }
        public void   setDescription(String v) { this.description = v; }
        public double getPrice()           { return price; }
        public void   setPrice(double v)   { this.price = v; }
        public int    getQuantity()        { return quantity; }
        public void   setQuantity(int v)   { this.quantity = v; }
        public String getSku()             { return sku; }
        public void   setSku(String v)     { this.sku = v; }
    }

    // ── Bean 5: person info (6 @Caption fields for responsive column demo) ────

    public static final class PersonInfo {

        /** First Name must not be blank — referenced directly from view code. */
        static final Validator<String> FIRST_NAME_RULE = Validator.notBlank();
        /** Last Name must not be blank. */
        static final Validator<String> LAST_NAME_RULE  = Validator.notBlank();
        /** Email must not be blank. */
        static final Validator<String> EMAIL_RULE      = Validator.notBlank();

        @Sequence(1) @Caption("First Name")
        private String firstName;
        @Sequence(2) @Caption("Last Name")
        private String lastName;
        @Sequence(3) @Caption("Email Address")
        private String email;
        @Sequence(4) @Caption("Phone Number")
        private String phone;
        @Sequence(5) @Caption("City")
        private String city;
        @Sequence(6) @Caption("Country")
        private String country;

        public PersonInfo() {}

        public String getFirstName()           { return firstName; }
        public void   setFirstName(String v)   { this.firstName = v; }
        public String getLastName()            { return lastName; }
        public void   setLastName(String v)    { this.lastName = v; }
        public String getEmail()               { return email; }
        public void   setEmail(String v)       { this.email = v; }
        public String getPhone()               { return phone; }
        public void   setPhone(String v)       { this.phone = v; }
        public String getCity()                { return city; }
        public void   setCity(String v)        { this.city = v; }
        public String getCountry()             { return country; }
        public void   setCountry(String v)     { this.country = v; }
    }

    // ── Constructor ──────────────────────────────────────────────────────────

    public BeanInputFormDemoView() {
        addClassName("app-view");

        var title = new H1("BeanPropertyInputForm");

        var desc = new Paragraph(
                "BeanPropertyInputForm auto-introspects a Java bean class and renders matching "
                + "Input fields (String → TextField, double → NumberField, boolean → Checkbox, etc.). "
                + "It respects @Identifier / @Version / @Caption / @Sequence annotations and provides "
                + "setBean() / getBean() for zero-boilerplate data binding.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicFormLayoutExample());
        examples.add(roundTripExample());
        examples.add(excludeFieldsExample());
        examples.add(readOnlyFieldsExample());
        examples.add(configureValidatorsExample());
        examples.add(annotatedBeanExample());
        examples.add(identifierVersionExample());
        examples.add(verticalLayoutExample());
        examples.add(validationExample());
        examples.add(responsiveFormLayoutExample());
        examples.add(responsiveDivFormExample());
        examples.add(keyboardFormExample());
        examples.add(enterNavigationExample());

        add(title, desc, examples);
    }

    // ── Example 1 – Basic FormLayout ─────────────────────────────────────────

    private DemoExample basicFormLayoutExample() {
        var form = BeanPropertyInputForm.formLayout(Employee.class)
                .build();

        var result = new Span("Submit to see bean values");

        var submitBtn = new Button("Submit", e -> {
            try {
                Employee emp = form.getBean();
                result.setText(String.format("%s | %s | %s | $%.2f | active=%b",
                        emp.getName(), emp.getDepartment(), emp.getRole(),
                        emp.getSalary(), emp.isActive()));
            } catch (Exception ex) {
                showError("Validation failed: " + ex.getMessage());
            }
        });

        var clearBtn = new Button("Clear", e -> form.clear());

        var container = new Div(form.getComponent(), submitBtn, clearBtn, result);

        return new DemoExample("Basic FormLayout (auto-introspected)", container, """
                // BeanPropertyInputForm introspects the bean and renders one
                // Input per field: String → TextField, double → NumberField, boolean → Checkbox.
                var form = BeanPropertyInputForm.formLayout(Employee.class)
                        .build();

                // Read values back as a bean (validates before returning):
                Employee emp = form.getBean();

                // Skip validation:
                Employee raw = form.getBean(false);

                // Clear all inputs:
                form.clear();
                """);
    }

    // ── Example 2 – setBean / getBean round-trip ─────────────────────────────

    private DemoExample roundTripExample() {
        var form = BeanPropertyInputForm.formLayout(Employee.class)
                .build();

        var result = new Span("Load sample data, edit, then read back");

        var loadBtn = new Button("Load Sample", e -> {
            form.setBean(new Employee("Jane Smith", "Engineering", "Senior Developer", 95_000.0, true));
            showSuccess("Bean loaded into form");
        });

        var readBtn = new Button("Read Bean", e -> {
            try {
                Employee emp = form.getBean(false); // skip validation for demo
                result.setText(String.format("Read: %s, %s, $%.2f",
                        emp.getName(), emp.getDepartment(), emp.getSalary()));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        var container = new Div(form.getComponent(), loadBtn, readBtn, result);

        return new DemoExample("setBean / getBean round-trip", container, """
                // Pre-populate all inputs from an existing bean instance:
                form.setBean(new Employee("Jane Smith", "Engineering", "Senior Developer", 95_000.0, true));

                // Read back (false = skip validation):
                Employee updated = form.getBean(false);

                // Read back with validation (throws ValidationException on failure):
                Employee validated = form.getBean();
                """);
    }

    // ── Example 3 – excludeFields ─────────────────────────────────────────────

    private DemoExample excludeFieldsExample() {
        // Exclude 'salary' and 'active' — they will not appear in the form at all.
        var form = BeanPropertyInputForm.formLayout(Employee.class)
                .excludeFields("salary", "active")
                .build();

        var result = new Span("Submit to see values");

        var submitBtn = new Button("Submit", e -> {
            try {
                Employee emp = form.getBean(false);
                result.setText(String.format("%s | %s | %s",
                        emp.getName(), emp.getDepartment(), emp.getRole()));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        var container = new Div(form.getComponent(), submitBtn, result);

        return new DemoExample("excludeFields() — hide salary & active", container, """
                // 'salary' and 'active' are removed entirely from the form;
                // they are neither rendered nor tracked in the PropertyBox.
                var form = BeanPropertyInputForm.formLayout(Employee.class)
                        .excludeFields("salary", "active")
                        .build();
                """);
    }

    // ── Example 4 – readOnlyFields ────────────────────────────────────────────

    private DemoExample readOnlyFieldsExample() {
        // 'department' and 'role' are shown but cannot be edited.
        var form = BeanPropertyInputForm.formLayout(Employee.class)
                .readOnlyFields("department", "role")
                .build();

        form.setBean(new Employee("Bob Johnson", "Product", "Staff Engineer", 110_000.0, true));

        var container = new Div(form.getComponent());

        return new DemoExample("readOnlyFields() — lock department & role", container, """
                // 'department' and 'role' are rendered as read-only inputs;
                // the user can see their values but cannot change them.
                var form = BeanPropertyInputForm.formLayout(Employee.class)
                        .readOnlyFields("department", "role")
                        .build();

                // Pre-populate so the read-only values are visible:
                form.setBean(existingEmployee);
                """);
    }

    // ── Example 5 – configure() escape hatch + validators ────────────────────

    @SuppressWarnings({"unchecked", "rawtypes"})
    private DemoExample configureValidatorsExample() {
        // We need PathProperty references to attach typed validators.
        var builder = BeanPropertyInputForm.formLayout(Employee.class);

        // Resolve PathProperty for 'name' so we can attach a typed validator.
        var nameProp  = builder.property("name");
        var salaryProp = builder.property("salary");

        var form = builder
                .configure(fb -> {
                    nameProp.ifPresent(p ->
                            //noinspection unchecked,rawtypes
                            fb.withValidator((com.holonplatform.core.property.Property) p,
                                    Validator.notBlank()));
                    salaryProp.ifPresent(p ->
                            //noinspection unchecked,rawtypes
                            fb.withValidator((com.holonplatform.core.property.Property) p,
                                    Validator.min(0.0)));
                })
                .build();

        var result = new Span("Submit to validate");

        var submitBtn = new Button("Submit", e -> {
            if (form.isValid()) {
                result.setText("Valid!");
            } else {
                result.setText("Validation failed – check the fields.");
            }
        });

        var container = new Div(form.getComponent(), submitBtn, result);

        return new DemoExample("configure() — content per-field validators", container, """
                var builder = BeanPropertyInputForm.formLayout(Employee.class);

                // Resolve PathProperty references before calling configure():
                var nameProp   = builder.property("name");
                var salaryProp = builder.property("salary");

                var form = builder
                        .configure(fb -> {
                            nameProp.ifPresent(p -> fb.withValidator(p, Validator.notBlank()));
                            salaryProp.ifPresent(p -> fb.withValidator(p, Validator.min(0.0)));
                        })
                        .build();

                // isValid() triggers all validators:
                boolean valid = form.isValid();
                """);
    }

    // ── Example 6 – @Identifier / @Caption annotations ───────────────────────

    private DemoExample annotatedBeanExample() {
        // By default @Identifier field 'id' is hidden. showIdentifiers() reveals it.
        var formDefault = BeanPropertyInputForm.formLayout(Customer.class)
                .build();

        var formWithId = BeanPropertyInputForm.formLayout(Customer.class)
                .showIdentifiers()
                .build();

        formWithId.setBean(new Customer(42L, "Alice Martin", "alice@example.com", "+44 700 000", "Gold"));

        var result = new Span("Submit to read customer");

        var submitBtn = new Button("Submit (default — id hidden)", e -> {
            try {
                Customer c = formDefault.getBean(false);
                result.setText(String.format("%s | %s | %s",
                        c.getFullName(), c.getEmail(), c.getTier()));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        var submitWithIdBtn = new Button("Submit (showIdentifiers)", e -> {
            try {
                Customer c = formWithId.getBean(false);
                result.setText(String.format("id=%d | %s | %s",
                        c.getId(), c.getFullName(), c.getTier()));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        var container = new Div(
                new Span("Default (id hidden):"), formDefault.getComponent(),
                new Span("showIdentifiers():"), formWithId.getComponent(),
                submitBtn, submitWithIdBtn, result);

        return new DemoExample("@Identifier + @Caption + @Sequence annotations", container, """
                // @Identifier 'id' is hidden by default; @Caption sets field labels;
                // @Sequence controls render order.
                public static final class Customer {
                    @Identifier
                    private long id;

                    @Sequence(1) @Caption("Full Name")
                    private String fullName;

                    @Sequence(2) @Caption("Email Address")
                    private String email;
                    // ...
                }

                // Default — id not rendered:
                var form = BeanPropertyInputForm.formLayout(Customer.class).build();

                // Opt-in to show @Identifier fields:
                var formWithId = BeanPropertyInputForm.formLayout(Customer.class)
                        .showIdentifiers()
                        .build();
                """);
    }

    // ── Example 7 – @Identifier and @Version behaviour ───────────────────────

    private DemoExample identifierVersionExample() {
        // ── Panel A: default — id + version hidden ────────────────────────────
        var formDefault = BeanPropertyInputForm.formLayout(Order.class)
                .build();

        // ── Panel B: showIdentifiers() only ───────────────────────────────────
        var formShowId = BeanPropertyInputForm.formLayout(Order.class)
                .showIdentifiers()
                .build();

        // ── Panel C: showVersions() only ──────────────────────────────────────
        var formShowVersion = BeanPropertyInputForm.formLayout(Order.class)
                .showVersions()
                .build();

        // ── Panel D: showIdentifiers() + showVersions() ───────────────────────
        var formShowBoth = BeanPropertyInputForm.formLayout(Order.class)
                .showIdentifiers()
                .showVersions()
                .build();

        // Pre-populate all four forms with the same order
        var sample = new Order(1001L, 3L, "Alice Martin", "Widget Pro", 5);
        formDefault.setBean(sample);
        formShowId.setBean(sample);
        formShowVersion.setBean(sample);
        formShowBoth.setBean(sample);

        // Round-trip proof: even when id/version are hidden their values survive getBean()
        var result = new Span("Click a button to read back the bean");

        var readDefaultBtn = new Button("Read (default)", e -> {
            Order o = formDefault.getBean(false);
            result.setText(String.format("[default]  id=%s  version=%s  qty=%d",
                    o.getId(), o.getVersion(), o.getQuantity()));
        });

        var readShowIdBtn = new Button("Read (showIdentifiers)", e -> {
            Order o = formShowId.getBean(false);
            result.setText(String.format("[showId]   id=%s  version=%s  qty=%d",
                    o.getId(), o.getVersion(), o.getQuantity()));
        });

        var readShowVersionBtn = new Button("Read (showVersions)", e -> {
            Order o = formShowVersion.getBean(false);
            result.setText(String.format("[showVer]  id=%s  version=%s  qty=%d",
                    o.getId(), o.getVersion(), o.getQuantity()));
        });

        var readBothBtn = new Button("Read (show both)", e -> {
            Order o = formShowBoth.getBean(false);
            result.setText(String.format("[both]     id=%s  version=%s  qty=%d",
                    o.getId(), o.getVersion(), o.getQuantity()));
        });

        var label = new Span(
                "All four forms are pre-populated with id=1001, version=3, qty=5. " +
                "Notice how id and version survive getBean() even when their inputs are hidden.");

        var container = new Div(
                new Span("Default (id + version hidden):"), formDefault.getComponent(),
                new Span("showIdentifiers() — id visible:"), formShowId.getComponent(),
                new Span("showVersions() — version visible:"), formShowVersion.getComponent(),
                new Span("showIdentifiers() + showVersions():"), formShowBoth.getComponent(),
                readDefaultBtn, readShowIdBtn, readShowVersionBtn, readBothBtn,
                label, result);

        return new DemoExample("@Identifier + @Version — auto-hide and opt-in reveal", container, """
                // @Identifier marks the primary-key field.
                // @Version marks the optimistic-lock counter field.
                // Both are HIDDEN by default — their values are preserved in the
                // underlying PropertyBox but no Input is rendered.
                public static final class Order {
                    @Identifier
                    @Sequence(1)
                    private Long id;

                    @Version
                    @Sequence(2)
                    private Long version;

                    @Sequence(3) @Caption("Customer Name")
                    private String customerName;
                    // ...
                }

                // Default — id and version not rendered but values survive round-trip:
                var form = BeanPropertyInputForm.formLayout(Order.class).build();
                form.setBean(order);
                Order saved = form.getBean(false);
                // saved.getId()     == order.getId()     ✓  (preserved even though hidden)
                // saved.getVersion()== order.getVersion()✓

                // Opt-in: show the @Identifier input field:
                BeanPropertyInputForm.formLayout(Order.class)
                        .showIdentifiers()
                        .build();

                // Opt-in: show the @Version input field:
                BeanPropertyInputForm.formLayout(Order.class)
                        .showVersions()
                        .build();

                // Show both:
                BeanPropertyInputForm.formLayout(Order.class)
                        .showIdentifiers()
                        .showVersions()
                        .build();
                """);
    }

    // ── Example 8 – VerticalLayout variant ───────────────────────────────────

    private DemoExample verticalLayoutExample() {
        var form = BeanPropertyInputForm.verticalLayout(Employee.class)
                .excludeFields("active")
                .build();

        var result = new Span("Submit to see values");

        var submitBtn = new Button("Submit", e -> {
            try {
                Employee emp = form.getBean(false);
                result.setText(String.format("%s | %s | $%.2f",
                        emp.getName(), emp.getRole(), emp.getSalary()));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        var container = new Div(form.getComponent(), submitBtn, result);

        return new DemoExample("VerticalLayout variant", container, """
                // Use verticalLayout() instead of formLayout() to stack inputs top-to-bottom.
                // horizontalLayout() is also available for inline side-by-side layouts.
                var form = BeanPropertyInputForm.verticalLayout(Employee.class)
                        .excludeFields("active")
                        .build();
                """);
    }

    // ── Example 9 – Live inline validation from entity rules ─────────────────

    /**
     * Demonstrates:
     * <ul>
     *   <li>Validator constants co-located on the bean class ({@code Product.NAME_RULE} etc.)</li>
     *   <li>{@code @Caption} annotations driving field labels automatically</li>
     *   <li>{@code validateOnValueChange(true)} for live inline error messages</li>
     *   <li>Submit shows all errors at once or prints the valid bean</li>
     * </ul>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private DemoExample validationExample() {
        var builder = BeanPropertyInputForm.formLayout(Product.class);

        // Resolve PathProperty references for each field that carries a validator rule.
        var nameProp     = builder.property("name");
        var priceProp    = builder.property("price");
        var quantityProp = builder.property("quantity");
        var skuProp      = builder.property("sku");

        var form = builder
                // Apply per-field validators sourced from the bean's own rule constants.
                .configure(fb -> {
                    nameProp    .ifPresent(p -> fb.withValidator((Property) p, Product.NAME_RULE));
                    priceProp   .ifPresent(p -> fb.withValidator((Property) p, Product.PRICE_RULE));
                    quantityProp.ifPresent(p -> fb.withValidator((Property) p, Product.QTY_RULE));
                    skuProp     .ifPresent(p -> fb.withValidator((Property) p, Product.SKU_RULE));
                })
                // Validate each field as the user types — shows inline error messages immediately.
                .configure(fb -> fb.validateOnValueChange(true))
                .build();

        var result = new Span("Fill the form and click Save");

        var saveBtn = new Button("Save", e -> {
            try {
                Product p = form.getBean();   // validates all fields; throws on failure
                result.setText(String.format("Saved: %s | €%.2f | qty %d | SKU %s",
                        p.getName(), p.getPrice(), p.getQuantity(), p.getSku()));
                showSuccess("Product saved successfully");
            } catch (Validator.ValidationException ex) {
                result.setText("Fix errors before saving.");
                showError(ex.getMessage());
            }
        });

        var clearBtn = new Button("Clear", e -> {
            form.clear();
            result.setText("Fill the form and click Save");
        });

        var actions = ResponsiveDiv.flex().row().gapS().add(saveBtn, clearBtn).build();
        var container = new Div(form.getComponent(), actions, result);

        return new DemoExample(
                "Live Validation — rules from entity class + @Caption labels",
                container, """
                        // ── Validator rules co-located on the entity ─────────────────────────────
                        public static final class Product {
                            static final Validator<String>  NAME_RULE  = Validator.notBlank();
                            static final Validator<String>  SKU_RULE   = Validator.notBlank();
                            static final Validator<Double>  PRICE_RULE = Validator.min(Double.valueOf("0.01"));
                            static final Validator<Integer> QTY_RULE   = Validator.min(Integer.valueOf(0));

                            @Sequence(1) @Caption("Product Name")   private String name;
                            @Sequence(3) @Caption("Unit Price (€)") private double price;
                            // ...
                        }

                        // ── Form builder: register entity rules + enable inline validation ────────
                        var builder      = BeanPropertyInputForm.formLayout(Product.class);
                        var nameProp     = builder.property("name");
                        var priceProp    = builder.property("price");
                        var quantityProp = builder.property("quantity");
                        var skuProp      = builder.property("sku");

                        var form = builder
                            .configure(fb -> {
                                nameProp    .ifPresent(p -> fb.withValidator((Property) p, Product.NAME_RULE));
                                priceProp   .ifPresent(p -> fb.withValidator((Property) p, Product.PRICE_RULE));
                                quantityProp.ifPresent(p -> fb.withValidator((Property) p, Product.QTY_RULE));
                                skuProp     .ifPresent(p -> fb.withValidator((Property) p, Product.SKU_RULE));
                            })
                            .configure(fb -> fb.validateOnValueChange(true))  // live inline errors
                            .build();

                        // Save — getBean() re-validates everything; throws ValidationException on failure.
                        try {
                            Product p = form.getBean();
                        } catch (ValidationException ex) {
                            ex.getValidationMessages().forEach(m -> showError(m.getMessage()));
                        }
                        """);
    }

    // ── Example 10 – Responsive FormLayout (1 → 2 → 3 columns) ──────────────

    /**
     * Shows Vaadin {@link FormLayout}'s native responsive column capability via
     * {@link FormLayout#setResponsiveSteps}. After the form is built, the underlying
     * {@link FormLayout} is retrieved and configured with three breakpoints:
     * <ul>
     *   <li>0 px   → 1 column  (narrow mobile)</li>
     *   <li>500 px → 2 columns (tablet)</li>
     *   <li>900 px → 3 columns (desktop)</li>
     * </ul>
     * Resize the browser to watch the layout reflow.
     */
    private DemoExample responsiveFormLayoutExample() {
        var form = BeanPropertyInputForm.formLayout(PersonInfo.class).build();

        // Configure responsive column steps on the already-built FormLayout.
        // This is the FormLayout-native approach — no extra CSS classes needed.
        ((FormLayout) form.getComponent()).setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",     1),   // mobile:  1 col
                new FormLayout.ResponsiveStep("500px", 2),   // tablet:  2 cols
                new FormLayout.ResponsiveStep("900px", 3)    // desktop: 3 cols
        );

        var result = new Span("Submit to see values");
        var submitBtn = new Button("Submit", e -> {
            PersonInfo p = form.getBean(false);
            result.setText(String.format("%s %s — %s — %s, %s",
                    p.getFirstName(), p.getLastName(), p.getEmail(), p.getCity(), p.getCountry()));
        });
        var clearBtn = new Button("Clear", e -> form.clear());

        var hint = new Span("↔ Resize below 500 px / 900 px to see the column count change.");

        var container = new Div(hint, form.getComponent(), submitBtn, clearBtn, result);

        return new DemoExample(
                "Responsive FormLayout — 1 → 2 → 3 columns via ResponsiveStep (resize to see)",
                container, """
                        // Build the form normally, then apply responsive steps to the FormLayout.
                        var form = BeanPropertyInputForm.formLayout(PersonInfo.class).build();

                        ((FormLayout) form.getComponent()).setResponsiveSteps(
                            new FormLayout.ResponsiveStep("0",     1),   // narrow mobile: 1 col
                            new FormLayout.ResponsiveStep("500px", 2),   // tablet: 2 cols
                            new FormLayout.ResponsiveStep("900px", 3)    // desktop: 3 cols
                        );

                        // The @Caption annotations on the bean drive field labels automatically:
                        public static final class PersonInfo {
                            @Sequence(1) @Caption("First Name")    private String firstName;
                            @Sequence(2) @Caption("Last Name")     private String lastName;
                            @Sequence(3) @Caption("Email Address") private String email;
                            @Sequence(4) @Caption("Phone Number")  private String phone;
                            @Sequence(5) @Caption("City")          private String city;
                            @Sequence(6) @Caption("Country")       private String country;
                        }
                        """);
    }

    // ── Example 11 – Responsive outer layout using ResponsiveDiv ─────────────

    /**
     * Demonstrates using {@link ResponsiveDiv} as the <em>outer</em> layout container
     * to place the form alongside a live-updating summary panel:
     * <ul>
     *   <li><b>Mobile</b>: form stacked above summary (flex-col)</li>
     *   <li><b>Desktop</b>: form and summary side-by-side (flex-row)</li>
     * </ul>
     * The live summary updates on every field change via
     * {@link BeanPropertyInputForm#addValueChangeListener}.
     */
    private DemoExample responsiveDivFormExample() {
        var form = BeanPropertyInputForm.formLayout(PersonInfo.class).build();

        // ── Live summary rows ─────────────────────────────────────────────────
        var snFirstName = new Span("—"); var snLastName = new Span("—");
        var snEmail     = new Span("—"); var snPhone    = new Span("—");
        var snCity      = new Span("—"); var snCountry  = new Span("—");

        Runnable refreshSummary = () -> {
            PersonInfo p = form.getBean(false);
            snFirstName.setText(p.getFirstName()  != null ? p.getFirstName()  : "—");
            snLastName .setText(p.getLastName()   != null ? p.getLastName()   : "—");
            snEmail    .setText(p.getEmail()       != null ? p.getEmail()       : "—");
            snPhone    .setText(p.getPhone()       != null ? p.getPhone()       : "—");
            snCity     .setText(p.getCity()        != null ? p.getCity()        : "—");
            snCountry  .setText(p.getCountry()     != null ? p.getCountry()     : "—");
        };

        // addValueChangeListener on the form fires only on programmatic setValue().
        // For live user-interaction updates, attach to each individual Input instead.
        form.getElements().forEach(input -> input.addValueChangeListener(e -> refreshSummary.run()));

        // ── Summary card layout ───────────────────────────────────────────────
        // Build summary rows as label + value pairs
        var summaryRows = ResponsiveDiv.flex().column().gapXS().build();
        record Row(String label, Span value) {}
        var rows = new Row[]{
                new Row("First Name", snFirstName), new Row("Last Name", snLastName),
                new Row("Email",      snEmail),     new Row("Phone",     snPhone),
                new Row("City",       snCity),      new Row("Country",   snCountry)
        };
        for (var row : rows) {
            var lbl = new Span(row.label());
            lbl.addClassName("demo-bif-summary-label");
            var val = row.value();
            val.addClassName("demo-bif-summary-value");
            var pair = ResponsiveDiv.flex().row().gapS().alignCenter().add(lbl, val).build();
            summaryRows.add(pair);
        }

        var summaryCard = ResponsiveDiv.flex().column().gapS().card().padM()
                .add(new H4("Live Preview"), summaryRows)
                .build();

        // ── Form wrapper ──────────────────────────────────────────────────────
        var clearBtn  = new Button("Clear",  e -> { form.clear(); refreshSummary.run(); });
        var submitBtn = new Button("Confirm", e -> showSuccess("Person saved!"));
        var btnRow = ResponsiveDiv.flex().row().gapS().add(submitBtn, clearBtn).build();

        var formWrapper = ResponsiveDiv.flex().column().gapS().grow()
                .add(form.getComponent(), btnRow)
                .build();

        // ── Outer responsive container ────────────────────────────────────────
        // Mobile:  form stacked above summary
        // Desktop: form on left (grows), summary card on right (fixed ~280px)
        var container = ResponsiveDiv.flex()
                .column().gapM()
                .desktop().row().gapXL().alignStart().end()
                .add(formWrapper, summaryCard)
                .build();

        return new DemoExample(
                "Responsive outer layout via ResponsiveDiv — form + live preview (resize to see)",
                container, """
                        // The form itself is a standard FormLayout.
                        var form = BeanPropertyInputForm.formLayout(PersonInfo.class).build();

                        // Re-render a summary panel on every field change.
                        form.addValueChangeListener(e -> {
                            PersonInfo p = form.getBean(false);
                            snFirstName.setText(p.getFirstName());
                            // ...
                        });

                        // Summary card — a plain ResponsiveDiv.flex().column().card()
                        var summaryCard = ResponsiveDiv.flex().column().gapS().card().padM()
                            .content(new H4("Live Preview"), summaryRows)
                            .build();

                        // Outer shell switches direction at the desktop breakpoint.
                        // Mobile:  flex-col — form above, preview below.
                        // Desktop: flex-row — form on left (grows), preview on right.
                        var container = ResponsiveDiv.flex()
                            .column().gapM()
                            .desktop().row().gapXL().alignStart().end()
                            .content(formWrapper, summaryCard)
                            .build();
                        """);
    }

    // ── Example 12 – Keyboard-centric form ───────────────────────────────────

    /**
     * Demonstrates native keyboard support for enterprise forms:
     * <ul>
     *   <li><b>Enter</b> – validates; shows success notification <em>only</em> if all
     *       required fields (First Name, Last Name, Email) pass; shows error otherwise</li>
     *   <li><b>Escape</b> – clears all fields</li>
     *   <li><b>Ctrl+S</b> – quick-saves without full validation; browser "Save page" suppressed</li>
     *   <li><b>Auto-focus</b> – first field receives focus when the section attaches</li>
     * </ul>
     * Required fields use {@code notBlank} validators wired via {@code configure()}.
     * {@code validateOnValueChange(true)} shows inline red errors while typing.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private DemoExample keyboardFormExample() {
        // ── Form with required-field validators ────────────────────────────────
        var builder       = BeanPropertyInputForm.formLayout(PersonInfo.class);
        var firstNameProp = builder.property("firstName");
        var lastNameProp  = builder.property("lastName");
        var emailProp     = builder.property("email");

        var form = builder
                .configure(fb -> {
                    firstNameProp.ifPresent(p -> fb.withValidator((Property) p, PersonInfo.FIRST_NAME_RULE));
                    lastNameProp .ifPresent(p -> fb.withValidator((Property) p, PersonInfo.LAST_NAME_RULE));
                    emailProp    .ifPresent(p -> fb.withValidator((Property) p, PersonInfo.EMAIL_RULE));
                })
                .configure(fb -> fb.validateOnValueChange(true))  // inline errors while typing
                .build();

        var statusLabel = new Span("Tab into any field — try Enter, Escape, or Ctrl+S");
        statusLabel.addClassName("demo-keyboard-hint");

        var result = new Span("No submission yet");

        // ── Auto-focus first field when the section attaches to the DOM ───────
        form.getComponent().addAttachListener(e ->
                form.getElements()
                        .findFirst()
                        .ifPresent(input -> input.getComponent().getElement()
                                .callJsFunction("focus")));

        // ── Form wrapper — keyboard shortcuts are scoped to this container ────
        var clearBtn  = new Button("Clear",  e -> { form.clear(); result.setText("Form cleared."); });
        var submitBtn = new Button("Submit", e -> submitKeyboardForm(form, result, statusLabel));
        var btnRow    = ResponsiveDiv.flex().row().gapS().add(submitBtn, clearBtn).build();

        var formWrapper = ResponsiveDiv.flex().column().gapS()
                .add(form.getComponent(), btnRow)
                .build();

        // ── Enter → submit (scoped: fires only when focus is inside formWrapper) ─
        Shortcuts.addShortcutListener(formWrapper,
                () -> submitKeyboardForm(form, result, statusLabel),
                Key.ENTER);

        // ── Escape → clear ─────────────────────────────────────────────────────
        Shortcuts.addShortcutListener(formWrapper, () -> {
            form.clear();
            result.setText("Form cleared via Escape.");
            statusLabel.setText("⌨ Escape → fields cleared");
        }, Key.ESCAPE);

        // ── Ctrl+S → save (suppress browser "Save page" dialog) ───────────────
        Shortcuts.addShortcutListener(formWrapper, () -> {
            PersonInfo p = form.getBean(false);   // skip validation — grab current values
            result.setText("Quick-saved: %s %s".formatted(
                    blankOr(p.getFirstName(), "?"), blankOr(p.getLastName(), "?")));
            statusLabel.setText("⌨ Ctrl+S → quick-saved");
            showSuccess("Quick-saved via Ctrl+S");
        }, Key.KEY_S, KeyModifier.CONTROL)
                .setBrowserDefaultAllowed(false);   // ← suppress browser "Save page"

        var container = new Div(statusLabel, formWrapper, result);

        return new DemoExample(
                "Keyboard-Centric Form — Enter / Escape / Ctrl+S shortcuts (auto-focused)",
                container, """
                        // ── Auto-focus the first field when the section attaches to the DOM ───────
                        form.getComponent().addAttachListener(e ->
                            form.getElements().findFirst()
                                .ifPresent(input -> input.getComponent().getElement()
                                    .callJsFunction("focus")));

                        // ── Keyboard shortcuts scoped to the form container ────────────────────────
                        // Scoped means: fires ONLY while keyboard focus is inside `formWrapper`.
                        // This prevents the shortcut from firing when the user is elsewhere
                        // on the page or in another browser tab.

                        // Enter → validate + submit
                        Shortcuts.addShortcutListener(formWrapper, () -> {
                            try {
                                PersonInfo p = form.getBean();   // validates; throws on failure
                                save(p);
                            } catch (Validator.ValidationException ex) {
                                showError(ex.getMessage());
                            }
                        }, Key.ENTER);

                        // Escape → clear all fields
                        Shortcuts.addShortcutListener(formWrapper,
                            () -> form.clear(), Key.ESCAPE);

                        // Ctrl+S → quick-save (skip validation, keep focus in form)
                        Shortcuts.addShortcutListener(formWrapper, () -> {
                            PersonInfo p = form.getBean(false);  // no validation
                            quickSave(p);
                        }, Key.KEY_S, KeyModifier.CONTROL)
                            .setBrowserDefaultAllowed(false);    // suppress browser "Save page"

                        // ── Tab order follows @Sequence on the bean automatically ────────────────
                        // No extra configuration needed — DOM order matches @Sequence order.
                        // To override tab order on a specific field:
                        //   form.getInput(myProp).ifPresent(input ->
                        //       ((Focusable<?>) input.getComponent()).setTabIndex(n));
                        """);
    }

    // ── Example 13 – Enter-key field traversal (built-in API) ────────────────

    /**
     * Demonstrates keyboard-first field traversal using the built-in
     * {@link com.holonplatform.vaadin.flow.components.builders.PropertyInputFormConfigurator} API:
     * <ul>
     *   <li><b>{@code enterMovesFocusToNext(true)}</b> – pressing Enter moves focus to the
     *       next visible, enabled, non-read-only input in {@link com.holonplatform.core.beans.Sequence}
     *       order; on the last field it walks the DOM to find the first focusable element
     *       after the form — typically the Submit button</li>
     *   <li><b>{@code validateOnEnterFocusMove(true)}</b> – validates the form <em>before</em>
     *       allowing focus to move; focus stays on the current field if validation fails</li>
     *   <li><b>Auto-focus</b> – first field focused on attach via {@link Focusable#focus()}</li>
     * </ul>
     * No custom helper method, no {@code KeyNotifier}, no JavaScript required —
     * two builder flags do everything.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private DemoExample enterNavigationExample() {
        var builder       = BeanPropertyInputForm.formLayout(PersonInfo.class);
        var firstNameProp = builder.property("firstName");
        var lastNameProp  = builder.property("lastName");
        var emailProp     = builder.property("email");

        var form = builder
                .configure(fb -> {
                    // Validators from bean class constants — no validator code in the view.
                    firstNameProp.ifPresent(p -> fb.withValidator((Property) p, PersonInfo.FIRST_NAME_RULE));
                    lastNameProp .ifPresent(p -> fb.withValidator((Property) p, PersonInfo.LAST_NAME_RULE));
                    emailProp    .ifPresent(p -> fb.withValidator((Property) p, PersonInfo.EMAIL_RULE));
                })
                .configure(fb -> fb
                        .validateOnValueChange(true)       // inline red errors while typing
                        .enterMovesFocusToNext(true)       // Enter → focus next field
                        .validateOnEnterFocusMove(true))   // validate before moving; stay on field if invalid
                .build();

        var result = new Span("Press Enter to move through fields, then Enter on Submit");

        var submitBtn = new Button("Submit", e -> {
            try {
                PersonInfo p = form.getBean();
                result.setText("Submitted: %s %s — %s".formatted(
                        blankOr(p.getFirstName(), "?"),
                        blankOr(p.getLastName(),  "?"),
                        blankOr(p.getEmail(),     "no email")));
                showSuccess("Form submitted via keyboard");
            } catch (Validator.ValidationException ex) {
                result.setText("Fix required fields first.");
                showError(ex.getMessage());
            }
        });

        var clearBtn = new Button("Clear", e -> { form.clear(); result.setText("Cleared."); });
        var btnRow   = ResponsiveDiv.flex().row().gapS().add(submitBtn, clearBtn).build();

        // ── Auto-focus first field via Focusable.focus() ──────────────────────
        form.getComponent().addAttachListener(e ->
                form.getElements()
                        .findFirst()
                        .ifPresent(input -> {
                            if (input.getComponent() instanceof Focusable<?> f) f.focus();
                        }));

        // Place submitBtn AFTER the form in DOM order so focusNextDocumentElement()
        // finds it naturally when Enter is pressed on the last field.
        var container = new Div(form.getComponent(), btnRow, result);

        return new DemoExample(
                "Enter-Key Navigation — built-in enterMovesFocusToNext (two builder flags, no helpers)",
                container, """
                        // Two builder flags enable full keyboard traversal — nothing else needed.
                        var form = BeanPropertyInputForm.formLayout(PersonInfo.class)
                            .configure(fb -> fb
                                .validateOnValueChange(true)      // inline errors while typing
                                .enterMovesFocusToNext(true)      // Enter → next field
                                .validateOnEnterFocusMove(true))  // stay on field if invalid
                            .build();

                        // How it works:
                        // - Enter in field N   → focus field N+1 (skips read-only / hidden fields)
                        // - Enter in last field → focusNextDocumentElement() walks the DOM to
                        //     find the first focusable element after the form — the Submit button
                        //     if it is placed after the form component in the DOM.
                        // - Enter on Submit     → native browser button activation, no extra code.
                        // - validateOnEnterFocusMove: if required fields are empty, Enter shows
                        //     inline validation errors and focus stays on the failing field.

                        // Auto-focus first field on attach (pure Vaadin, no JS):
                        form.getComponent().addAttachListener(e ->
                            form.getElements().findFirst()
                                .ifPresent(input -> {
                                    if (input.getComponent() instanceof Focusable<?> f) f.focus();
                                }));
                        """);
    }

    /** Submit helper shared by the Submit button and the Enter shortcut. */
    private void submitKeyboardForm(BeanPropertyInputForm<PersonInfo> form, Span result, Span status) {
        try {
            PersonInfo p = form.getBean();   // validates; throws ValidationException on failure
            result.setText("Submitted: %s %s — %s".formatted(
                    blankOr(p.getFirstName(), "?"),
                    blankOr(p.getLastName(),  "?"),
                    blankOr(p.getEmail(),     "no email")));
            status.setText("⌨ Enter → form submitted successfully");
            showSuccess("Form submitted via Enter");
        } catch (Validator.ValidationException ex) {
            status.setText("⌨ Enter → validation failed — fix errors above");
            showError(ex.getMessage());
        }
    }

    private static String blankOr(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void showSuccess(String message) {
        var n = Notification.show(message, 2000, Notification.Position.BOTTOM_END);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showError(String message) {
        var n = Notification.show(message, 3000, Notification.Position.BOTTOM_END);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}







