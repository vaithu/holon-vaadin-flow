package com.holonplatform.vaadin.flow.customer.ui;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.customer.entity.Customer;
import com.holonplatform.vaadin.flow.customer.entity.CustomerType;
import com.holonplatform.vaadin.flow.customer.i18n.CustomerI18n;
import com.holonplatform.vaadin.flow.customer.service.CustomerService;
import com.holonplatform.vaadin.flow.customer.ui.dialog.CustomerFormDialog;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbPage;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbSeparator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Self-contained customer listing panel — drop it into any Vaadin view.
 *
 * <pre>{@code
 * // Minimal usage
 * add(new CustomerListPanel(customerService));
 * }</pre>
 *
 * <p>Shows a paginated, searchable listing of {@link Customer} records with
 * type/status badges, inline create/edit/delete actions, and breadcrumb navigation.</p>
 */
@StyleSheet("context://customer.css")
public class CustomerListPanel extends Div {

    private final CustomerService customerService;

    /** Refreshes the grid after a create / edit / delete operation. */
    private Runnable refreshGrid;

    // ── Constructor ───────────────────────────────────────────────────────────

    public CustomerListPanel(CustomerService customerService) {
        this.customerService = customerService;
        addClassName("customer-list-view");
        buildLayout();
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private void buildLayout() {

        // ── Breadcrumb ────────────────────────────────────────────────────────
        var breadcrumb = new Breadcrumb(
                new BreadcrumbItem(new Anchor("/", getTranslation(CustomerI18n.BREADCRUMB_HOME))),
                new BreadcrumbSeparator(),
                new BreadcrumbPage(getTranslation(CustomerI18n.VIEW_TITLE))
        );

        // ── Page header ────────────────────────────────────────────────────────
        var title    = new H2(getTranslation(CustomerI18n.VIEW_TITLE));
        title.addClassName("customer-page-title");

        var subtitle = new Paragraph(getTranslation(CustomerI18n.VIEW_SUBTITLE));
        subtitle.addClassName("customer-page-subtitle");

        var newBtn = new Button(
                getTranslation(CustomerI18n.VIEW_NEW),
                VaadinIcon.PLUS.create());
        newBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newBtn.addClickListener(e -> openFormDialog(null));

        var headerActions = new Div(title, newBtn);
        headerActions.addClassName("customer-page-header");

        // ── Listing bundle ─────────────────────────────────────────────────────
        var bundle = Components.listing(Customer.class)
                .columns("displayName", "type", "status", "email", "phone")
                .header("displayName", getTranslation(CustomerI18n.FIELD_DISPLAY_NAME))
                .header("type",        getTranslation(CustomerI18n.FIELD_TYPE))
                .header("status",      getTranslation(CustomerI18n.FIELD_STATUS))
                .header("email",       getTranslation(CustomerI18n.FIELD_EMAIL))
                .header("phone",       getTranslation(CustomerI18n.FIELD_PHONE))
                .pageSizes(10, 25, 50)
                .defaultPageSize(10)
                .search(getTranslation(CustomerI18n.VIEW_SEARCH))
                .fetch((q, text) -> customerService.fetch(q.getOffset(), q.getLimit(), text))
                .build();

        // Override the displayName column with a rich component cell
        bundle.listing().addComponentColumn(this::buildNameCell)
                .setHeader(getTranslation(CustomerI18n.FIELD_DISPLAY_NAME))
                .setFlexGrow(2)
                .setWidth("220px");

        // ── Component columns: type and status badges ─────────────────────────
        bundle.listing().addComponentColumn(this::buildTypeCell)
                .setHeader(getTranslation(CustomerI18n.FIELD_TYPE))
                .setWidth("130px")
                .setFlexGrow(0);

        bundle.listing().addComponentColumn(this::buildStatusCell)
                .setHeader(getTranslation(CustomerI18n.FIELD_STATUS))
                .setWidth("120px")
                .setFlexGrow(0);

        // ── Actions column ─────────────────────────────────────────────────────
        bundle.listing().addComponentColumn(this::buildActionsCell)
                .setHeader("")
                .setWidth("120px")
                .setFlexGrow(0)
                .setFrozenToEnd(true);

        refreshGrid = () -> bundle.listing().getDataProvider().refreshAll();

        add(breadcrumb, headerActions, subtitle,
                bundle.toolbar(), bundle.grid(), bundle.footer());
    }

    // ── Cell builders ─────────────────────────────────────────────────────────

    private Div buildNameCell(Customer c) {
        var nameSpan = new Span(c.getDisplayName());
        nameSpan.addClassName("customer-list__name-primary");
        nameSpan.getElement().addEventListener("click",
                ev -> UI.getCurrent().navigate("customers/" + c.getId()));

        var sub     = resolveSubtitle(c);
        var subSpan = new Span(sub);
        subSpan.addClassName("customer-list__name-secondary");

        var cell = new Div(nameSpan, subSpan);
        cell.addClassName("customer-list__name-cell");
        return cell;
    }

    private Span buildTypeCell(Customer c) {
        var badge = new Span(getTranslation(CustomerI18n.typeKey(c.getType())));
        badge.addClassName("customer-type-badge");
        badge.addClassName("customer-type-badge--" + c.getType().name().toLowerCase());
        return badge;
    }

    private Span buildStatusCell(Customer c) {
        var badge = new Span(getTranslation(CustomerI18n.statusKey(c.getStatus())));
        badge.addClassName("customer-status-badge");
        badge.addClassName("customer-status-badge--" + c.getStatus().name().toLowerCase());
        return badge;
    }

    private Div buildActionsCell(Customer c) {
        var viewBtn = new Button(VaadinIcon.ARROW_RIGHT.create());
        viewBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_SMALL);
        viewBtn.setAriaLabel("View " + c.getDisplayName());
        viewBtn.addClickListener(ev -> UI.getCurrent().navigate("customers/" + c.getId()));

        var editBtn = new Button(VaadinIcon.EDIT.create());
        editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_SMALL);
        editBtn.setAriaLabel("Edit " + c.getDisplayName());
        editBtn.addClickListener(ev -> openFormDialog(c));

        var delBtn = new Button(VaadinIcon.TRASH.create());
        delBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        delBtn.setAriaLabel("Delete " + c.getDisplayName());
        delBtn.addClickListener(ev -> confirmDelete(c));

        var actions = new Div(viewBtn, editBtn, delBtn);
        actions.addClassName("customer-list__row-actions");
        return actions;
    }

    // ── CRUD helpers ──────────────────────────────────────────────────────────

    private void openFormDialog(Customer existing) {
        new CustomerFormDialog(customerService, existing, refreshGrid).open();
    }

    private void confirmDelete(Customer customer) {
        var confirm = new ConfirmDialog();
        confirm.setHeader(getTranslation(CustomerI18n.CONFIRM_DELETE_HDR));
        confirm.setText(getTranslation(CustomerI18n.CONFIRM_DELETE_TEXT, customer.getDisplayName()));
        confirm.setCancelable(true);
        confirm.setConfirmText(getTranslation(CustomerI18n.CONFIRM_DELETE_BTN));
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(ev -> {
            try {
                customerService.delete(customer);
                refreshGrid.run();
                var n = Notification.show(
                        getTranslation(CustomerI18n.NOTIFY_DELETED, customer.getDisplayName()),
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String resolveSubtitle(Customer c) {
        var sb = new StringBuilder();
        if (c.getType() != CustomerType.INDIVIDUAL
                && c.getCompanyName() != null && !c.getCompanyName().isBlank()) {
            sb.append(c.getCompanyName());
        } else if (c.getJobTitle() != null && !c.getJobTitle().isBlank()) {
            sb.append(c.getJobTitle());
        }
        String addr = c.formatAddress();
        if (!addr.isBlank()) {
            if (!sb.isEmpty()) sb.append(" · ");
            sb.append(addr);
        }
        return sb.isEmpty() ? (c.getEmail() != null ? c.getEmail() : "") : sb.toString();
    }
}

