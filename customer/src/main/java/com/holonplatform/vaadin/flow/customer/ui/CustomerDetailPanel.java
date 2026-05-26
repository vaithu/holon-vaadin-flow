package com.holonplatform.vaadin.flow.customer.ui;

import com.holonplatform.vaadin.flow.components.builders.TabSheetBuilder;
import com.holonplatform.vaadin.flow.customer.entity.Customer;
import com.holonplatform.vaadin.flow.customer.i18n.CustomerI18n;
import com.holonplatform.vaadin.flow.customer.service.CustomerService;
import com.holonplatform.vaadin.flow.customer.ui.dialog.CustomerFormDialog;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueItem;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueList;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbPage;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbSeparator;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.Optional;

/**
 * Self-contained customer master-detail panel — drop it into any Vaadin view.
 *
 * <pre>{@code
 * // Minimal usage — full CRUD with master list + detail panel
 * add(new CustomerDetailPanel(customerService));
 *
 * // Pre-select a specific customer
 * add(new CustomerDetailPanel(customerService, customerId));
 * }</pre>
 *
 * <p>Renders a responsive {@link MasterDetailLayout}: a searchable master list on the left
 * and a tabbed detail panel on the right. Fully self-contained with breadcrumb, CRUD actions,
 * and reactive sync — no {@code @Route} or Spring annotations required.</p>
 */
@StyleSheet("context://customer.css")
public class CustomerDetailPanel extends Div {

    private final CustomerService customerService;

    // ── Master grid ───────────────────────────────────────────────────────────
    private Grid<Customer> masterGrid;
    private String         masterSearchText = "";

    // ── Detail header (created once, updated via withDetailSync) ─────────────
    private final Header detailHeader = new Header("", HeadingLevel.H3);

    // ── Read-only KeyValueLists — one per tab (updated via withDetailSync) ────
    private final KeyValueList identityKvl  = new KeyValueList();
    private final KeyValueList contactKvl   = new KeyValueList();
    private final KeyValueList addressKvl   = new KeyValueList();
    private final KeyValueList shippingKvl  = new KeyValueList();
    private final KeyValueList businessKvl  = new KeyValueList();
    private final Div          notesDiv     = new Div();

    // ── Breadcrumb row ────────────────────────────────────────────────────────
    private final Div breadcrumbRow = new Div();

    // ── MDL reference ─────────────────────────────────────────────────────────
    private MasterDetailLayout<Customer> masterDetail;

    // ── State ─────────────────────────────────────────────────────────────────
    private Customer currentCustomer;
    private Long     initialCustomerId;

    // ── Constructors ──────────────────────────────────────────────────────────

    /** Creates the panel with no pre-selected customer. */
    public CustomerDetailPanel(CustomerService customerService) {
        this(customerService, null);
    }

    /**
     * Creates the panel and pre-selects the given customer.
     *
     * @param customerService the service
     * @param initialCustomerId ID of the customer to select on first render, or {@code null}
     */
    public CustomerDetailPanel(CustomerService customerService, Long initialCustomerId) {
        this.customerService   = customerService;
        this.initialCustomerId = initialCustomerId;
        addClassName("customer-detail-view");
        setSizeFull();
        buildLayout();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Programmatically selects a customer by ID after the panel is already attached.
     *
     * @param customerId the customer ID to select
     */
    public void selectCustomer(Long customerId) {
        if (masterDetail != null) {
            masterDetail.restoreSelection(String.valueOf(customerId));
        } else {
            this.initialCustomerId = customerId;
        }
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private void buildLayout() {

        // ── Master grid ───────────────────���───────────────────────────────────
        masterGrid = new Grid<>(Customer.class, false);
        masterGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        masterGrid.addComponentColumn(this::buildMasterRowCell)
                .setHeader("")
                .setFlexGrow(1)
                .setAutoWidth(true);
        masterGrid.setItems(
                DataProvider.fromCallbacks(
                        q -> customerService.fetch(q.getOffset(), q.getLimit(), masterSearchText),
                        q -> (int) customerService.count(masterSearchText)));

        // ── Master search ─────────────────────────────────────────────────────
        var masterSearch = new TextField();
        masterSearch.setPlaceholder(getTranslation(CustomerI18n.VIEW_SEARCH));
        masterSearch.setClearButtonVisible(true);
        masterSearch.addValueChangeListener(ev -> {
            masterSearchText = ev.getValue();
            masterGrid.getDataProvider().refreshAll();
        });

        // ── Master header with "New Customer" button ──────────────────────────
        var masterHeader = new GridHeader(getTranslation(CustomerI18n.VIEW_TITLE));
        var newBtnMaster = new Button(
                getTranslation(CustomerI18n.VIEW_NEW), VaadinIcon.PLUS.create());
        newBtnMaster.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        newBtnMaster.addClickListener(e -> openFormDialog(null));
        masterHeader.setActions(newBtnMaster);

        // ── Detail header action buttons ──────────────────────────────────────
        var editBtn = new Button(getTranslation(CustomerI18n.VIEW_EDIT),
                VaadinIcon.EDIT.create());
        editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editBtn.addClickListener(e -> {
            if (currentCustomer != null) openFormDialog(currentCustomer);
        });

        var deleteBtn = new Button(getTranslation(CustomerI18n.VIEW_DELETE),
                VaadinIcon.TRASH.create());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_ERROR);
        deleteBtn.addClickListener(e -> {
            if (currentCustomer != null) confirmDelete(currentCustomer);
        });

        detailHeader.setHeadingFontSize(Font.Size.LARGE);
        detailHeader.setActions(editBtn, deleteBtn);

        // ── Detail body tabs ──────────────────────────────────────────────────
        notesDiv.addClassName("customer-detail__tab-notes");
        var detailBody = buildDetailTabs();

        // ── Breadcrumb row ─────────────────────────────────────────────────────
        breadcrumbRow.addClassName("customer-detail__breadcrumb-row");
        refreshBreadcrumb(null);

        // ── MasterDetailLayout ─────────────────────────────────────────────────
        masterDetail = MasterDetailLayout.<Customer>builder()
                .masterHeader(masterHeader)
                .masterSearch(masterSearch)
                .masterGrid(masterGrid)
                .detailHeader(detailHeader)
                .detailContent(c -> new Component[]{ detailBody })
                .itemId(c -> String.valueOf(c.getId()),
                        id -> {
                            try {
                                return customerService.findById(Long.parseLong(id));
                            } catch (NumberFormatException ignored) {
                                return Optional.empty();
                            }
                        })
                .mobileSheetTitle(getTranslation(CustomerI18n.VIEW_TITLE))
                .autoSelectFirst(initialCustomerId == null)
                .onDataChanged(() -> masterGrid.getDataProvider().refreshAll())
                .withDetailSync(detailHeader, c -> {
                    currentCustomer = c;
                    detailHeader.setHeading(c.getDisplayName());
                    detailHeader.setDetails(buildStatusSpan(c), buildTypeSpan(c));
                })
                .withDetailSync(breadcrumbRow, this::refreshBreadcrumb)
                .withDetailSync(identityKvl,  this::updateIdentityKvl)
                .withDetailSync(contactKvl,   this::updateContactKvl)
                .withDetailSync(addressKvl,   this::updateAddressKvl)
                .withDetailSync(shippingKvl,  this::updateShippingKvl)
                .withDetailSync(businessKvl,  this::updateBusinessKvl)
                .withDetailSync(notesDiv,      this::updateNotesDiv)
                .build();

        masterDetail.setSizeFull();
        masterDetail.getResponsiveLayout().withSeparator();

        add(breadcrumbRow, masterDetail);

        // Pre-select if an initial ID was provided
        if (initialCustomerId != null) {
            masterDetail.restoreSelection(String.valueOf(initialCustomerId));
        }
    }

    // ── Detail tabs ────────────────────────────────────────────────────────────

    private Component buildDetailTabs() {
        var identityTab = new Div(identityKvl);
        identityTab.addClassName("customer-detail__tab-content");

        var contactTab = new Div(contactKvl);
        contactTab.addClassName("customer-detail__tab-content");

        var addressTab = new Div(addressKvl);
        addressTab.addClassName("customer-detail__tab-content");

        var shippingTab = new Div(shippingKvl);
        shippingTab.addClassName("customer-detail__tab-content");

        var businessTab = new Div(businessKvl);
        businessTab.addClassName("customer-detail__tab-content");

        var notesTab = new Div(notesDiv);
        notesTab.addClassName("customer-detail__tab-content");

        return TabSheetBuilder.create()
                .withTab(new Icon(VaadinIcon.USER),    getTranslation(CustomerI18n.SECTION_IDENTITY), identityTab)
                .withTab(new Icon(VaadinIcon.PHONE),   getTranslation(CustomerI18n.SECTION_CONTACT),  contactTab)
                .withTab(new Icon(VaadinIcon.HOME_O),  getTranslation(CustomerI18n.SECTION_BILLING),  addressTab)
                .withTab(new Icon(VaadinIcon.PACKAGE), getTranslation(CustomerI18n.SECTION_SHIPPING), shippingTab)
                .withTab(new Icon(VaadinIcon.OFFICE),  getTranslation(CustomerI18n.SECTION_BUSINESS), businessTab)
                .withTab(new Icon(VaadinIcon.PENCIL),  getTranslation(CustomerI18n.SECTION_NOTES),    notesTab)
                .build();
    }

    // ── Sync handlers ─────────────────────────────────────────────────────────

    private void updateIdentityKvl(Customer c) {
        identityKvl.clearItems();
        if (c.getSalutation() != null && c.getSalutation() != com.holonplatform.vaadin.flow.customer.entity.CustomerSalutation.NONE) {
            identityKvl.addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_SALUTATION),
                    getTranslation(CustomerI18n.salutationKey(c.getSalutation()))));
        }
        identityKvl
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_FIRST_NAME),   nvl(c.getFirstName())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_LAST_NAME),    nvl(c.getLastName())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_COMPANY_NAME), nvl(c.getCompanyName())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_JOB_TITLE),    nvl(c.getJobTitle())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_DEPARTMENT),   nvl(c.getDepartment())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_TYPE),
                        getTranslation(CustomerI18n.typeKey(c.getType()))))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_STATUS),
                        getTranslation(CustomerI18n.statusKey(c.getStatus()))));
    }

    private void updateContactKvl(Customer c) {
        contactKvl.clearItems();
        contactKvl
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_EMAIL),   nvl(c.getEmail())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_PHONE),   nvl(c.getPhone())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_MOBILE),  nvl(c.getMobile())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_FAX),     nvl(c.getFax())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_WEBSITE), nvl(c.getWebsite())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_SKYPE),   nvl(c.getSkype())));
    }

    private void updateAddressKvl(Customer c) {
        addressKvl.clearItems();
        // Billing
        addressKvl
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_ATTENTION),     nvl(c.getBillingAttention())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_ADDRESS_LINE1), nvl(c.getAddressLine1())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_ADDRESS_LINE2), nvl(c.getAddressLine2())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_CITY),          nvl(c.getCity())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_STATE),         nvl(c.getState())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_POSTAL_CODE),   nvl(c.getPostalCode())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_COUNTRY),       nvl(c.getCountry())));
    }

    private void updateShippingKvl(Customer c) {
        shippingKvl.clearItems();
        shippingKvl
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_ATTENTION),     nvl(c.getShippingAttention())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_ADDRESS_LINE1), nvl(c.getShippingAddressLine1())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_ADDRESS_LINE2), nvl(c.getShippingAddressLine2())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_CITY),          nvl(c.getShippingCity())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_STATE),         nvl(c.getShippingState())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_POSTAL_CODE),   nvl(c.getShippingPostalCode())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_COUNTRY),       nvl(c.getShippingCountry())));
    }

    private void updateBusinessKvl(Customer c) {
        businessKvl.clearItems();
        businessKvl
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_CUSTOMER_CODE), nvl(c.getCustomerCode())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_PAYMENT_TERMS),
                        c.getPaymentTerms() != null ? getTranslation(CustomerI18n.paymentTermsKey(c.getPaymentTerms())) : "—"))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_TAX_ID),    nvl(c.getTaxId())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_VAT_NUMBER), nvl(c.getVatNumber())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_SEGMENT),
                        c.getSegment() != null
                                ? getTranslation(CustomerI18n.segmentKey(c.getSegment())) : "—"))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_INDUSTRY),   nvl(c.getIndustry())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_CURRENCY),   nvl(c.getCurrency())))
                .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_LANGUAGE),   nvl(c.getLanguage())));
    }

    private void updateNotesDiv(Customer c) {
        notesDiv.removeAll();
        var kvl = new KeyValueList();
        kvl.addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_NOTES),       nvl(c.getNotes())))
           .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_TAGS),        nvl(c.getTags())))
           .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_REFERRED_BY), nvl(c.getReferredBy())))
           .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_CREATED_AT),
                   c.getCreatedAt() != null ? c.getCreatedAt().toString() : "—"))
           .addItem(new KeyValueItem(getTranslation(CustomerI18n.FIELD_UPDATED_AT),
                   c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : "—"));
        notesDiv.add(kvl);
    }

    private void refreshBreadcrumb(Customer c) {
        breadcrumbRow.removeAll();
        var crumb = new Breadcrumb();
        if (c != null) {
            crumb.addWithSeparators(
                    new BreadcrumbItem(new Anchor("/", getTranslation(CustomerI18n.BREADCRUMB_HOME))),
                    new BreadcrumbItem(new Anchor("customers", getTranslation(CustomerI18n.VIEW_TITLE))),
                    new BreadcrumbPage(c.getDisplayName())
            );
        } else {
            crumb.addWithSeparators(
                    new BreadcrumbItem(new Anchor("/", getTranslation(CustomerI18n.BREADCRUMB_HOME))),
                    new BreadcrumbSeparator(),
                    new BreadcrumbPage(getTranslation(CustomerI18n.VIEW_TITLE))
            );
        }
        breadcrumbRow.add(crumb);
    }

    // ── Master row cell ────────────────────────────────────────────────────────

    private Div buildMasterRowCell(Customer c) {
        var initials = buildAvatarDiv(c);

        var nameLbl = new Span(c.getDisplayName());
        nameLbl.addClassName("customer-master__row-name");

        var sub = new StringBuilder();
        if (c.getCompanyName() != null && !c.getCompanyName().isBlank()) {
            sub.append(c.getCompanyName());
        } else if (c.getJobTitle() != null && !c.getJobTitle().isBlank()) {
            sub.append(c.getJobTitle());
        }
        if (!c.formatAddress().isBlank()) {
            if (!sub.isEmpty()) sub.append(" · ");
            sub.append(c.formatAddress());
        }
        var subLbl = new Span(sub.isEmpty() ? nvl(c.getEmail()) : sub.toString());
        subLbl.addClassName("customer-master__row-sub");

        var info = new Div(nameLbl, subLbl);
        info.addClassName("customer-master__row-info");

        var statusBadge = new Span(getTranslation(CustomerI18n.statusKey(c.getStatus())));
        statusBadge.addClassName("customer-status-badge");
        statusBadge.addClassName("customer-status-badge--" + c.getStatus().name().toLowerCase());

        var cell = new Div(initials, info, statusBadge);
        cell.addClassName("customer-master__row-cell");
        return cell;
    }

    private Div buildAvatarDiv(Customer c) {
        String name     = c.getDisplayName();
        String initials = name != null && name.length() >= 2
                ? name.substring(0, 2).toUpperCase()
                : (name != null && name.length() == 1 ? name.toUpperCase() : "?");
        int colorIdx    = Math.abs(name != null ? name.hashCode() : 0) % 6;
        var div         = new Div(new Span(initials));
        div.addClassName("customer-avatar");
        div.addClassName("customer-avatar--" + colorIdx);
        return div;
    }

    // ── Detail badge helpers ───────────────────────────────────────────────────

    private Span buildStatusSpan(Customer c) {
        var span = new Span(getTranslation(CustomerI18n.statusKey(c.getStatus())));
        span.addClassName("customer-status-badge");
        span.addClassName("customer-status-badge--" + c.getStatus().name().toLowerCase());
        return span;
    }

    private Span buildTypeSpan(Customer c) {
        var span = new Span(getTranslation(CustomerI18n.typeKey(c.getType())));
        span.addClassName("customer-type-badge");
        span.addClassName("customer-type-badge--" + c.getType().name().toLowerCase());
        return span;
    }

    // ── CRUD helpers ──────────────────────────────────────────────────────────

    private void openFormDialog(Customer existing) {
        new CustomerFormDialog(customerService, existing, () -> masterDetail.notifyDataChanged()).open();
    }

    private void confirmDelete(Customer customer) {
        var confirm = new ConfirmDialog();
        confirm.setHeader(getTranslation(CustomerI18n.CONFIRM_DELETE_HDR));
        confirm.setText(getTranslation(CustomerI18n.CONFIRM_DELETE_TEXT, customer.getDisplayName()));
        confirm.setCancelable(true);
        confirm.setConfirmText(getTranslation(CustomerI18n.CONFIRM_DELETE_BTN));
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(ev -> {
            String name = customer.getDisplayName();
            try {
                customerService.delete(customer);
                currentCustomer = null;
                masterDetail.clearSelection();
                masterDetail.notifyDataChanged();
                var n = Notification.show(
                        getTranslation(CustomerI18n.NOTIFY_DELETED, name),
                        3000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                var n = Notification.show(
                        getTranslation(CustomerI18n.NOTIFY_ERROR, ex.getMessage()),
                        5000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        confirm.open();
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private static String nvl(String value) {
        return (value != null && !value.isBlank()) ? value : "—";
    }
}










