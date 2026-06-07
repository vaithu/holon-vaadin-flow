package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.CollaborationFormSupport;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Demo page for {@link CollaborationFormSupport}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Option A — {@code generateFormLayout()} auto-creates fields + Jakarta validation</li>
 *   <li>Option B — {@code autoBindFields(holder)} — declare fields by name</li>
 *   <li>Signals — {@code enabledSignal()} drives external reactive components</li>
 *   <li>Live collaboration + invitation — {@code onCollaborationStarted()} pushes
 *       a cross-session toast when the other user enables sharing</li>
 * </ol>
 */
@PageTitle("CollaborationFormSupport – Holon Demo")
@Route(value = "collaboration-form-support", layout = DemoMainLayout.class)
public class CollaborationFormSupportDemoView extends Div {

    // ── Demo bean ─────────────────────────────────────────────────────────────

    public enum ContactStatus { ACTIVE, IDLE, NEW }

    public static final class Contact {
        private Long          id;
        @NotEmpty @Size(max = 80)
        private String        name;
        @Email
        private String        email;
        @Size(max = 60)
        private String        role;
        @Min(0) @Max(130)
        private int           age;
        private LocalDate     birthDate;
        private ContactStatus status;

        public Contact() {}
        public Contact(Long id, String name, String email, String role,
                       int age, LocalDate birthDate, ContactStatus status) {
            this.id = id; this.name = name; this.email = email;
            this.role = role; this.age = age;
            this.birthDate = birthDate; this.status = status;
        }
        public Long          getId()        { return id; }
        public String        getName()      { return name; }
        public void          setName(String v)         { name = v; }
        public String        getEmail()     { return email; }
        public void          setEmail(String v)        { email = v; }
        public String        getRole()      { return role; }
        public void          setRole(String v)         { role = v; }
        public int           getAge()       { return age; }
        public void          setAge(int v)             { age = v; }
        public LocalDate     getBirthDate() { return birthDate; }
        public void          setBirthDate(LocalDate v) { birthDate = v; }
        public ContactStatus getStatus()    { return status; }
        public void          setStatus(ContactStatus v){ status = v; }
    }

    private static final List<Contact> CONTACTS = List.of(
        new Contact(1L, "Alice Martin",  "alice@example.com",  "Product Manager", 35, LocalDate.of(1990, 3, 14),  ContactStatus.ACTIVE),
        new Contact(2L, "Bob Chen",      "bob@example.com",    "Senior Engineer", 29, LocalDate.of(1996, 7, 22),  ContactStatus.IDLE),
        new Contact(3L, "Carol Torres",  "carol@example.com",  "UX Designer",     31, LocalDate.of(1994, 11, 5),  ContactStatus.NEW)
    );

    // ── Constructor ───────────────────────────────────────────────────────────

    public CollaborationFormSupportDemoView() {
        addClassName("app-view");

        var examples = ResponsiveDiv.flex().column().gapL().build();
        examples.add(optionAExample(), signalsExample(), liveCollabExample());

        add(
            new H1("CollaborationFormSupport"),
            new Paragraph("A single helper that wraps CollaborationBinder and adds an opt-in " +
                "\"Collaborate\" toggle to any form. Off by default — behaves like a plain " +
                "BeanValidationBinder. Powered by Vaadin Signals for reactive UI state and " +
                "CollaborationMap for cross-session invitations."),
            examples
        );
    }

    // ── Example 1 — Option A + Option B side by side ──────────────────────────

    private DemoExample optionAExample() {

        // ── User: Alice — Option A (generateFormLayout)  ──────────────────────
        var alice = new UserInfo("demo-user-a", "Alice");
        alice.setImage("https://i.pravatar.cc/40?u=alice");
        var collabA = CollaborationFormSupport.of(Contact.class, alice);

        FormLayout formA = collabA.generateFormLayout(
                f -> f.setResponsiveSteps(
                        new FormLayout.ResponsiveStep("0", 1),
                        new FormLayout.ResponsiveStep("500px", 2)),
                "id");
        collabA.setEntity("demo-contact/1", CONTACTS.getFirst());

        var saveBtnA = new Button("Save", e -> {
            var draft = new Contact();
            if (collabA.binder().writeBeanIfValid(draft))
                showSuccess("Saved: " + draft.getName());
        });
        saveBtnA.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var containerA = buildFormCard(
                "Option A — generateFormLayout()", collabA, formA, saveBtnA);

        // ── User: Bob — Option B (autoBindFields) ─────────────────────────────
        var bob = new UserInfo("demo-user-b", "Bob");
        bob.setImage("https://i.pravatar.cc/40?u=bob");
        var collabB = CollaborationFormSupport.of(Contact.class, bob);

        var nameField  = new TextField("Full name");
        var emailField = new EmailField("Email address");
        var roleField  = new TextField("Role");
        var ageField   = new IntegerField("Age");

        @SuppressWarnings("unused") // fields read reflectively by autoBindFields
        var holder = new Object() {
            final TextField    name  = nameField;
            final EmailField   email = emailField;
            final TextField    role  = roleField;
            final IntegerField age   = ageField;
        };
        collabB.autoBindFields(holder);
        collabB.setEntity("demo-contact-b/2", CONTACTS.get(1));

        var formB = new FormLayout(nameField, emailField, roleField, ageField);
        formB.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        var saveBtnB = new Button("Save", e -> {
            var draft = new Contact();
            if (collabB.binder().writeBeanIfValid(draft))
                showSuccess("Saved: " + draft.getName());
        });
        saveBtnB.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var containerB = buildFormCard(
                "Option B — autoBindFields(holder)", collabB, formB, saveBtnB);

        var row = ResponsiveDiv.flex()
                .column().gapM()
                .desktop().row().gapL().end()
                .add(containerA, containerB)
                .build();

        return new DemoExample("Options A & B — auto field generation vs. declared fields", row, """
                // ── Option A: ONE call generates fields + binds + wires Jakarta validation ──
                var collab = CollaborationFormSupport.of(Contact.class, localUser);
                FormLayout form = collab.generateFormLayout(
                    f -> f.setResponsiveSteps(...),
                    "id");                         // exclude fields

                // ── Option B: declare fields by property name, auto-scan & bind ──
                private final TextField    name  = new TextField("Full name");
                private final EmailField   email = new EmailField("Email");
                private final IntegerField age   = new IntegerField("Age");

                var collab = CollaborationFormSupport.of(Contact.class, localUser);
                collab.autoBindFields(this);       // @NotEmpty, @Email, @Min/@Max auto-wired

                // Both options: identical save API
                collab.setEntity("contact/" + id, contact);
                toolbar.add(collab.avatarGroup(), collab.toggleButton());
                if (collab.binder().writeBeanIfValid(draft)) service.save(draft);
                """);
    }

    // ── Example 2 — Signals: enabledSignal() drives external reactive UI ───────

    private DemoExample signalsExample() {

        var carol = new UserInfo("signal-demo-user", "Carol");
        carol.setImage("https://i.pravatar.cc/40?u=carol");
        var collab = CollaborationFormSupport.of(Contact.class, carol);

        FormLayout form = collab.generateFormLayout("id");
        collab.setEntity("signal-demo/contact/3", CONTACTS.get(2));

        // ── 1. Reactive status badge driven by enabledSignal ──────────────────
        var statusBadge = new Span("🔒 Private editing");
        statusBadge.addClassName("collab-status-badge");

        Signal.effect(statusBadge, () -> {
            boolean on = collab.enabledSignal().get();         // reactive dependency
            statusBadge.setText(on ? "🟢 Live collaboration" : "🔒 Private editing");
            statusBadge.addClassName   ("collab-status--" + (on ? "active"   : "inactive"));
            statusBadge.removeClassName("collab-status--" + (on ? "inactive" : "active"));
        });

        // ── 2. Form border highlights when active ─────────────────────────────
        var formWrapper = new Div(form);
        formWrapper.addClassName("collab-form-wrapper");

        Signal.effect(formWrapper, () ->
            formWrapper.setClassName("collab-form-wrapper--active", collab.enabledSignal().get()));

        // ── 3. Save button enabled only while collaboration is on ─────────────
        var saveBtn = new Button("Save (enabled while Live)");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.setEnabled(false);

        Signal.effect(saveBtn, () -> saveBtn.setEnabled(collab.enabledSignal().get()));

        saveBtn.addClickListener(e -> {
            var draft = new Contact();
            if (collab.binder().writeBeanIfValid(draft)) showSuccess("Saved: " + draft.getName());
        });

        var toolbar = new HorizontalLayout(collab.avatarGroup(), statusBadge,
                                           collab.toggleButton(), saveBtn);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setFlexGrow(1, statusBadge);

        var container = ResponsiveDiv.flex().column().gapS().build();
        container.add(toolbar, formWrapper);

        return new DemoExample("Signals — enabledSignal() binds external components reactively", container, """
                var collab = CollaborationFormSupport.of(Contact.class, localUser);

                // 1. Status badge — updates automatically when toggle is clicked
                var statusBadge = new Span("🔒 Private editing");
                Signal.effect(statusBadge, () -> {
                    boolean on = collab.enabledSignal().get();  // reactive read
                    statusBadge.setText(on ? "🟢 Live collaboration" : "🔒 Private editing");
                });

                // 2. Form wrapper gets a CSS class while live
                Signal.effect(formWrapper, () ->
                    formWrapper.setClassName("collab-active", collab.enabledSignal().get()));

                // 3. Save button only active during live collaboration
                Signal.effect(saveBtn, () -> saveBtn.setEnabled(collab.enabledSignal().get()));

                // Any number of components can independently observe the same signal.
                // Each effect is lifecycle-bound to its owner component — no leaks.
                """);
    }

    // ── Example 3 — Live collab + onCollaborationStarted invitation ───────────

    private DemoExample liveCollabExample() {

        var userA = new UserInfo("live-user-a", "Alice 👩\u200d💻");
        userA.setImage("https://i.pravatar.cc/40?u=alice-live");

        var userB = new UserInfo("live-user-b", "Bob 👨\u200d💻");
        userB.setImage("https://i.pravatar.cc/40?u=bob-live");

        var collabA = CollaborationFormSupport.of(Contact.class, userA);
        var collabB = CollaborationFormSupport.of(Contact.class, userB);

        // Contact selector
        var contactSelect = new Select<Contact>();
        contactSelect.setLabel("Select contact to edit");
        contactSelect.setItems(CONTACTS);
        contactSelect.setItemLabelGenerator(Contact::getName);
        contactSelect.setValue(CONTACTS.getFirst());

        // Build panels (with reactive status badges)
        var panelA = buildUserPanel("Alice 👩\u200d💻", collabA);
        var panelB = buildUserPanel("Bob 👨\u200d💻",   collabB);

        // Contact change — both panels follow, re-enabling collaboration on the new topic
        contactSelect.addValueChangeListener(e -> {
            Contact c = e.getValue();
            if (c == null) { collabA.clearEntity(); collabB.clearEntity(); return; }
            String topicId = "live-demo/contact/" + c.getId();
            collabA.setEntity(topicId, c);
            collabB.setEntity(topicId, c);
            // Re-enable collaboration on the new topic
            collabA.enable();
            collabB.enable();
        });

        // Initialise on first attach
        var container = new Div() {
            @Override
            protected void onAttach(AttachEvent ae) {
                super.onAttach(ae);
                String topicId = "live-demo/contact/" + CONTACTS.getFirst().getId();

                // Set entity on both sides (populates via localBinder, no CE yet)
                collabA.setEntity(topicId, CONTACTS.getFirst());
                collabB.setEntity(topicId, CONTACTS.getFirst());

                // Register invitation listeners BEFORE enabling so that the
                // auto-enable broadcasts below don't re-trigger them immediately.
                collabA.onCollaborationStarted(panelB, inviter ->
                    showInvitation(inviter + " started collaborating — join them!"));
                collabB.onCollaborationStarted(panelA, inviter ->
                    showInvitation(inviter + " started collaborating — join them!"));

                // Auto-enable collaboration on BOTH panels so the demo shows
                // live sync the moment the page loads.  In a real app each user
                // does this from their own browser session.
                collabA.enable();
                collabB.enable();
            }
        };

        var tip = Alert.builder(Alert.Variant.INFO)
                .title("Both panels start in Live collaboration mode")
                .description("Type in either form — changes appear instantly in the other. " +
                             "Click \"Stop sharing\" on one panel to pause its sync; its fields " +
                             "keep the last values locally. Click \"Collaborate\" again to " +
                             "re-join the shared topic. When one user re-enables, the other " +
                             "receives a real-time invitation toast via CE CollaborationMap.")
                .build();

        var panelRow = ResponsiveDiv.flex()
                .column().gapM()
                .desktop().row().gapL().end()
                .add(panelA, panelB)
                .build();

        container.add(tip, new Div(), contactSelect, panelRow);

        return new DemoExample(
                "Live Collaboration + Invitation (enabledSignal + onCollaborationStarted)", container, """
                var collabA = CollaborationFormSupport.of(Contact.class, userA);
                var collabB = CollaborationFormSupport.of(Contact.class, userB);

                // Wire invitation BEFORE enable() so initial auto-enable doesn't re-fire them.
                collabA.onCollaborationStarted(panelB, inviter ->
                    Notification.show(inviter + " started collaborating — join them!",
                        5000, Notification.Position.BOTTOM_END));
                collabB.onCollaborationStarted(panelA, inviter ->
                    Notification.show(inviter + " started collaborating — join them!",
                        5000, Notification.Position.BOTTOM_END));

                // Both point at the same topicId — CE immediately syncs both binders.
                collabA.setEntity("contact/42", contact);
                collabB.setEntity("contact/42", contact);
                collabA.enable();   // activates collabBinder.setTopic(topicId, draftSupplier)
                collabB.enable();   // both now subscribed → real-time sync

                // Type on either side → changes appear instantly on the other.
                // Click "Stop sharing" → CE disconnects, fields retain values locally.
                // Click "Collaborate" again → re-joins, CE seeds from current draft.
                """);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Builds a card with: title bar, avatar group, reactive status badge,
     * toggle button, the given form layout, and a save button.
     */
    private static Div buildFormCard(String title,
                                     CollaborationFormSupport<Contact> collab,
                                     FormLayout form,
                                     Button saveBtn) {
        var titleSpan = new Span(title);
        titleSpan.addClassName("collab-card-title");

        var statusBadge = buildStatusBadge(collab);

        var header = new HorizontalLayout(titleSpan, collab.avatarGroup(),
                                          statusBadge, collab.toggleButton(), saveBtn);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setFlexGrow(1, titleSpan);

        var card = new Div(header, form);
        card.addClassName("collab-demo-panel");
        return card;
    }

    /**
     * Builds a full user panel (title + reactive badge + form + save).
     */
    private static Div buildUserPanel(String userName,
                                      CollaborationFormSupport<Contact> collab) {

        var nameField  = new TextField("Full name");
        var emailField = new EmailField("Email");
        var roleField  = new TextField("Role");
        var ageField   = new IntegerField("Age");

        @SuppressWarnings("unused") // fields read reflectively by autoBindFields
        var holder = new Object() {
            final TextField    name  = nameField;
            final EmailField   email = emailField;
            final TextField    role  = roleField;
            final IntegerField age   = ageField;
        };
        collab.autoBindFields(holder);

        var form = new FormLayout(nameField, emailField, roleField, ageField);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        var saveBtn = new Button(VaadinIcon.CHECK.create(), e -> {
            var draft = new Contact();
            if (collab.binder().writeBeanIfValid(draft))
                showSuccess(userName + " saved: " + draft.getName());
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON);
        saveBtn.getElement().setAttribute("title", "Save");

        var userLabel = new Span(userName);
        userLabel.addClassName("collab-card-title");

        var header = new HorizontalLayout(userLabel, collab.avatarGroup(),
                                          buildStatusBadge(collab), collab.toggleButton(), saveBtn);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setFlexGrow(1, userLabel);

        var panel = new Div(header, form);
        panel.addClassName("collab-demo-panel");
        return panel;
    }

    /**
     * Creates a Span whose text reacts to {@code collab.enabledSignal()} via
     * {@link Signal#effect} — no manual update calls needed anywhere.
     */
    private static Span buildStatusBadge(CollaborationFormSupport<?> collab) {
        var badge = new Span("🔒 Private");
        badge.addClassName("collab-status-badge");
        badge.addClassName("collab-status--inactive");

        Signal.effect(badge, () -> {
            boolean on = collab.enabledSignal().get();          // reactive read
            badge.setText(on ? "🟢 Live" : "🔒 Private");
            badge.addClassName   ("collab-status--" + (on ? "active"   : "inactive"));
            badge.removeClassName("collab-status--" + (on ? "inactive" : "active"));
        });

        return badge;
    }

    // ── Notification helpers ──────────────────────────────────────────────────

    private static void showSuccess(String message) {
        var n = Notification.show(message, 2500, Notification.Position.BOTTOM_END);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showInvitation(String message) {
        var n = Notification.show(message, 5000, Notification.Position.BOTTOM_END);
        n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }
}







