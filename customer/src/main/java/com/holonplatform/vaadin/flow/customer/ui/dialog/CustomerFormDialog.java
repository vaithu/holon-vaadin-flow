package com.holonplatform.vaadin.flow.customer.ui.dialog;

import com.holonplatform.vaadin.flow.components.builders.TabSheetBuilder;
import com.holonplatform.vaadin.flow.customer.entity.*;
import com.holonplatform.vaadin.flow.customer.i18n.CustomerI18n;
import com.holonplatform.vaadin.flow.customer.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * Reusable Create / Edit dialog for {@link Customer} records.
 *
 * <p>A tabbed {@link Dialog} backed by a {@link Binder}. Modelled after the
 * Zoho Books "New Contact" form structure:
 * <ul>
 *   <li><b>Other Details</b> — customer code, payment terms, currency, language, website, department, skype</li>
 *   <li><b>Identity</b>      — type, salutation, name, company, job title</li>
 *   <li><b>Contact</b>       — email, work phone, mobile, fax</li>
 *   <li><b>Billing Address</b>  — attention, address, city, state, postal code, country</li>
 *   <li><b>Shipping Address</b> — same fields + "Same as Billing" shortcut</li>
 *   <li><b>Business</b>     — tax ID, VAT, segment, industry</li>
 *   <li><b>Notes &amp; Tags</b> — notes, tags, referred by</li>
 * </ul>
 */
public class CustomerFormDialog extends Dialog {

    private final CustomerService  service;
    private final Customer         customer;
    private final Runnable         onSaved;
    private final Binder<Customer> binder = new Binder<>(Customer.class);

    // ── Identity / Core fields ────────────────────────────────────────────────
    private final Select<CustomerType>       typeSelect        = new Select<>();
    private final Select<CustomerSalutation> salutationSelect  = new Select<>();
    private final TextField                  firstNameField    = new TextField();
    private final TextField                  lastNameField     = new TextField();
    private final TextField                  displayNameField  = new TextField();
    private final Select<CustomerStatus>     statusSelect      = new Select<>();
    private final TextField                  companyNameField  = new TextField();
    private final TextField                  jobTitleField     = new TextField();
    private final TextField                  departmentField   = new TextField();

    // ── Other Details ─────────────────────────────────────────────────────────
    private final TextField                      customerCodeField  = new TextField();
    private final Select<CustomerPaymentTerms>   paymentTermsSelect = new Select<>();
    private final TextField                      currencyField      = new TextField();
    private final TextField                      languageField      = new TextField();
    private final TextField                      websiteField       = new TextField();
    private final TextField                      skypeField         = new TextField();

    // ── Contact fields ────────────────────────────────────────────────────────
    private final EmailField emailField   = new EmailField();
    private final TextField  phoneField   = new TextField();
    private final TextField  mobileField  = new TextField();
    private final TextField  faxField     = new TextField();

    // ── Billing Address ───────────────────────────────────────────────────────
    private final TextField billingAttentionField = new TextField();
    private final TextField addrLine1Field        = new TextField();
    private final TextField addrLine2Field        = new TextField();
    private final TextField cityField             = new TextField();
    private final TextField stateField            = new TextField();
    private final TextField postalCodeField       = new TextField();
    private final TextField countryField          = new TextField();

    // ── Shipping Address ──────────────────────────────────────────────────────
    private final TextField shippingAttentionField  = new TextField();
    private final TextField shippingLine1Field      = new TextField();
    private final TextField shippingLine2Field      = new TextField();
    private final TextField shippingCityField       = new TextField();
    private final TextField shippingStateField      = new TextField();
    private final TextField shippingPostalField     = new TextField();
    private final TextField shippingCountryField    = new TextField();

    // ── Business ─────────────────────────────────────────────────────────────
    private final TextField               taxIdField       = new TextField();
    private final TextField               vatNumberField   = new TextField();
    private final Select<CustomerSegment> segmentSelect    = new Select<>();
    private final TextField               industryField    = new TextField();

    // ── Notes & Tags ──────────────────────────────────────────────────────────
    private final TextArea  notesArea       = new TextArea();
    private final TextField tagsField       = new TextField();
    private final TextField referredByField = new TextField();

    // ── Constructor ───────────────────────────────────────────────────────────

    public CustomerFormDialog(CustomerService service, Customer customer, Runnable onSaved) {
        this.service  = service;
        this.customer = (customer != null) ? customer : new Customer();
        this.onSaved  = onSaved;
        buildDialog();
    }

    // ── Dialog construction ───────────────────────────────────────────────────

    private void buildDialog() {
        boolean isNew = (customer.getId() == null);

        setHeaderTitle(isNew
                ? getTranslation(CustomerI18n.VIEW_NEW)
                : getTranslation(CustomerI18n.VIEW_EDIT) + " · " + customer.getDisplayName());
        setCloseOnOutsideClick(false);
        setDraggable(true);
        setWidth("820px");
        setMaxHeight("90vh");
        addClassName("customer-form-dialog");

        var tabSheet = TabSheetBuilder.create()
                .withTab(new Icon(VaadinIcon.USER),    getTranslation(CustomerI18n.SECTION_IDENTITY),  buildIdentitySection())
                .withTab(new Icon(VaadinIcon.PHONE),   getTranslation(CustomerI18n.SECTION_CONTACT),   buildContactSection())
                .withTab(new Icon(VaadinIcon.HOME),    getTranslation(CustomerI18n.SECTION_BILLING),   buildBillingSection())
                .withTab(new Icon(VaadinIcon.PACKAGE), getTranslation(CustomerI18n.SECTION_SHIPPING),  buildShippingSection())
                .withTab(new Icon(VaadinIcon.OFFICE),  getTranslation(CustomerI18n.SECTION_BUSINESS),  buildBusinessSection())
                .withTab(new Icon(VaadinIcon.COG_O),   getTranslation(CustomerI18n.SECTION_OTHER),     buildOtherSection())
                .withTab(new Icon(VaadinIcon.PENCIL),  getTranslation(CustomerI18n.SECTION_NOTES),     buildNotesSection())
                .build();

        add(tabSheet);
        configureBinder();

        if (isNew) {
            var blank = new Customer();
            blank.setType(CustomerType.INDIVIDUAL);
            blank.setStatus(CustomerStatus.PROSPECT);
            blank.setSalutation(CustomerSalutation.NONE);
            binder.setBean(blank);
        } else {
            binder.setBean(customer);
        }

        var saveBtn = new Button(getTranslation(CustomerI18n.VIEW_SAVE), VaadinIcon.CHECK.create());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addClickListener(e -> handleSave());

        var cancelBtn = new Button(getTranslation(CustomerI18n.VIEW_CANCEL));
        cancelBtn.addClickListener(e -> close());

        getFooter().add(cancelBtn, saveBtn);
    }

    // ── Section builders ──────────────────────────────────────────────────────

    /**
     * Identity tab — mirrors Zoho's top section:
     * Contact Type (radio-like select) → Salutation + Name fields → Company / Job / Department.
     */
    private Div buildIdentitySection() {
        typeSelect.setItems(CustomerType.values());
        typeSelect.setItemLabelGenerator(t -> getTranslation(CustomerI18n.typeKey(t)));
        typeSelect.setWidthFull();

        statusSelect.setItems(CustomerStatus.values());
        statusSelect.setItemLabelGenerator(s -> getTranslation(CustomerI18n.statusKey(s)));
        statusSelect.setWidthFull();

        salutationSelect.setItems(CustomerSalutation.values());
        salutationSelect.setItemLabelGenerator(s -> getTranslation(CustomerI18n.salutationKey(s)));
        salutationSelect.setWidth("120px");

        firstNameField.setWidthFull();
        lastNameField.setWidthFull();
        companyNameField.setWidthFull();
        jobTitleField.setWidthFull();
        departmentField.setWidthFull();

        displayNameField.setPlaceholder("Auto-derived from name if left blank");
        displayNameField.setHelperText(getTranslation("customer.field.display_name.help"));
        displayNameField.setWidthFull();

        // Salutation + First Name side by side
        var nameRow = new HorizontalLayout(salutationSelect, firstNameField);
        nameRow.setWidthFull();
        nameRow.setFlexGrow(1, firstNameField);
        nameRow.setSpacing(true);

        // Group the name row + last name into a pseudo 2-column spanning item
        var nameBlock = new Div(nameRow, lastNameField);
        nameBlock.setWidthFull();

        var form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("480px", 2));
        var typeItem   = form.addFormItem(typeSelect,       getTranslation(CustomerI18n.FIELD_TYPE));
        var statusItem = form.addFormItem(statusSelect,     getTranslation(CustomerI18n.FIELD_STATUS));
        var nameItem   = form.addFormItem(nameBlock,        getTranslation(CustomerI18n.FIELD_FIRST_NAME));
        var lastItem   = form.addFormItem(lastNameField,    getTranslation(CustomerI18n.FIELD_LAST_NAME));
        form.addFormItem(companyNameField, getTranslation(CustomerI18n.FIELD_COMPANY_NAME));
        form.addFormItem(jobTitleField,    getTranslation(CustomerI18n.FIELD_JOB_TITLE));
        form.addFormItem(departmentField,  getTranslation(CustomerI18n.FIELD_DEPARTMENT));
        var dnItem = form.addFormItem(displayNameField, getTranslation(CustomerI18n.FIELD_DISPLAY_NAME));
        form.setColspan(dnItem, 2);

        // Name row spans both columns; last name field is hidden (it's inside nameBlock)
        form.setColspan(nameItem, 2);
        form.remove(lastItem);

        var wrapper = new Div(form);
        wrapper.addClassName("customer-form__tab-content");
        return wrapper;
    }

    private Div buildContactSection() {
        emailField.setWidthFull();
        phoneField.setWidthFull();
        mobileField.setWidthFull();
        faxField.setWidthFull();

        var form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("480px", 2));
        form.addFormItem(emailField,  getTranslation(CustomerI18n.FIELD_EMAIL));
        form.addFormItem(phoneField,  getTranslation(CustomerI18n.FIELD_PHONE));
        form.addFormItem(mobileField, getTranslation(CustomerI18n.FIELD_MOBILE));
        form.addFormItem(faxField,    getTranslation(CustomerI18n.FIELD_FAX));

        var wrapper = new Div(form);
        wrapper.addClassName("customer-form__tab-content");
        return wrapper;
    }

    private Div buildBillingSection() {
        billingAttentionField.setWidthFull();
        addrLine1Field.setWidthFull();
        addrLine2Field.setWidthFull();
        cityField.setWidthFull();
        stateField.setWidthFull();
        postalCodeField.setWidthFull();
        countryField.setWidthFull();

        var form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("480px", 2));
        var attItem   = form.addFormItem(billingAttentionField, getTranslation(CustomerI18n.FIELD_ATTENTION));
        var line1Item = form.addFormItem(addrLine1Field,        getTranslation(CustomerI18n.FIELD_ADDRESS_LINE1));
        var line2Item = form.addFormItem(addrLine2Field,        getTranslation(CustomerI18n.FIELD_ADDRESS_LINE2));
        form.addFormItem(cityField,       getTranslation(CustomerI18n.FIELD_CITY));
        form.addFormItem(stateField,      getTranslation(CustomerI18n.FIELD_STATE));
        form.addFormItem(postalCodeField, getTranslation(CustomerI18n.FIELD_POSTAL_CODE));
        form.addFormItem(countryField,    getTranslation(CustomerI18n.FIELD_COUNTRY));
        form.setColspan(attItem, 2);
        form.setColspan(line1Item, 2);
        form.setColspan(line2Item, 2);

        var wrapper = new Div(form);
        wrapper.addClassName("customer-form__tab-content");
        return wrapper;
    }

    private Div buildShippingSection() {
        shippingAttentionField.setWidthFull();
        shippingLine1Field.setWidthFull();
        shippingLine2Field.setWidthFull();
        shippingCityField.setWidthFull();
        shippingStateField.setWidthFull();
        shippingPostalField.setWidthFull();
        shippingCountryField.setWidthFull();

        // "Same as Billing" checkbox — copies billing values into shipping fields on check
        var sameAsBilling = new Checkbox(getTranslation(CustomerI18n.SHIPPING_SAME_AS_BILLING));
        sameAsBilling.addValueChangeListener(ev -> {
            if (ev.getValue()) {
                shippingAttentionField.setValue(nvl(billingAttentionField.getValue()));
                shippingLine1Field.setValue(nvl(addrLine1Field.getValue()));
                shippingLine2Field.setValue(nvl(addrLine2Field.getValue()));
                shippingCityField.setValue(nvl(cityField.getValue()));
                shippingStateField.setValue(nvl(stateField.getValue()));
                shippingPostalField.setValue(nvl(postalCodeField.getValue()));
                shippingCountryField.setValue(nvl(countryField.getValue()));
            }
        });

        var form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("480px", 2));
        var attItem   = form.addFormItem(shippingAttentionField, getTranslation(CustomerI18n.FIELD_ATTENTION));
        var line1Item = form.addFormItem(shippingLine1Field,     getTranslation(CustomerI18n.FIELD_ADDRESS_LINE1));
        var line2Item = form.addFormItem(shippingLine2Field,     getTranslation(CustomerI18n.FIELD_ADDRESS_LINE2));
        form.addFormItem(shippingCityField,   getTranslation(CustomerI18n.FIELD_CITY));
        form.addFormItem(shippingStateField,  getTranslation(CustomerI18n.FIELD_STATE));
        form.addFormItem(shippingPostalField, getTranslation(CustomerI18n.FIELD_POSTAL_CODE));
        form.addFormItem(shippingCountryField, getTranslation(CustomerI18n.FIELD_COUNTRY));
        form.setColspan(attItem, 2);
        form.setColspan(line1Item, 2);
        form.setColspan(line2Item, 2);

        var wrapper = new Div(sameAsBilling, form);
        wrapper.addClassName("customer-form__tab-content");
        return wrapper;
    }

    private Div buildBusinessSection() {
        taxIdField.setWidthFull();
        vatNumberField.setWidthFull();
        industryField.setWidthFull();

        segmentSelect.setItems(CustomerSegment.values());
        segmentSelect.setItemLabelGenerator(seg -> getTranslation(CustomerI18n.segmentKey(seg)));
        segmentSelect.setWidthFull();

        var form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("480px", 2));
        form.addFormItem(taxIdField,     getTranslation(CustomerI18n.FIELD_TAX_ID));
        form.addFormItem(vatNumberField, getTranslation(CustomerI18n.FIELD_VAT_NUMBER));
        form.addFormItem(segmentSelect,  getTranslation(CustomerI18n.FIELD_SEGMENT));
        form.addFormItem(industryField,  getTranslation(CustomerI18n.FIELD_INDUSTRY));

        var wrapper = new Div(form);
        wrapper.addClassName("customer-form__tab-content");
        return wrapper;
    }

    private Div buildOtherSection() {
        customerCodeField.setPlaceholder("e.g. CUST-001");
        customerCodeField.setHelperText(getTranslation("customer.field.customer_code.help"));
        customerCodeField.setWidthFull();

        paymentTermsSelect.setItems(CustomerPaymentTerms.values());
        paymentTermsSelect.setItemLabelGenerator(pt -> getTranslation(CustomerI18n.paymentTermsKey(pt)));
        paymentTermsSelect.setWidthFull();

        currencyField.setHelperText(getTranslation("customer.field.currency.help"));
        currencyField.setWidthFull();
        languageField.setHelperText(getTranslation("customer.field.language.help"));
        languageField.setWidthFull();
        websiteField.setWidthFull();
        skypeField.setWidthFull();

        var form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("480px", 2));
        form.addFormItem(customerCodeField, getTranslation(CustomerI18n.FIELD_CUSTOMER_CODE));
        form.addFormItem(paymentTermsSelect, getTranslation(CustomerI18n.FIELD_PAYMENT_TERMS));
        form.addFormItem(currencyField,     getTranslation(CustomerI18n.FIELD_CURRENCY));
        form.addFormItem(languageField,     getTranslation(CustomerI18n.FIELD_LANGUAGE));
        form.addFormItem(websiteField,      getTranslation(CustomerI18n.FIELD_WEBSITE));
        form.addFormItem(skypeField,        getTranslation(CustomerI18n.FIELD_SKYPE));

        var wrapper = new Div(form);
        wrapper.addClassName("customer-form__tab-content");
        return wrapper;
    }

    private Div buildNotesSection() {
        notesArea.setMinHeight("100px");
        notesArea.setWidthFull();
        tagsField.setHelperText(getTranslation("customer.field.tags.help"));
        tagsField.setWidthFull();
        referredByField.setWidthFull();

        var form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        form.addFormItem(notesArea,       getTranslation(CustomerI18n.FIELD_NOTES));
        form.addFormItem(tagsField,       getTranslation(CustomerI18n.FIELD_TAGS));
        form.addFormItem(referredByField, getTranslation(CustomerI18n.FIELD_REFERRED_BY));

        var wrapper = new Div(form);
        wrapper.addClassName("customer-form__tab-content");
        return wrapper;
    }

    // ── Binder ────────────────────────────────────────────────────────────────

    private void configureBinder() {
        binder.forField(typeSelect).asRequired().bind(Customer::getType, Customer::setType);
        binder.forField(statusSelect).asRequired().bind(Customer::getStatus, Customer::setStatus);
        binder.forField(salutationSelect).bind(Customer::getSalutation, Customer::setSalutation);
        binder.forField(firstNameField).bind(Customer::getFirstName, Customer::setFirstName);
        binder.forField(lastNameField).bind(Customer::getLastName, Customer::setLastName);
        binder.forField(displayNameField).bind(Customer::getDisplayName, Customer::setDisplayName);
        binder.forField(companyNameField).bind(Customer::getCompanyName, Customer::setCompanyName);
        binder.forField(jobTitleField).bind(Customer::getJobTitle, Customer::setJobTitle);
        binder.forField(departmentField).bind(Customer::getDepartment, Customer::setDepartment);

        binder.forField(customerCodeField).bind(Customer::getCustomerCode, Customer::setCustomerCode);
        binder.forField(paymentTermsSelect).bind(Customer::getPaymentTerms, Customer::setPaymentTerms);
        binder.forField(currencyField).bind(Customer::getCurrency, Customer::setCurrency);
        binder.forField(languageField).bind(Customer::getLanguage, Customer::setLanguage);
        binder.forField(websiteField).bind(Customer::getWebsite, Customer::setWebsite);
        binder.forField(skypeField).bind(Customer::getSkype, Customer::setSkype);

        binder.forField(emailField).bind(Customer::getEmail, Customer::setEmail);
        binder.forField(phoneField).bind(Customer::getPhone, Customer::setPhone);
        binder.forField(mobileField).bind(Customer::getMobile, Customer::setMobile);
        binder.forField(faxField).bind(Customer::getFax, Customer::setFax);

        binder.forField(billingAttentionField).bind(Customer::getBillingAttention, Customer::setBillingAttention);
        binder.forField(addrLine1Field).bind(Customer::getAddressLine1, Customer::setAddressLine1);
        binder.forField(addrLine2Field).bind(Customer::getAddressLine2, Customer::setAddressLine2);
        binder.forField(cityField).bind(Customer::getCity, Customer::setCity);
        binder.forField(stateField).bind(Customer::getState, Customer::setState);
        binder.forField(postalCodeField).bind(Customer::getPostalCode, Customer::setPostalCode);
        binder.forField(countryField).bind(Customer::getCountry, Customer::setCountry);

        binder.forField(shippingAttentionField).bind(Customer::getShippingAttention, Customer::setShippingAttention);
        binder.forField(shippingLine1Field).bind(Customer::getShippingAddressLine1, Customer::setShippingAddressLine1);
        binder.forField(shippingLine2Field).bind(Customer::getShippingAddressLine2, Customer::setShippingAddressLine2);
        binder.forField(shippingCityField).bind(Customer::getShippingCity, Customer::setShippingCity);
        binder.forField(shippingStateField).bind(Customer::getShippingState, Customer::setShippingState);
        binder.forField(shippingPostalField).bind(Customer::getShippingPostalCode, Customer::setShippingPostalCode);
        binder.forField(shippingCountryField).bind(Customer::getShippingCountry, Customer::setShippingCountry);

        binder.forField(taxIdField).bind(Customer::getTaxId, Customer::setTaxId);
        binder.forField(vatNumberField).bind(Customer::getVatNumber, Customer::setVatNumber);
        binder.forField(segmentSelect).bind(Customer::getSegment, Customer::setSegment);
        binder.forField(industryField).bind(Customer::getIndustry, Customer::setIndustry);

        binder.forField(notesArea).bind(Customer::getNotes, Customer::setNotes);
        binder.forField(tagsField).bind(Customer::getTags, Customer::setTags);
        binder.forField(referredByField).bind(Customer::getReferredBy, Customer::setReferredBy);
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    private void handleSave() {
        if (!binder.isValid()) {
            binder.validate();
            return;
        }
        Customer target = customer;
        if (!binder.writeBeanIfValid(target)) return;

        if (target.getDisplayName() == null || target.getDisplayName().isBlank()) {
            target.setDisplayName(target.deriveDisplayName());
        }

        try {
            service.save(target);
            close();
            onSaved.run();
            showSuccess(getTranslation(CustomerI18n.NOTIFY_SAVED, target.getDisplayName()));
        } catch (Exception ex) {
            showError(getTranslation(CustomerI18n.NOTIFY_ERROR, ex.getMessage()));
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private static String nvl(String v) { return v != null ? v : ""; }

    private static void showSuccess(String text) {
        var n = Notification.show(text, 3000, Notification.Position.BOTTOM_END);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showError(String text) {
        var n = Notification.show(text, 5000, Notification.Position.BOTTOM_END);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}


