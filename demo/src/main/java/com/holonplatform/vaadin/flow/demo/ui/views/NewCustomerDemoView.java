package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.utils.NotificationUtil;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Full-page "New Customer" CRM form demo — refactored to use {@link EntityCreationForm}
 * as the page scaffold and {@link EntityFormPanel} for each step's form body.
 *
 * <h3>Component mapping</h3>
 * <ul>
 *   <li>Page scaffold      → {@link EntityCreationForm} via {@code Components.entityCreationForm()}</li>
 *   <li>6 step cards       → {@link FormStepCard} via {@code Components.formStepCard()}</li>
 *   <li>Step form bodies   → {@link EntityFormPanel} per step (bean-mode, {@code .noFooter()})</li>
 *   <li>Custom inputs      → {@code Input.*} passed via {@code .bind(fieldName, input)}</li>
 *   <li>Toggle rows        → {@code Input.boolean_().asSwitch()} in extra step content</li>
 *   <li>Chip pickers       → {@link ChipGroup} in extra step content</li>
 *   <li>Assignment rows    → {@link AssignmentPickerRow} in extra step content</li>
 *   <li>Multi-select tags  → Vaadin {@code MultiSelectComboBox} in extra step content</li>
 *   <li>Sticky save bar    → wired through {@link EntityCreationForm#setStatus} /
 *                            {@link EntityCreationForm#setProgress} via {@code withPostProcessor}</li>
 * </ul>
 */
@PageTitle("New Customer — CRM Demo")
@Route(value = "new-customer-crm", layout = DemoMainLayout.class)
@StyleSheet("context://new-customer-demo.css")
@StyleSheet("context://form-utils.css")
public class NewCustomerDemoView extends Div implements BeforeLeaveObserver {

    /** Flipped to {@code true} once the form is successfully saved. */
    private boolean saved = false;

    public NewCustomerDemoView() {
        addClassName("ncd");

        // Only extract the inputs that genuinely need to be accessible outside their panel:
        //   accountNameInput — drives the page title and avatar name reactively
        var accountNameInput = Input.string()
                .label("Account name")
                .withValue("Atelier Tremblay")
                .helperText("Available · no duplicate")
                .build();

        // Avatar refs for reactive name sync
        var orgAvatar     = new Avatar("Atelier Tremblay");
        var orgAvatarName = new Span("Atelier Tremblay");

        // ── One EntityFormPanel per step (noFooter — save lives in the bar) ─
        EntityFormPanel<OrganizationBean> orgPanel     = buildOrgPanel(accountNameInput);
        EntityFormPanel<ContactBean>      contactPanel = buildContactPanel();
        EntityFormPanel<AddressBean>      addrPanel    = buildAddressPanel();
        EntityFormPanel<FinancialBean>    finPanel     = buildFinancialPanel();
        EntityFormPanel<TagsBean>         tagsPanel    = buildTagsPanel();

        List<EntityFormPanel<?>> allPanels =
                List.of(orgPanel, contactPanel, addrPanel, finPanel, tagsPanel);

        // ── FormStepCards ─────────────────────────────────────────────────
        FormStepCard step1 = Components.formStepCard()
                .stepNumber(1).totalSteps(6)
                .title("Organization").subtitle("The legal entity you're adding")
                .helpText("Account ID is system-generated and immutable. "
                        + "Legal name is used on invoices and tax documents.")
                .content(buildAvatarPicker(orgAvatar, orgAvatarName), orgPanel)
                .build();

        FormStepCard step2 = Components.formStepCard()
                .stepNumber(2).totalSteps(6)
                .title("Primary contact").subtitle("The main person we communicate with")
                .content(
                        contactPanel,
                        FormViewUtils.labeledField("Role", buildRoleChipGroup()),
                        FormViewUtils.toggleRow("Sync with LinkedIn", "Auto-pull profile updates & profile photo", true))
                .build();

        FormStepCard step3 = Components.formStepCard()
                .stepNumber(3).totalSteps(6)
                .title("Address").subtitle("Billing & shipping addresses")
                .content(
                        addrPanel,
                        FormViewUtils.toggleRow("Shipping address is different", "For goods receipt & deliveries", false))
                .build();

        FormStepCard step4 = Components.formStepCard()
                .stepNumber(4).totalSteps(6)
                .title("Financial & tax").subtitle("Invoicing setup · required for bill generation")
                .state(FormStepCard.StepState.PENDING)
                .content(finPanel)
                .build();

        FormStepCard step5 = Components.formStepCard()
                .stepNumber(5).totalSteps(6)
                .title("Assignment & plan").subtitle("Who's responsible and what tier")
                .state(FormStepCard.StepState.PENDING)
                .content(buildAssignmentContent())
                .build();

        FormStepCard step6 = Components.formStepCard()
                .stepNumber(6).totalSteps(6)
                .title("Tags, source & notes").subtitle("Optional but helps with segmentation")
                .state(FormStepCard.StepState.PENDING)
                .content(buildTagsComboBox(), tagsPanel)
                .build();

        // ── Buttons whose click listeners are wired inside withPostProcessor ─
        var saveHdrBtn    = Components.button().text("Save customer").primary().build();
        var saveBarBtn    = Components.button().text("Save customer").primary().build();
        var discardHdrBtn = Components.button().text("Discard").build();
        var discardBarBtn = Components.button().text("Discard").build();

        // ── EntityCreationForm — single declarative chain ─────────────────
        var breadcrumbPage = new BreadcrumbPage("New customer");

        EntityCreationForm form = Components.entityCreationForm()
                .breadcrumb(
                        new BreadcrumbItem(new Span("CRM")),
                        new BreadcrumbItem(new Span("Customers")),
                        breadcrumbPage)
                .title("New customer")
                .subtitle(FormViewUtils.requiredFieldsHint())
                .draftBadge("Unsaved draft")
                .headerAction(
                        discardHdrBtn,
                        Components.button().text("Save as template").build(),
                        Components.button().text("Save & add another").build(),
                        saveHdrBtn)
                .steps(step1, step2, step3, step4, step5, step6)
                .status("Draft — not saved yet", StickyActionBar.Variant.WARNING)
                .progress("Profile", 0)
                .barAction(
                        discardBarBtn,
                        Components.button().text("Save & add another").build(),
                        saveBarBtn)
                .withPostProcessor(f -> {

                    Runnable syncHeader = () -> {
                        String v = accountNameInput.getValue();
                        String display = (v != null && !v.isBlank()) ? v : "New customer";
                        f.setHeader(display);
                        breadcrumbPage.setText(display);
                        orgAvatar.setName(display);
                        orgAvatarName.setText(display);
                    };
                    accountNameInput.addValueChangeListener(e -> syncHeader.run());
                    syncHeader.run();

                    // ── Progress + reactive draft badge ────────────────────
                    FormViewUtils.wireProgressTracking(f, allPanels, "Profile", "Unsaved changes");

                    // ── Step transitions (focus-tracking) ──────────────────
                    var transitions = FormViewUtils.stepTransitions()
                            .addPanelStep(step1, orgPanel)
                            .addPanelStep(step2, contactPanel)
                            .addPanelStep(step3, addrPanel)
                            .addPanelStep(step4, finPanel)
                            .addClickStep(step5)
                            .addPanelStep(step6, tagsPanel)
                            .autoDone(step5, step6)
                            .wire();

                    // Init: sync progress bar with pre-filled field values
                    FormViewUtils.updateProgress(f, allPanels, "Profile");

                    // ── Save ───────────────────────────────────────────────
                    Runnable handleSave = () -> {
                        if (transitions.validateAll()) {
                            saved = true;
                            f.setDraftBadge(null);
                            f.setStatus("Saved · just now", StickyActionBar.Variant.SUCCESS);
                            NotificationUtil.notificationSuccess("Customer saved!");
                        } else {
                            f.setDraftBadge("Save failed", EntityCreationForm.DraftBadgeVariant.ERROR);
                            f.setStatus("Please fix validation errors before saving",
                                    StickyActionBar.Variant.ERROR);
                            focusFirstInvalidInput(allPanels);
                        }
                    };
                    saveHdrBtn.addClickListener(e -> handleSave.run());
                    saveBarBtn.addClickListener(e -> handleSave.run());

                    // ── Discard ─────────────────────────────────────────────
                    Runnable handleDiscard = () -> AlertDialog.builder()
                            .title("Discard changes?")
                            .description("All unsaved changes will be lost. This cannot be undone.")
                            .headerIcon(VaadinIcon.TRASH.create(), Alert.Variant.DESTRUCTIVE)
                            .cancelText("Keep editing")
                            .confirmText("Discard")
                            .variant(Alert.Variant.DESTRUCTIVE)
                            .onConfirm(() -> {
                                saved = true; // suppress beforeLeave guard
                                f.getUI().ifPresent(ui -> ui.getPage().getHistory().go(-1));
                            })
                            .build()
                            .open();
                    discardHdrBtn.addClickListener(e -> handleDiscard.run());
                    discardBarBtn.addClickListener(e -> handleDiscard.run());
                })
                .build();

        add(form);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BeforeLeaveObserver — warn user if they navigate away without saving
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (!saved) {
            BeforeLeaveEvent.ContinueNavigationAction action = event.postpone();
            AlertDialog.builder()
                    .title("Leave without saving?")
                    .description("You have unsaved changes. If you leave now, all changes will be lost.")
                    .headerIcon(VaadinIcon.WARNING.create(), Alert.Variant.WARNING)
                    .cancelText("Keep editing")
                    .confirmText("Leave anyway")
                    .variant(Alert.Variant.DESTRUCTIVE)
                    .onConfirm(action::proceed)
                    .build()
                    .open();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EntityFormPanel builders — one per step
    // ─────────────────────────────────────────────────────────────────────────

    private static void focusFirstInvalidInput(List<EntityFormPanel<?>> panels) {
        for (EntityFormPanel<?> panel : panels) {
            var firstInvalid = panel.getForm().getElements()
                    .filter(input -> input.hasValidation().map(HasValidation::isInvalid).orElse(false))
                    .findFirst();
            if (firstInvalid.isPresent()) {
                var input = firstInvalid.get();
                input.focus();
                input.getComponent().getElement().executeJs("this.scrollIntoView({behavior:'smooth', block:'center'})");
                return;
            }
        }
    }

    private EntityFormPanel<OrganizationBean> buildOrgPanel(Input<String> accountNameInput) {
        return EntityFormPanel.bean(OrganizationBean.class)
                .properties("accountName", "legalName", "accountId",
                             "type", "industry", "segment",
                             "website", "phone", "employees")
                .autoLabels(true)
                .bind("accountName", accountNameInput)
                .bind("legalName", Input.string().label("Legal name")
                        .withValue("Atelier Tremblay GmbH")
                        .placeholder("If different from account name").build())
                .bind("accountId", Input.string().label("Account ID")
                        .withValue("AT-00287").readOnly()
                        .helperText("Auto-assigned · cannot be changed").build())
                .bind("type", Input.singleSelect(String.class).label("Type")
                        .items("Company", "Person", "Government", "Non-profit").build())
                .bind("industry", Input.singleSelect(String.class).label("Industry")
                        .items("Design & Creative", "Healthcare", "Logistics",
                               "Manufacturing", "Retail", "Technology").build())
                .bind("segment", Input.singleSelect(String.class).label("Segment")
                        .items("SMB", "Mid-market", "Enterprise").build())
                .bind("website", Input.string().label("Website")
                        .prefixComponent(VaadinIcon.GLOBE.create())
                        .withValue("atelier-tremblay.de").build())
                .bind("phone", Input.string().label("Phone")
                        .prefixComponent(VaadinIcon.PHONE.create())
                        .withValue("+43 1 532 1840").build())
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .required("accountName", "Account name is required")
                .required("industry",    "Industry is required")
                .noFooter()
                .build();
    }

    private EntityFormPanel<ContactBean> buildContactPanel() {
        return EntityFormPanel.bean(ContactBean.class)
                .properties("firstName", "lastName", "jobTitle",
                             "emailWork", "emailPersonal", "phoneMobile",
                             "language", "timezone")
                .autoLabels(true)
                .bind("firstName", Input.string().label("First name").withValue("Klaus").build())
                .bind("lastName",  Input.string().label("Last name").withValue("Weber").build())
                .bind("jobTitle",  Input.string().label("Job title").withValue("Managing Director").build())
                .bind("emailWork", Input.string().label("Email (work)")
                        .prefixComponent(VaadinIcon.ENVELOPE.create())
                        .withValue("k.weber@atelier-tremblay.de")
                        .helperText("Domain verified · deliverability 98%").build())
                .bind("emailPersonal", Input.string().label("Email (personal)")
                        .prefixComponent(VaadinIcon.ENVELOPE.create())
                        .placeholder("optional").build())
                .bind("phoneMobile", Input.string().label("Phone (mobile)")
                        .prefixComponent(VaadinIcon.MOBILE.create())
                        .withValue("+43 664 123 4567").build())
                .bind("language", Input.singleSelect(String.class).label("Language")
                        .items("German", "English", "French").build())
                .bind("timezone", Input.singleSelect(String.class).label("Timezone")
                        .items("Europe/Vienna (CET, UTC+1)",
                               "Europe/Berlin (CET, UTC+1)",
                               "Europe/London (GMT, UTC+0)").build())
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .required("firstName", "First name is required")
                .required("lastName",  "Last name is required")
                .required("emailWork", "Work email is required")
                .noFooter()
                .build();
    }

    private EntityFormPanel<AddressBean> buildAddressPanel() {
        return EntityFormPanel.bean(AddressBean.class)
                .properties("street", "postalCode", "city", "country")
                .autoLabels(true)
                .bind("street",     Input.string().label("Street").withValue("Maximilianstraße 14").build())
                .bind("postalCode", Input.string().label("Postal code").withValue("80539").build())
                .bind("city",       Input.string().label("City").withValue("München").build())
                .bind("country",    Input.singleSelect(String.class).label("Country")
                        .items("🇩🇪 Germany", "🇦🇹 Austria", "🇨🇿 Czechia", "🇫🇷 France", "🇮🇹 Italy").build())
                .<FormLayout>withPostProcessor((layout, property, input) -> {
                    if ("street".equals(property.getName())) layout.setColspan(input.getComponent(), 3);
                })
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .required("street",     "Street is required")
                .required("postalCode", "Postal code is required")
                .required("city",       "City is required")
                .required("country",    "Country is required")
                .noFooter()
                .build();
    }

    private EntityFormPanel<FinancialBean> buildFinancialPanel() {
        return EntityFormPanel.bean(FinancialBean.class)
                .properties("taxId", "currency", "paymentTerms",
                             "paymentMethod", "creditLimit", "taxExemption",
                             "billingEmail")
                .autoLabels(true)
                .bind("taxId", Input.string().label("Tax ID / VAT")
                        .withValue("DE 287 442 091")
                        .helperText("VIES-validated · EU format ✓").build())
                .bind("currency", Input.singleSelect(String.class).label("Currency")
                        .items("EUR · €", "USD · $", "GBP · £", "CZK · Kč").build())
                .bind("paymentTerms", Input.singleSelect(String.class).label("Payment terms")
                        .items("Due on receipt", "Net 14", "Net 30", "Net 45", "Net 60").build())
                .bind("paymentMethod", Input.singleSelect(String.class).label("Payment method")
                        .items("SEPA direct debit", "Bank transfer", "Credit card", "Check").build())
                .bind("creditLimit", Input.string().label("Credit limit")
                        .prefixComponent(new Span("€")).withValue("50,000").build())
                .bind("taxExemption", Input.singleSelect(String.class).label("Tax exemption")
                        .items("None", "Small business (§19 UStG)",
                               "Reverse charge (B2B EU)", "Export (outside EU)").build())
                .bind("billingEmail", Input.string().label("Billing email")
                        .prefixComponent(VaadinIcon.ENVELOPE.create())
                        .withValue("invoicing@atelier-tremblay.de")
                        .helperText("Invoices & payment reminders will be sent here").build())
                .<FormLayout>withPostProcessor((layout, property, input) -> {
                    if ("billingEmail".equals(property.getName())) layout.setColspan(input.getComponent(), 3);
                })
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .required("taxId",        "Tax ID / VAT is required")
                .required("billingEmail", "Billing email is required")
                .noFooter()
                .build();
    }

    private EntityFormPanel<TagsBean> buildTagsPanel() {
        return EntityFormPanel.<TagsBean>bean(TagsBean.class)
                .properties("leadSource", "campaign", "firstContactDate", "notes")
                .autoLabels(true)
                .bind("leadSource", Input.singleSelect(String.class).label("Lead source")
                        .items("Referral · Aisha Patel", "Website · contact form",
                               "Inbound · webinar", "Outbound · cold call").build())
                .bind("campaign", Input.singleSelect(String.class).label("Original campaign")
                        .items("Q1-2026 referral program", "None").build())
                .bind("firstContactDate", Input.localDate().label("First contact date")
                        .withValue(LocalDate.of(2026, 3, 12)).build())
                .bind("notes", Input.stringArea().label("Internal notes")
                        .placeholder("Anything your team should know about this customer…")
                        .withValue("Referred by Aisha Patel (Lumen Health). Worth a 1:1 with Tom B. before the QBR.")
                        .helperText("Visible to your team only. Customers never see these notes.").build())
                .<FormLayout>withPostProcessor((layout, property, input) -> {
                    if ("notes".equals(property.getName())) layout.setColspan(input.getComponent(), 3);
                })
                .responsiveSteps(steps -> steps.mobile(1).tablet(2).desktop(3))
                .noFooter()
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Custom step content (components that don't map to EntityFormPanel fields)
    // ─────────────────────────────────────────────────────────────────────────

    private Component buildAvatarPicker(Avatar avatar, Span pickName) {
        avatar.setColorIndex(1);
        avatar.getStyle().set("width", "64px").set("height", "64px").set("font-size", "1.25rem");

        pickName.addClassName("ncd__av-name");
        Span pickSub  = new Span("Auto-generated from initials · shown across the CRM");
        pickSub.addClassName("ncd__av-sub");
        Div pickInfo    = new Div(pickName, pickSub);
        pickInfo.addClassName("ncd__av-info");

        Div pickActions = new Div(
                Components.button().text("Initials").build(),
                Components.button().text("Upload logo").build());
        pickActions.addClassName("ncd__av-actions");

        Div avatarPicker = new Div(avatar, pickInfo, pickActions);
        avatarPicker.addClassName("ncd__av-pick");
        return avatarPicker;
    }

    private ChipGroup buildRoleChipGroup() {
        return ChipGroup.create()
                .addChip("★ Decision-maker", true)
                .addChip("Influencer")
                .addChip("End user")
                .addChip("Champion")
                .addChip("Gatekeeper");
    }

    private Component buildAssignmentContent() {
        AssignmentPickerRow ownerRow = Components.assignmentPickerRow("Maria Schmidt", "Account Executive · me");
        ownerRow.setColorIndex(1);
        ownerRow.addActionClickListener(ignored -> NotificationUtil.notificationWarning("Open person picker for Account Owner"));

        AssignmentPickerRow csmRow = Components.assignmentPickerRow("Tom Bergmann", "Senior CSM");
        csmRow.setColorIndex(4);
        csmRow.addActionClickListener(ignored -> NotificationUtil.notificationWarning("Open person picker for CSM"));

        ChipGroup tierGroup = ChipGroup.create()
                .addChip("Free").addChip("Starter").addChip("★ Gold", true).addChip("Platinum");

        FormLayout form = Components.formLayout().build();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",     1),
                new FormLayout.ResponsiveStep("480px", 2),
                new FormLayout.ResponsiveStep("700px", 3));
        form.add(
                FormViewUtils.labeledField("Account owner",        ownerRow),
                FormViewUtils.labeledField("Customer success mgr", csmRow),
                FormViewUtils.labeledField("Tier",                 tierGroup));
        return form;
    }

    private Component buildTagsComboBox() {
        MultiSelectComboBox<String> tags = new MultiSelectComboBox<>("Tags");
        tags.setItems("Mid-market", "Design", "EU-based", "High-touch", "Renewal", "E-commerce", "SMB");
        tags.setValue(Set.of("Mid-market", "Design", "EU-based", "High-touch", "Renewal"));
        tags.setWidthFull();
        return tags;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step focus wiring
    // ─────────────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────────────
    // Inner bean classes (one per step — mutable for BeanPropertyInputForm)
    // ─────────────────────────────────────────────────────────────────────────

    public static class OrganizationBean {
        private String accountName, legalName, accountId, type, industry, segment, website, phone;
        private Integer employees;
        public String getAccountName()          { return accountName; }
        public void setAccountName(String v)    { this.accountName = v; }
        public String getLegalName()            { return legalName; }
        public void setLegalName(String v)      { this.legalName = v; }
        public String getAccountId()            { return accountId; }
        public void setAccountId(String v)      { this.accountId = v; }
        public String getType()                 { return type; }
        public void setType(String v)           { this.type = v; }
        public String getIndustry()             { return industry; }
        public void setIndustry(String v)       { this.industry = v; }
        public String getSegment()              { return segment; }
        public void setSegment(String v)        { this.segment = v; }
        public String getWebsite()              { return website; }
        public void setWebsite(String v)        { this.website = v; }
        public String getPhone()                { return phone; }
        public void setPhone(String v)          { this.phone = v; }
        public Integer getEmployees()           { return employees; }
        public void setEmployees(Integer v)     { this.employees = v; }
    }

    public static class ContactBean {
        private String firstName, lastName, jobTitle, emailWork, emailPersonal, phoneMobile, language, timezone;
        public String getFirstName()            { return firstName; }
        public void setFirstName(String v)      { this.firstName = v; }
        public String getLastName()             { return lastName; }
        public void setLastName(String v)       { this.lastName = v; }
        public String getJobTitle()             { return jobTitle; }
        public void setJobTitle(String v)       { this.jobTitle = v; }
        public String getEmailWork()            { return emailWork; }
        public void setEmailWork(String v)      { this.emailWork = v; }
        public String getEmailPersonal()        { return emailPersonal; }
        public void setEmailPersonal(String v)  { this.emailPersonal = v; }
        public String getPhoneMobile()          { return phoneMobile; }
        public void setPhoneMobile(String v)    { this.phoneMobile = v; }
        public String getLanguage()             { return language; }
        public void setLanguage(String v)       { this.language = v; }
        public String getTimezone()             { return timezone; }
        public void setTimezone(String v)       { this.timezone = v; }
    }

    public static class AddressBean {
        private String street, postalCode, city, country;
        public String getStreet()               { return street; }
        public void setStreet(String v)         { this.street = v; }
        public String getPostalCode()           { return postalCode; }
        public void setPostalCode(String v)     { this.postalCode = v; }
        public String getCity()                 { return city; }
        public void setCity(String v)           { this.city = v; }
        public String getCountry()              { return country; }
        public void setCountry(String v)        { this.country = v; }
    }

    public static class FinancialBean {
        private String taxId, currency, paymentTerms, paymentMethod, creditLimit, taxExemption, billingEmail;
        public String getTaxId()                { return taxId; }
        public void setTaxId(String v)          { this.taxId = v; }
        public String getCurrency()             { return currency; }
        public void setCurrency(String v)       { this.currency = v; }
        public String getPaymentTerms()         { return paymentTerms; }
        public void setPaymentTerms(String v)   { this.paymentTerms = v; }
        public String getPaymentMethod()        { return paymentMethod; }
        public void setPaymentMethod(String v)  { this.paymentMethod = v; }
        public String getCreditLimit()          { return creditLimit; }
        public void setCreditLimit(String v)    { this.creditLimit = v; }
        public String getTaxExemption()         { return taxExemption; }
        public void setTaxExemption(String v)   { this.taxExemption = v; }
        public String getBillingEmail()         { return billingEmail; }
        public void setBillingEmail(String v)   { this.billingEmail = v; }
    }

    public static class TagsBean {
        private String leadSource, campaign, notes;
        private LocalDate firstContactDate;
        public String getLeadSource()               { return leadSource; }
        public void setLeadSource(String v)         { this.leadSource = v; }
        public String getCampaign()                 { return campaign; }
        public void setCampaign(String v)           { this.campaign = v; }
        public LocalDate getFirstContactDate()      { return firstContactDate; }
        public void setFirstContactDate(LocalDate v){ this.firstContactDate = v; }
        public String getNotes()                    { return notes; }
        public void setNotes(String v)              { this.notes = v; }
    }
}
