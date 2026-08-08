package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.i18n.Caption;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueList;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import com.vaadin.flow.theme.lumo.LumoIcon;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Factory that creates the "New Customer" 5-step WizardFrame demo.
 * Uses the fluent WizardFrame.builder() API -- no manual CSS classes, no raw Vaadin components.
 */
final class WizardMockupFactory {

    private WizardMockupFactory() {}

    // ── Step beans ──────────────────────────────────────────────────────────

    public static class ContactBean {
        @NotBlank(message = "First name is required")
        @Caption("First Name")
        private String firstName;
        @NotBlank(message = "Last name is required")
        private String lastName;
        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email")
        private String email;
        private String phone;
        private String jobTitle;
        private String department;

        public String getFirstName() {return firstName;}

        public void setFirstName(String v) {this.firstName = v;}

        public String getLastName() {return lastName;}

        public void setLastName(String v) {this.lastName = v;}

        public String getEmail() {return email;}

        public void setEmail(String v) {this.email = v;}

        public String getPhone() {return phone;}

        public void setPhone(String v) {this.phone = v;}

        public String getJobTitle() {return jobTitle;}

        public void setJobTitle(String v) {this.jobTitle = v;}

        public String getDepartment() {return department;}

        public void setDepartment(String v) {this.department = v;}
    }

    public static class CompanyBean {
        @NotBlank(message = "Company name is required")
        private String companyName;
        private String industry;
        private String companySize;
        private String website;
        @NotBlank(message = "Account owner is required")
        private String accountOwner;
        private String customerSource;

        public String getCompanyName() {return companyName;}

        public void setCompanyName(String v) {this.companyName = v;}

        public String getIndustry() {return industry;}

        public void setIndustry(String v) {this.industry = v;}

        public String getCompanySize() {return companySize;}

        public void setCompanySize(String v) {this.companySize = v;}

        public String getWebsite() {return website;}

        public void setWebsite(String v) {this.website = v;}

        public String getAccountOwner() {return accountOwner;}

        public void setAccountOwner(String v) {this.accountOwner = v;}

        public String getCustomerSource() {return customerSource;}

        public void setCustomerSource(String v) {this.customerSource = v;}
    }

    public static class AddressBean {
        private String street;
        private String city;
        private String state;
        private String zip;
        private String country;
        @Size(max = 500)
        private String internalNotes;

        public String getStreet() {return street;}

        public void setStreet(String v) {this.street = v;}

        public String getCity() {return city;}

        public void setCity(String v) {this.city = v;}

        public String getState() {return state;}

        public void setState(String v) {this.state = v;}

        public String getZip() {return zip;}

        public void setZip(String v) {this.zip = v;}

        public String getCountry() {return country;}

        public void setCountry(String v) {this.country = v;}

        public String getInternalNotes() {return internalNotes;}

        public void setInternalNotes(String v) {this.internalNotes = v;}
    }

    public static class TermsBean {
        private String paymentTerms;
        private String currency;
        private String creditLimit;
        private String preferredPaymentMethod;

        public String getPaymentTerms() {return paymentTerms;}

        public void setPaymentTerms(String v) {this.paymentTerms = v;}

        public String getCurrency() {return currency;}

        public void setCurrency(String v) {this.currency = v;}

        public String getCreditLimit() {return creditLimit;}

        public void setCreditLimit(String v) {this.creditLimit = v;}

        public String getPreferredPaymentMethod() {return preferredPaymentMethod;}

        public void setPreferredPaymentMethod(String v) {this.preferredPaymentMethod = v;}
    }

    // ── Factory method ───────────────────────────────────────────────────────

    static WizardFrame create() {
        // Step 1 — Contact details
        EntityFormPanel<ContactBean> step1 = EntityFormPanel.bean(ContactBean.class)
                .title("Contact Information")
//                .required("firstName", "First name is required")
                .required("lastName",  "Last name is required")
                .required("email",     "Email is required")
                .withPostProcessor((component, property, input) -> {
                    if (property.getName().equalsIgnoreCase("phone")) {
                        input.hasPlaceholder().ifPresent(p -> p.setPlaceholder("+1 (555) 000-0000"));
                    }
                })
                .noFooter()
                .build();
        step1.setBean(new ContactBean());

        // Step 2 — Company details
        EntityFormPanel<CompanyBean> step2 = EntityFormPanel.<CompanyBean>bean(CompanyBean.class)
                .required("companyName",   "Company name is required")
                .required("accountOwner",  "Account owner is required")
                .bind("industry",      Input.singleSelect(String.class)
                        .items("Technology", "Finance", "Healthcare", "Retail",
                               "Manufacturing", "Education", "Government", "Other").build())
                .bind("companySize",   Input.singleSelect(String.class)
                        .items("1–10", "11–50", "51–200", "201–1000", "1000+").build())
                .bind("customerSource", Input.singleSelect(String.class)
                        .items("Inbound", "Outbound", "Referral", "Partner", "Event", "Other").build())
                .noFooter()
                .build();
        step2.setBean(new CompanyBean());

        // Step 3 — Billing address
        EntityFormPanel<AddressBean> step3 = EntityFormPanel.bean(AddressBean.class)
                .noFooter()
                .build();
        step3.setBean(new AddressBean());

        // Step 4 — Payment terms
        EntityFormPanel<TermsBean> step4 = EntityFormPanel.<TermsBean>bean(TermsBean.class)
                .bind("paymentTerms",          Input.singleSelect(String.class)
                        .items("Immediate", "Net 15", "Net 30", "Net 60", "Net 90").build())
                .bind("currency",              Input.singleSelect(String.class)
                        .items("USD", "EUR", "GBP", "AUD", "CAD", "JPY").build())
                .bind("preferredPaymentMethod", Input.singleSelect(String.class)
                        .items("Bank Transfer", "Credit Card", "PayPal", "Check", "Direct Debit").build())
                .noFooter()
                .build();
        step4.setBean(new TermsBean());

        // Step 5 — Review summary (rebuilt each time user navigates to it)
        KeyValueList review = new KeyValueList().asFields();

        // ── Header action buttons ─────────────────────────────────────────────
        // Built ahead of time so we can wire the click listener after build()
        var cancelBtn = Components.button().text("Cancel").build();
        var createBtn = Components.button().text("Create Customer").primary().build();

        // Cancel: for demo purposes, show a notification (in production: navigate away)
        cancelBtn.addClickListener(e -> {
            Notification n = Notification.show("Wizard cancelled");
            n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        });

        WizardFrame wizard = WizardFrame.builder()
                .configureHeader(header -> {
                                     header.setAvatar("New Customer", AvatarVariant.LUMO_LARGE);
                                     header.setHeading(
                                             createHeading()
                                     );
                                 }
                )
                // Breadcrumb trail above the heading
                .breadcrumb(
                        new BreadcrumbItem("Customers", com.holonplatform.vaadin.flow.demo.ui.views.WizardDesktopDemoView.class),
                        new BreadcrumbPage("New Customer"))
                // "New" badge pill + action buttons in the header actions slot
                .headerActions(cancelBtn, createBtn)
                .step("Contact", step1)
                .step("Company", step2)
                .step("Address", step3)
                .step("Terms", step4)
                .step("Review", "Create Customer", review)
                .onStepChanged(idx -> {
                    if (idx == 4) {
                        review.clearItems()
                                .addFromBean(step1.getBean(false))
                                .addFromBean(step2.getBean(false))
                                .addFromBean(step3.getBean(false))
                                .addFromBean(step4.getBean(false));
                    }
                })
                .onFinish(wf -> {
                    ContactBean c = step1.getBean(false);
                    String name = blank(c.getFirstName()) + " " + blank(c.getLastName());
                    Notification n = Notification.show("Customer \"" + name.trim() + "\" created!");
                    n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                })
                .finishButton(b -> b.primary())
                .build();

        // Wire header "Create Customer" button: jump to review step; on review, trigger finish
        createBtn.addClickListener(e -> {
            if (wizard.getCurrentStep() < wizard.getStepCount() - 1) {
                wizard.setCurrentStep(wizard.getStepCount() - 1);
            } else {
                // Already on review — same action as footer "Create Customer" button
                ContactBean c = step1.getBean(false);
                String name = blank(c.getFirstName()) + " " + blank(c.getLastName());
                Notification n = Notification.show("Customer \"" + name.trim() + "\" created!");
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });

        return wizard;
    }

    private static Component createHeading() {
        return Components.layout()
                .add(
                        Components.h3().text("New Customer").build(
                        ))
                .add(
                        Components.iconBadge(LumoIcon.PLUS.create())
                                .size(IconBadge.Size.XS)
                                .variant(Alert.Variant.INFO)
                                .text("New")
                .build()
                )
                .horizontal()

                .build();
    }

    private static String blank(String value) {
        return (value != null && !value.isBlank()) ? value : "--";
    }
}
