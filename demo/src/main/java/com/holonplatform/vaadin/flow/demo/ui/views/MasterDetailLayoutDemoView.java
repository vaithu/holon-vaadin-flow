package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link MasterDetailLayout}.
 */
@StyleSheet("context://mdl-demo.css")
@PageTitle("MasterDetailLayout – Holon Demo")
@Route(value = "master-detail-layout", layout = DemoMainLayout.class)
public class MasterDetailLayoutDemoView extends Div implements BeforeEnterObserver {

    // ── Status enum ────────────────────────────────────────────────────────────

    public enum Status {
        ACTIVE("Active"),
        IDLE("Idle"),
        NEW("New");

        private final String label;

        Status(String label) { this.label = label; }

        public String getLabel() { return label; }

        /** Returns the BEM modifier CSS class, e.g. {@code mdl-demo__status--active}. */
        public String getCssModifier() { return "mdl-demo__status--" + name().toLowerCase(); }
    }

    // ── Mock data ──────────────────────────────────────────────────────────────

    /** Simple demo bean. No Lombok — plain accessors. */
    public static final class Contact {
        private final int    id;
        private       String name, email, role, department, location;
        private final Status status;

        Contact(int id, String name, String email, String role,
                String department, String location, Status status) {
            this.id = id; this.name = name; this.email = email;
            this.role = role; this.department = department;
            this.location = location; this.status = status;
        }

        public int    getId()         { return id; }
        public String getName()       { return name; }
        public String getEmail()      { return email; }
        public String getRole()       { return role; }
        public String getDepartment() { return department; }
        public String getLocation()   { return location; }
        public Status getStatus()     { return status; }

        public void setName(String v)       { this.name       = v; }
        public void setEmail(String v)      { this.email      = v; }
        public void setRole(String v)       { this.role       = v; }
        public void setDepartment(String v) { this.department = v; }
        public void setLocation(String v)   { this.location   = v; }
    }

    private static final List<Contact> DATA = new ArrayList<>(List.of(
        new Contact(1,  "Alice Martin",  "alice@example.com",  "Product Manager",     "Product",    "London",  Status.ACTIVE),
        new Contact(2,  "Bob Chen",      "bob@example.com",    "Senior Engineer",     "Engineering","Berlin",  Status.IDLE),
        new Contact(3,  "Carol Torres",  "carol@example.com",  "UX Designer",         "Design",     "Madrid",  Status.NEW),
        new Contact(4,  "David Kim",     "david@example.com",  "DevOps Lead",         "Platform",   "Seoul",   Status.ACTIVE),
        new Contact(5,  "Eva Müller",    "eva@example.com",    "Data Scientist",      "Analytics",  "Munich",  Status.IDLE),
        new Contact(6,  "Frank Rossi",   "frank@example.com",  "Backend Engineer",    "Engineering","Rome",    Status.IDLE),
        new Contact(7,  "Grace Patel",   "grace@example.com",  "QA Lead",             "Quality",    "Mumbai",  Status.ACTIVE),
        new Contact(8,  "Henry Dubois",  "henry@example.com",  "Security Engineer",   "Platform",   "Paris",   Status.IDLE),
        new Contact(9,  "Isla Nakamura", "isla@example.com",   "Frontend Engineer",   "Engineering","Tokyo",   Status.NEW),
        new Contact(10, "James O'Brien", "james@example.com",  "Engineering Manager", "Engineering","Dublin",  Status.ACTIVE)
    ));

    // ── Component references ────────────────────────────────────────────────────

    private MasterDetailLayout<Contact> masterDetail;
    private Grid<Contact>               grid;
    private final Header                detailProfileHeader;

    // ── Form fields — created ONCE ────────────────────────────────────────────

    private final TextField  nameField  = new TextField("Full name");
    private final EmailField emailField = new EmailField("Email address");
    private final TextField  roleField  = new TextField("Role");
    private final TextField  deptField  = new TextField("Department");
    private final TextField  locField   = new TextField("Location");

    // Detail content components — created once, reused on every selection
    private final Div    formBody;
    private final Div    footer;
    private final Span   metaId     = new Span();
    private final Button saveBtn    = new Button("Save changes");
    private final Button archiveBtn = new Button("Archive");
    private final Button deleteBtn  = new Button("Delete");

    // Holds the currently displayed contact — set by the core sync handler.
    // Used only by the save and delete button listeners.
    private Contact currentContact;

    /** URL ?id= restore */
    private String pendingId;

    // ── Constructor ────────────────────────────────────────────────────────────

    public MasterDetailLayoutDemoView() {
        addClassName("app-view");
        addClassName("mdl-demo");
        setSizeFull();

        detailProfileHeader = new Header("Select a contact", HeadingLevel.H3);
        detailProfileHeader.setHeadingFontSize(Font.Size.LARGE);
        detailProfileHeader.setActions(saveBtn);

        // ── Build form body once ──────────────────────────────────────────────

        Span identityLabel = new Span("Identity");
        identityLabel.addClassName("mdl-demo__section-label");
        FormLayout identityForm = new FormLayout();
        identityForm.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("480px", 2));
        identityForm.add(nameField, emailField);
        Div identitySection = new Div(identityLabel, identityForm);
        identitySection.addClassName("mdl-demo__form-section");

        Span orgLabel = new Span("Organisation");
        orgLabel.addClassName("mdl-demo__section-label");
        FormLayout orgForm = new FormLayout();
        orgForm.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("480px", 2));
        orgForm.add(roleField, deptField, locField);
        orgForm.setColspan(locField, 2);
        Div orgSection = new Div(orgLabel, orgForm);
        orgSection.addClassName("mdl-demo__form-section");

        formBody = new Div(identitySection, orgSection);
        formBody.addClassName("mdl-demo__form-body");

        // ── Buttons ───────────────────────────────────────────────────────────

        saveBtn.getElement().getThemeList().add("primary");
        saveBtn.addClickListener(e -> {
            if (currentContact == null) return;
            currentContact.setName(nameField.getValue());
            currentContact.setEmail(emailField.getValue());
            currentContact.setRole(roleField.getValue());
            currentContact.setDepartment(deptField.getValue());
            currentContact.setLocation(locField.getValue());
            // notifyDataChanged() bumps dataVersion → all withDetailSync effects re-run,
            // then calls the onDataChanged listener that refreshes the grid.
            masterDetail.notifyDataChanged();
            Notification n = Notification.show("Contact saved", 2000, Notification.Position.BOTTOM_END);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        metaId.addClassName("mdl-demo__footer-meta");

        archiveBtn.addClassName("mdl-demo__footer-btn");
        archiveBtn.addClickListener(e ->
            Notification.show("Contact archived", 2000, Notification.Position.BOTTOM_END));

        deleteBtn.getElement().getThemeList().add("error tertiary");
        deleteBtn.addClassName("mdl-demo__footer-btn--danger");
        deleteBtn.addClickListener(e -> {
            if (currentContact == null) return;
            String name = currentContact.getName();
            DATA.removeIf(c -> c.getId() == currentContact.getId());
            currentContact = null;
            masterDetail.notifyDataChanged();
            masterDetail.clearSelection();
            Notification n = Notification.show(name + " removed", 2000, Notification.Position.BOTTOM_END);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        footer = new Div(metaId, archiveBtn, deleteBtn);
        footer.addClassName("mdl-demo__detail-footer");

        buildLayout();
    }

    // ── Layout assembly ────────────────────────────────────────────────────────

    private void buildLayout() {

        TextField search = new TextField();
        search.setPlaceholder("Search contacts…");
        search.setClearButtonVisible(true);

        grid = new Grid<>(Contact.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        grid.addClassName("mdl-demo__grid");

        grid.addComponentColumn(this::buildRowCell)
            .setHeader("")
            .setFlexGrow(1)
            .setAutoWidth(true);

        grid.setItems(DATA);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        search.addValueChangeListener(e -> {
            String q = e.getValue().toLowerCase();
            grid.setItems(q.isBlank() ? DATA : DATA.stream()
                .filter(c -> c.getName().toLowerCase().contains(q)
                          || c.getDepartment().toLowerCase().contains(q)
                          || c.getLocation().toLowerCase().contains(q))
                .toList());
        });

        GridHeader masterHeader = new GridHeader("All contacts");
        Span countBadge = new Span(DATA.size() + " records");
        countBadge.addClassName("mdl-demo__master-count");
        masterHeader.setDetails(countBadge);



        masterDetail = MasterDetailLayout.<Contact>builder()
            .masterHeader(masterHeader)
            .masterSearch(search)
            .masterGrid(grid)
            .detailHeader(detailProfileHeader)
            .detailContent(contact -> new Component[]{ formBody, footer })
            .itemId(c -> String.valueOf(c.getId()), this::findById)
            .mobileSheetTitle("Contact details")
            .autoSelectFirst(true)
            .onDataChanged(() -> grid.getDataProvider().refreshAll())
            // ── Reactive sync — driven by MasterDetailLayout.withDetailSync ──
            // Each handler is lifecycle-bound to its owner component.
            // Re-runs on selection change AND after notifyDataChanged() (e.g. save).
            .withDetailSync(formBody, c -> {
                currentContact = c;                           // keep local ref for buttons
                nameField.setValue(c.getName());
                emailField.setValue(c.getEmail());
                roleField.setValue(c.getRole());
                deptField.setValue(c.getDepartment());
                locField.setValue(c.getLocation());
            })
            .withDetailSync(detailProfileHeader, c -> {
                detailProfileHeader.setPrefix(buildAvatar(c, true));
                detailProfileHeader.setHeading(c.getName());
                detailProfileHeader.setDetails(buildMetaSpan(c), buildStatusPill(c.getStatus()));
            })
            .withDetailSync(metaId, c -> metaId.setText("Record ID · " + c.getId()))
            .build();

        masterDetail.setSizeFull();
        masterDetail.addClassName("mdl-demo__master-detail");
        masterDetail.getResponsiveLayout().withSeparator();

        add(masterDetail);
    }

    // ── Detail content — no imperative updates needed here ────────────────────

    // ── Grid row cell ─────────────────────────────────────────────────────────

    private Div buildRowCell(Contact c) {
        Avatar avatar = buildAvatar(c, false);

        Span nameLbl = new Span(c.getName());
        nameLbl.addClassName("mdl-demo__row-name");

        Span subLbl = new Span(c.getDepartment() + " · " + c.getLocation());
        subLbl.addClassName("mdl-demo__row-sub");

        Div nameStack = new Div(nameLbl, subLbl);
        nameStack.addClassName("mdl-demo__row-info");

        Span statusBadge = new Span(c.getStatus().getLabel());
        statusBadge.addClassName("mdl-demo__status-badge");
        statusBadge.addClassName(c.getStatus().getCssModifier());

        Div cell = new Div(avatar, nameStack, statusBadge);
        cell.addClassName("mdl-demo__row-cell");
        return cell;
    }

    // ── URL restore ───────────────────────────────────────────────────────────

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getLocation().getQueryParameters()
             .getParameters()
             .getOrDefault("id", List.of())
             .stream().findFirst()
             .ifPresent(id -> this.pendingId = id);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (pendingId != null) {
            masterDetail.restoreSelection(pendingId);
            pendingId = null;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Span buildMetaSpan(Contact c) {
        Span span = new Span(c.getRole() + " · " + c.getDepartment());
        span.addClassName("mdl-demo__profile-meta");
        return span;
    }

    private static Span buildStatusPill(Status status) {
        Span pill = new Span(status.getLabel());
        pill.addClassName("mdl-demo__status-pill");
        pill.addClassName(status.getCssModifier());
        return pill;
    }

    private Avatar buildAvatar(Contact c, boolean large) {
        Avatar avatar = new Avatar(c.getName());
//        avatar.addClassName("mdl-demo__avatar");
//        avatar.addClassName("mdl-demo__avatar--c" + (c.getId() % 6));
//        if (large) avatar.addClassName("mdl-demo__avatar--lg");
        return avatar;
    }

    private Optional<Contact> findById(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            return DATA.stream().filter(c -> c.getId() == id).findFirst();
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
