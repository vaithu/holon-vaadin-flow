package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link AlertDialog} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic confirmation (minimal)</li>
 *   <li>All five action variants (DEFAULT → INFO)</li>
 *   <li>Header icon badge (Tailwind UI pattern)</li>
 *   <li>Centered layout with icon</li>
 *   <li>Size presets (SM → XL2)</li>
 *   <li>Secondary action button (multi-action footer)</li>
 *   <li>Scrollable body content slot</li>
 *   <li>Conditional confirm (BooleanSupplier — stay open on failure)</li>
 *   <li>Async loading state</li>
 *   <li>Stacked buttons / full-screen on mobile (bottom-sheet)</li>
 *   <li>Dismissible dialog (ESC + outside-click)</li>
 *   <li>Header close (×) button</li>
 *   <li>Draggable and resizable</li>
 *   <li>Single-action "I acknowledge" (no cancel button)</li>
 * </ol>
 */
@PageTitle("AlertDialog – Holon Demo")
@Route(value = "alert-dialog", layout = DemoMainLayout.class)
public class AlertDialogDemoView extends Div {

    public AlertDialogDemoView() {
        addClassName("app-view");

        var title = new H1("AlertDialog");

        var desc = new Paragraph(
                "Confirmation dialog inspired by shadcn/ui AlertDialog and Tailwind UI Plus modal patterns. " +
                "Use when an action requires explicit user confirmation — especially for irreversible or " +
                "dangerous operations. Non-dismissible by default (no ESC, no outside-click). " +
                "Supports header icon badge, scrollable body, secondary action, loading state, " +
                "size presets, centered layout, stacked/mobile-fullscreen buttons, and full ARIA semantics.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(variantsExample());
        examples.add(headerIconExample());
        examples.add(centeredLayoutExample());
        examples.add(sizePresetsExample());
        examples.add(secondaryActionExample());
        examples.add(bodyContentExample());
        examples.add(conditionalConfirmExample());
        examples.add(loadingStateExample());
        examples.add(stackedMobileExample());
        examples.add(dismissibleExample());
        examples.add(closeButtonExample());
        examples.add(draggableResizableExample());
        examples.add(singleActionExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample basicExample() {
        var trigger = new Button("Open basic dialog",
                e -> AlertDialog.builder()
                        .title("Are you absolutely sure?")
                        .description("This action cannot be undone. This will permanently delete your account and remove all associated data.")
                        .cancelText("Cancel")
                        .confirmText("Yes, delete account")
                        .variant(Alert.Variant.DESTRUCTIVE)
                        .onConfirm(() -> Notification.show("Account deleted"))
                        .open());

        return new DemoExample("Basic Confirmation (minimal)", trigger, """
                AlertDialog.builder()
                    .title("Are you absolutely sure?")
                    .description("This action cannot be undone.")
                    .cancelText("Cancel")
                    .confirmText("Yes, delete account")
                    .variant(Alert.Variant.DESTRUCTIVE)
                    .onConfirm(() -> accountService.delete(currentUser))
                    .open();
                """);
    }

    private DemoExample variantsExample() {
        var preview = new Div();
        preview.addClassNames("demo-stack", "demo-stack--row");

        for (Alert.Variant variant : Alert.Variant.values()) {
            String label = capitalize(variant.name());
            preview.add(new Button("Open " + label,
                    e -> AlertDialog.builder()
                            .title(label + " confirmation")
                            .description("This is a " + label.toLowerCase() + " dialog — " + variantHint(variant) + ".")
                            .confirmText(variantConfirmText(variant))
                            .variant(variant)
                            .onConfirm(() -> Notification.show(label + " confirmed"))
                            .open()));
        }

        return new DemoExample("Action Button Variants (DEFAULT → INFO)", preview, """
                // DEFAULT — primary dark button
                AlertDialog.builder()
                    .title("Save changes?")
                    .confirmText("Save")
                    .variant(Alert.Variant.DEFAULT)
                    .onConfirm(() -> service.save(entity))
                    .open();

                // DESTRUCTIVE — red, for dangerous operations
                AlertDialog.builder()
                    .title("Delete account?")
                    .confirmText("Delete")
                    .variant(Alert.Variant.DESTRUCTIVE)
                    .onConfirm(() -> service.delete(entity))
                    .open();

                // WARNING — amber, for cautionary operations
                AlertDialog.builder()
                    .title("Archive project?")
                    .confirmText("Archive")
                    .variant(Alert.Variant.WARNING)
                    .onConfirm(() -> service.archive(project))
                    .open();

                // SUCCESS — green, for positive confirmations
                AlertDialog.builder()
                    .title("Publish article?")
                    .confirmText("Publish")
                    .variant(Alert.Variant.SUCCESS)
                    .onConfirm(() -> service.publish(article))
                    .open();

                // INFO — blue, for neutral confirmations
                AlertDialog.builder()
                    .title("Submit report?")
                    .confirmText("Submit")
                    .variant(Alert.Variant.INFO)
                    .onConfirm(() -> service.submit(report))
                    .open();
                """);
    }

    private DemoExample headerIconExample() {
        var preview = new Div();
        preview.addClassNames("demo-stack", "demo-stack--row");

        preview.add(
                new Button("Destructive icon",
                        e -> AlertDialog.builder()
                                .title("Delete project?")
                                .description("All files and collaborators will be permanently removed. This cannot be undone.")
                                .headerIcon(new Icon(VaadinIcon.TRASH), Alert.Variant.DESTRUCTIVE)
                                .variant(Alert.Variant.DESTRUCTIVE)
                                .confirmText("Delete project")
                                .onConfirm(() -> Notification.show("Project deleted"))
                                .open()),
                new Button("Warning icon",
                        e -> AlertDialog.builder()
                                .title("Deactivate account?")
                                .description("All users assigned to this account will lose access until it is reactivated.")
                                .headerIcon(new Icon(VaadinIcon.WARNING), Alert.Variant.WARNING)
                                .variant(Alert.Variant.WARNING)
                                .confirmText("Deactivate")
                                .onConfirm(() -> Notification.show("Account deactivated"))
                                .open()),
                new Button("Success icon",
                        e -> AlertDialog.builder()
                                .title("Payment successful")
                                .description("Your subscription has been activated. You now have access to all premium features.")
                                .headerIcon(new Icon(VaadinIcon.CHECK_CIRCLE), Alert.Variant.SUCCESS)
                                .variant(Alert.Variant.SUCCESS)
                                .confirmText("Continue")
                                .withCancelButton(false)
                                .alertRole(false)
                                .open()),
                new Button("Info icon",
                        e -> AlertDialog.builder()
                                .title("New version available")
                                .description("Version 11.0.0 is available with new features and bug fixes.")
                                .headerIcon(new Icon(VaadinIcon.INFO_CIRCLE), Alert.Variant.INFO)
                                .variant(Alert.Variant.INFO)
                                .confirmText("Update now")
                                .cancelText("Remind me later")
                                .onConfirm(() -> Notification.show("Updating..."))
                                .open())
        );

        return new DemoExample("Header Icon Badge (Tailwind UI pattern)", preview, """
                // Colored icon badge — paired with a matching variant
                AlertDialog.builder()
                    .title("Delete project?")
                    .description("All files will be permanently removed.")
                    .headerIcon(new Icon(VaadinIcon.TRASH), Alert.Variant.DESTRUCTIVE)
                    .variant(Alert.Variant.DESTRUCTIVE)
                    .confirmText("Delete project")
                    .onConfirm(() -> projectService.delete(project))
                    .open();

                // Neutral icon (no variant argument = neutral badge background)
                AlertDialog.builder()
                    .title("Confirm transfer?")
                    .headerIcon(new Icon(VaadinIcon.EXCHANGE))
                    .confirmText("Transfer")
                    .onConfirm(() -> service.transfer(amount))
                    .open();
                """);
    }

    private DemoExample centeredLayoutExample() {
        var preview = new Div();
        preview.addClassNames("demo-stack", "demo-stack--row");

        preview.add(
                new Button("Centered success",
                        e -> AlertDialog.builder()
                                .title("Payment confirmed!")
                                .description("Your order has been placed and will be shipped within 2 business days.")
                                .headerIcon(new Icon(VaadinIcon.CHECK_CIRCLE), Alert.Variant.SUCCESS)
                                .centered()
                                .withCancelButton(false)
                                .confirmText("Continue shopping")
                                .alertRole(false)
                                .open()),
                new Button("Centered destructive",
                        e -> AlertDialog.builder()
                                .title("Delete everything?")
                                .description("All data in this workspace will be permanently erased.")
                                .headerIcon(new Icon(VaadinIcon.TRASH), Alert.Variant.DESTRUCTIVE)
                                .centered()
                                .variant(Alert.Variant.DESTRUCTIVE)
                                .confirmText("Yes, delete all")
                                .onConfirm(() -> Notification.show("Workspace cleared"))
                                .open())
        );

        return new DemoExample("Centered Layout", preview, """
                // Centered — best paired with a header icon for icon-centric dialogs
                AlertDialog.builder()
                    .title("Payment confirmed!")
                    .description("Your order has been placed.")
                    .headerIcon(new Icon(VaadinIcon.CHECK_CIRCLE), Alert.Variant.SUCCESS)
                    .centered()
                    .withCancelButton(false)
                    .confirmText("Continue")
                    .alertRole(false)   // routine — downgrade role="dialog"
                    .open();
                """);
    }

    private DemoExample sizePresetsExample() {
        var preview = new Div();
        preview.addClassNames("demo-stack", "demo-stack--row");

        for (AlertDialog.Size size : AlertDialog.Size.values()) {
            String label = size.name();
            preview.add(new Button("Size " + label,
                    e -> AlertDialog.builder()
                            .title("Size preset: " + label)
                            .description("This dialog uses the " + label + " size preset. " +
                                    sizeDescription(size))
                            .size(size)
                            .confirmText("OK")
                            .withCancelButton(false)
                            .open()));
        }

        return new DemoExample("Size Presets (SM → XL2)", preview, """
                // SM  — ~24 rem, compact confirmations
                AlertDialog.builder().title("Confirm?").size(AlertDialog.Size.SM).open();

                // MD  — ~28 rem, default (no size() call needed)
                AlertDialog.builder().title("Confirm?").size(AlertDialog.Size.MD).open();

                // LG  — ~32 rem, icon-heavy or descriptive dialogs
                AlertDialog.builder().title("Confirm?").size(AlertDialog.Size.LG).open();

                // XL  — ~36 rem, dialogs with a form in the body slot
                AlertDialog.builder().title("Confirm?").size(AlertDialog.Size.XL).open();

                // XL2 — ~42 rem, rich content with a scrollable list
                AlertDialog.builder().title("Confirm?").size(AlertDialog.Size.XL2).open();
                """);
    }

    private DemoExample secondaryActionExample() {
        var trigger = new Button("Open multi-action dialog",
                e -> AlertDialog.builder()
                        .title("What would you like to do?")
                        .description("You can archive the item to keep it accessible, or delete it permanently.")
                        .secondaryAction("Move to archive", () -> Notification.show("Item archived"))
                        .confirmText("Delete permanently")
                        .variant(Alert.Variant.DESTRUCTIVE)
                        .onConfirm(() -> Notification.show("Item deleted permanently"))
                        .open());

        return new DemoExample("Secondary Action Button (multi-action footer)", trigger, """
                // Footer: [Cancel] [Move to archive] [Delete permanently]
                AlertDialog.builder()
                    .title("What would you like to do?")
                    .description("Archive keeps the item accessible; delete is permanent.")
                    .secondaryAction("Move to archive", () -> service.archive(id))
                    .confirmText("Delete permanently")
                    .variant(Alert.Variant.DESTRUCTIVE)
                    .onConfirm(() -> service.delete(id))
                    .open();
                """);
    }

    private DemoExample bodyContentExample() {
        var trigger = new Button("Open dialog with body content",
                e -> {
                    var itemList = new UnorderedList();
                    itemList.add(
                            new ListItem("invoice_march_2026.pdf"),
                            new ListItem("receipt_hotel_london.pdf"),
                            new ListItem("report_q1_2026.xlsx"),
                            new ListItem("presentation_board.pptx")
                    );

                    var note = new Span("These files cannot be recovered after deletion.");

                    AlertDialog.builder()
                            .title("Delete selected files?")
                            .description("The following 4 files will be permanently removed from your account:")
                            .bodyContent(itemList, note)
                            .size(AlertDialog.Size.LG)
                            .variant(Alert.Variant.DESTRUCTIVE)
                            .confirmText("Delete all files")
                            .onConfirm(() -> Notification.show("Files deleted"))
                            .open();
                });

        return new DemoExample("Scrollable Body Content Slot", trigger, """
                // Add any components between header and footer
                UnorderedList itemList = buildSelectedItemList(selectedFiles);
                Span note = new Span("These files cannot be recovered.");

                AlertDialog.builder()
                    .title("Delete selected files?")
                    .description("The following files will be permanently removed:")
                    .bodyContent(itemList, note)
                    .size(AlertDialog.Size.LG)
                    .variant(Alert.Variant.DESTRUCTIVE)
                    .confirmText("Delete all files")
                    .onConfirm(() -> fileService.deleteAll(selectedFiles))
                    .open();
                """);
    }

    private DemoExample conditionalConfirmExample() {
        // Simulate a toggle: first click fails, second succeeds
        var attemptHolder = new int[]{0};

        var trigger = new Button("Open conditional confirm dialog",
                e -> AlertDialog.builder()
                        .title("Submit expense report?")
                        .description("This will send the report to your manager for approval. " +
                                "(Demo: first attempt simulates a validation error.)")
                        .confirmText("Submit report")
                        .variant(Alert.Variant.INFO)
                        .onConfirm(() -> {
                            attemptHolder[0]++;
                            if (attemptHolder[0] % 2 != 0) {
                                Notification.show("Validation failed — please review line items (dialog stays open)");
                                return false;  // keep dialog open
                            }
                            Notification.show("Report submitted successfully!");
                            attemptHolder[0] = 0;
                            return true;  // close dialog
                        })
                        .open());

        return new DemoExample("Conditional Confirm (BooleanSupplier — stay open on failure)", trigger, """
                // Dialog stays open when the supplier returns false (e.g. validation failed)
                AlertDialog.builder()
                    .title("Submit expense report?")
                    .description("Report will be sent to your manager for approval.")
                    .confirmText("Submit report")
                    .variant(Alert.Variant.INFO)
                    .onConfirm(() -> {
                        try {
                            reportService.submit(report);
                            return true;   // success → close dialog
                        } catch (ValidationException ex) {
                            Notification.show("Validation failed: " + ex.getMessage());
                            return false;  // failure → stay open so user can retry
                        }
                    })
                    .open();
                """);
    }

    private DemoExample loadingStateExample() {
        var trigger = new Button("Open async loading dialog",
                e -> {
                    AlertDialog[] dialogRef = new AlertDialog[1];
                    dialogRef[0] = AlertDialog.builder()
                            .title("Send invitation emails?")
                            .description("This will send invitation emails to all 42 pending team members. The operation may take a few seconds.")
                            .confirmText("Send emails")
                            .variant(Alert.Variant.INFO)
                            .onConfirm(() -> {
                                dialogRef[0].setLoading(true);
                                // Simulate async work with a background thread
                                dialogRef[0].getUI().ifPresent(ui ->
                                    Thread.ofVirtual().start(() -> {
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException ex) {
                                            Thread.currentThread().interrupt();
                                        }
                                        ui.access(() -> {
                                            dialogRef[0].setLoading(false);
                                            dialogRef[0].close();
                                            Notification.show("42 invitations sent!");
                                        });
                                    }));
                            })
                            .build();
                    dialogRef[0].open();
                });

        return new DemoExample("Async Loading State", trigger, """
                AlertDialog[] dialogRef = new AlertDialog[1];
                dialogRef[0] = AlertDialog.builder()
                    .title("Send invitation emails?")
                    .description("This will send emails to all 42 pending members.")
                    .confirmText("Send emails")
                    .variant(Alert.Variant.INFO)
                    .onConfirm(() -> {
                        dialogRef[0].setLoading(true);
                        dialogRef[0].getUI().ifPresent(ui -> {
                            Thread.ofVirtual().start(() -> {
                                emailService.sendAll(pendingMembers);   // long operation
                                ui.access(() -> {
                                    dialogRef[0].setLoading(false);
                                    dialogRef[0].close();
                                    Notification.show("Emails sent!");
                                });
                            });
                        });
                    })
                    .build();
                dialogRef[0].open();
                """);
    }

    private DemoExample stackedMobileExample() {
        var trigger = new Button("Open stacked / mobile-fullscreen dialog",
                e -> AlertDialog.builder()
                        .title("Sign out of your account?")
                        .description("You will be redirected to the login page and any unsaved changes will be lost.")
                        .fullScreenOnMobile(true)
                        .stackedButtons(true)
                        .confirmText("Sign out")
                        .cancelText("Stay logged in")
                        .variant(Alert.Variant.WARNING)
                        .onConfirm(() -> Notification.show("Signed out"))
                        .open());

        return new DemoExample("Stacked Buttons / Full-screen on Mobile (bottom-sheet)", trigger, """
                AlertDialog.builder()
                    .title("Sign out of your account?")
                    .description("You will be redirected to the login page.")
                    .fullScreenOnMobile(true)   // expands to full viewport on ≤ 639 px
                    .stackedButtons(true)       // always stacks buttons vertically
                    .confirmText("Sign out")
                    .cancelText("Stay logged in")
                    .variant(Alert.Variant.WARNING)
                    .onConfirm(() -> logoutSupport.logout())
                    .open();
                """);
    }

    private DemoExample dismissibleExample() {
        var trigger = new Button("Open dismissible dialog",
                e -> AlertDialog.builder()
                        .title("Review terms of service")
                        .description("Please take a moment to review our updated terms of service. You can dismiss this dialog by pressing ESC or clicking outside.")
                        .closeOnEsc(true)
                        .closeOnOutsideClick(true)
                        .alertRole(false)
                        .confirmText("I accept")
                        .cancelText("Decline")
                        .variant(Alert.Variant.DEFAULT)
                        .onConfirm(() -> Notification.show("Terms accepted"))
                        .open());

        return new DemoExample("Dismissible (ESC + outside-click enabled)", trigger, """
                // Override non-dismissible defaults for non-critical dialogs
                AlertDialog.builder()
                    .title("Review terms of service")
                    .description("You can dismiss by pressing ESC or clicking outside.")
                    .closeOnEsc(true)
                    .closeOnOutsideClick(true)
                    .alertRole(false)            // routine → role="dialog"
                    .confirmText("I accept")
                    .cancelText("Decline")
                    .onConfirm(() -> userService.acceptTerms(currentUser))
                    .open();
                """);
    }

    private DemoExample closeButtonExample() {
        var trigger = new Button("Open dialog with close button",
                e -> AlertDialog.builder()
                        .title("Keyboard shortcuts")
                        .description("Use the close button (×) in the top-right corner or the button below to dismiss this dialog.")
                        .withCloseButton(true)
                        .withCancelButton(false)
                        .confirmText("Got it")
                        .alertRole(false)
                        .open());

        return new DemoExample("Header Close (×) Button", trigger, """
                // Show the × close button as an escape hatch (no cancel in footer)
                AlertDialog.builder()
                    .title("Keyboard shortcuts")
                    .description("Press the × button or Confirm to dismiss.")
                    .withCloseButton(true)
                    .withCancelButton(false)
                    .confirmText("Got it")
                    .alertRole(false)
                    .open();

                // Replace the default × glyph with a custom icon
                AlertDialog.builder()
                    .title("Info panel")
                    .closeIcon(new Icon(VaadinIcon.CLOSE))
                    .confirmText("Dismiss")
                    .open();
                """);
    }

    private DemoExample draggableResizableExample() {
        var trigger = new Button("Open draggable & resizable dialog",
                e -> AlertDialog.builder()
                        .title("Drag me around!")
                        .description("This dialog is draggable and resizable. Click and drag the title bar to move it; grab an edge to resize.")
                        .draggable(true)
                        .resizable(true)
                        .size(AlertDialog.Size.LG)
                        .alertRole(false)
                        .confirmText("Done")
                        .withCancelButton(false)
                        .open());

        return new DemoExample("Draggable and Resizable", trigger, """
                AlertDialog.builder()
                    .title("Drag me around!")
                    .description("Click the title bar to move; grab an edge to resize.")
                    .draggable(true)
                    .resizable(true)
                    .size(AlertDialog.Size.LG)
                    .alertRole(false)
                    .confirmText("Done")
                    .withCancelButton(false)
                    .open();
                """);
    }

    private DemoExample singleActionExample() {
        var preview = new Div();
        preview.addClassNames("demo-stack", "demo-stack--row");

        preview.add(
                new Button("\"Got it\" acknowledge",
                        e -> AlertDialog.builder()
                                .title("Your trial has ended")
                                .description("Your 30-day trial period has expired. Upgrade to a paid plan to continue using all features.")
                                .withCancelButton(false)
                                .confirmText("Got it")
                                .alertRole(false)
                                .open()),
                new Button("\"I understand\" single-action",
                        e -> AlertDialog.builder()
                                .title("Irreversible action warning")
                                .description("Once you proceed, this action cannot be reversed. Ensure you have backed up your data.")
                                .headerIcon(new Icon(VaadinIcon.EXCLAMATION_CIRCLE_O), Alert.Variant.WARNING)
                                .withCancelButton(false)
                                .confirmText("I understand, proceed")
                                .variant(Alert.Variant.WARNING)
                                .onConfirm(() -> Notification.show("Proceeding..."))
                                .open())
        );

        return new DemoExample("Single Action — No Cancel Button (\"I acknowledge\" pattern)", preview, """
                // "Got it" — acknowledgement only, no choice to cancel
                AlertDialog.builder()
                    .title("Your trial has ended")
                    .description("Upgrade to continue using all features.")
                    .withCancelButton(false)
                    .confirmText("Got it")
                    .alertRole(false)       // informational — not urgent
                    .open();

                // Single action with warning icon
                AlertDialog.builder()
                    .title("Irreversible action warning")
                    .headerIcon(new Icon(VaadinIcon.EXCLAMATION_CIRCLE_O), Alert.Variant.WARNING)
                    .withCancelButton(false)
                    .confirmText("I understand, proceed")
                    .variant(Alert.Variant.WARNING)
                    .onConfirm(() -> service.proceed())
                    .open();
                """);
    }

    // ── Utilities ────────────────────────────────────────────────────────────

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    private static String variantHint(Alert.Variant v) {
        return switch (v) {
            case DEFAULT     -> "neutral save / update operations";
            case DESTRUCTIVE -> "permanent deletions and irreversible actions";
            case WARNING     -> "cautionary operations with side effects";
            case SUCCESS     -> "positive publish / activate confirmations";
            case INFO        -> "neutral submit / send operations";
        };
    }

    private static String variantConfirmText(Alert.Variant v) {
        return switch (v) {
            case DEFAULT     -> "Save";
            case DESTRUCTIVE -> "Delete";
            case WARNING     -> "Archive";
            case SUCCESS     -> "Publish";
            case INFO        -> "Submit";
        };
    }

    private static String sizeDescription(AlertDialog.Size size) {
        return switch (size) {
            case SM  -> "~24 rem — best for compact confirmations with short text.";
            case MD  -> "~28 rem — default size for most dialogs.";
            case LG  -> "~32 rem — icon-heavy or descriptive dialogs.";
            case XL  -> "~36 rem — dialogs with a form or list in the body slot.";
            case XL2 -> "~42 rem — rich content with a scrollable body.";
        };
    }
}

