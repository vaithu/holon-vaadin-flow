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
 *   <li>Cancel in Dialog — EntityFormPanel inside a {@link Dialog}</li>
 *   <li>Components API — using {@code Components.entityFormPanel()} shortcut</li>
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
                inDialogExample(),
                componentsApiExample()
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
    // Example 7 — EntityFormPanel inside a Dialog (Cancel closes the dialog)
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
        return new DemoExample("7 — Inside a Dialog (Cancel closes, Save closes on success)", wrapper, """
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
    // Example 8 — Components API shortcut
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
        return new DemoExample("8 — Components API (Components.entityFormPanel)", wrapper, """
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

