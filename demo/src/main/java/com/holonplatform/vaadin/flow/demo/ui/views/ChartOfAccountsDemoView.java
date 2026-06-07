package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Clone of the TailAdmin "Chart of Accounts" page.
 * Demonstrates a clean table-based listing with blue link names,
 * lock/checkbox icons, and a simple page header with action button.
 */
@PageTitle("Chart of Accounts")
@Route(value = "chart-of-accounts", layout = DemoMainLayout.class)
@StyleSheet("context://chart-of-accounts.css")
public class ChartOfAccountsDemoView extends Div {

    // ── Account type enum ────────────────────────────────────────────────────
    public enum AccountType {
        ACCOUNTS_PAYABLE("Accounts Payable"),
        ACCOUNTS_RECEIVABLE("Accounts Receivable"),
        OTHER_CURRENT_ASSET("Other Current Asset"),
        EXPENSE("Expense"),
        COST_OF_GOODS_SOLD("Cost Of Goods Sold"),
        INCOME("Income"),
        EQUITY("Equity"),
        FIXED_ASSET("Fixed Asset"),
        OTHER_LIABILITY("Other Liability");

        private final String label;

        AccountType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    // ── Account bean ─────────────────────────────────────────────────────────
    public static final class Account {
        private long id;
        private String name;
        private int code;
        private AccountType type;
        private boolean systemAccount;

        public Account() {}

        public Account(long id, String name, int code, AccountType type, boolean systemAccount) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.type = type;
            this.systemAccount = systemAccount;
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public AccountType getType() { return type; }
        public void setType(AccountType type) { this.type = type; }
        public boolean isSystemAccount() { return systemAccount; }
        public void setSystemAccount(boolean systemAccount) { this.systemAccount = systemAccount; }
    }

    // ── Sample data ──────────────────────────────────────────────────────────
    private static final List<Account> ACCOUNTS = List.of(
            new Account(1, "Accounts Payable", 65538, AccountType.ACCOUNTS_PAYABLE, true),
            new Account(2, "Accounts Receivable", 14222, AccountType.ACCOUNTS_RECEIVABLE, true),
            new Account(3, "Advance Tax", 88896, AccountType.OTHER_CURRENT_ASSET, false),
            new Account(4, "Advertising And Marketing", 93937, AccountType.EXPENSE, true),
            new Account(5, "Automobile Expense", 13285, AccountType.EXPENSE, false),
            new Account(6, "Bad Debt", 72608, AccountType.EXPENSE, true),
            new Account(7, "Bank Fees and Charges", 56193, AccountType.EXPENSE, false),
            new Account(8, "Consultant Expense", 31611, AccountType.EXPENSE, false),
            new Account(9, "Cost of Goods Sold", 84909, AccountType.COST_OF_GOODS_SOLD, true),
            new Account(10, "Credit Card Charges", 30492, AccountType.EXPENSE, false),
            new Account(11, "Depreciation Expense", 30132, AccountType.EXPENSE, true),
            new Account(12, "Discount", 69315, AccountType.INCOME, true),
            new Account(13, "Drawings", 96025, AccountType.EQUITY, true),
            new Account(14, "Employee Advance", 94629, AccountType.OTHER_CURRENT_ASSET, true),
            new Account(15, "Equipment", 41520, AccountType.FIXED_ASSET, false),
            new Account(16, "Insurance", 28743, AccountType.EXPENSE, true),
            new Account(17, "Interest Income", 55012, AccountType.INCOME, false),
            new Account(18, "Inventory Asset", 77201, AccountType.OTHER_CURRENT_ASSET, true),
            new Account(19, "Office Supplies", 62384, AccountType.EXPENSE, false),
            new Account(20, "Rent Expense", 19847, AccountType.EXPENSE, true)
    );

    // ── Constructor ──────────────────────────────────────────────────────────
    public ChartOfAccountsDemoView() {
        addClassName("coa-page");

        // ── Page header ──
        var header = new Div();
        header.addClassName("coa-header");

        var titleRow = new H2("All Accounts");
        titleRow.addClassName("coa-title");
        var chevron = new Icon(VaadinIcon.CHEVRON_DOWN_SMALL);
        chevron.addClassName("coa-title-chevron");
        titleRow.add(chevron);

        var newBtn = new Button("+ New Account");
        newBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newBtn.addClassName("coa-new-btn");

        header.add(titleRow, newBtn);

        // ── Grid ──
        var gridWrapper = new Div();
        gridWrapper.addClassName("coa-grid");

        var listing = BeanListing.builder(Account.class, false)
                .withComponentColumn(account -> {
                    var container = new Span();
                    container.addClassName("coa-icon-col");
                    if (account.isSystemAccount()) {
                        var lockIcon = new Icon(VaadinIcon.LOCK);
                        lockIcon.addClassName("coa-lock-icon");
                        container.add(lockIcon);
                    } else {
                        var checkbox = new com.vaadin.flow.component.checkbox.Checkbox();
                        checkbox.addClassName("coa-icon-col");
                        container.add(checkbox);
                    }
                    return container;
                }).header("").flexGrow(0).add()
                .withComponentColumn(account -> {
                    var nameSpan = new Span(account.getName());
                    nameSpan.addClassName("coa-account-name");
                    return nameSpan;
                }).header("Account Name").flexGrow(3).add() // content() to prevent auto-linking by header()
                .withComponentColumn(account -> {
                    var codeSpan = new Span(String.valueOf(account.getCode()));
                    codeSpan.addClassName("coa-account-code");
                    return codeSpan;
                }).header("Account Code").flexGrow(2).add()
                .withComponentColumn(account -> {
                    var typeSpan = new Span(account.getType().getLabel());
                    typeSpan.addClassName("coa-account-type");
                    return typeSpan;
                }).header("Type").flexGrow(2).add()
                .build();

        listing.setItems(q -> ACCOUNTS.stream()
                .skip(q.getOffset()).limit(q.getLimit()));

        gridWrapper.add(listing.getComponent());

        add(header, gridWrapper);
    }
}
