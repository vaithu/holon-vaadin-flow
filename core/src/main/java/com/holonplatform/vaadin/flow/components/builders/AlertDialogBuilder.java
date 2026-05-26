/*
 * Copyright 2016-2024 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAlertDialogBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDialog;

/**
 * Builder to create and configure {@link AlertDialog} — the shadcn/ui + Tailwind UI-inspired
 * confirmation dialog.
 *
 * <p>Use this component when an action <strong>requires explicit user confirmation</strong>
 * — typically for dangerous or irreversible operations such as deletion or sign-out.
 * The dialog is intentionally <strong>non-dismissible</strong> by default (no ESC, no
 * click-outside) to force an explicit user choice.</p>
 *
 * <hr>
 *
 * <h3>Basic usage</h3>
 * <pre>{@code
 * AlertDialog.builder()
 *     .title("Are you absolutely sure?")
 *     .description("This action cannot be undone. This will permanently delete your account.")
 *     .cancelText("Cancel")
 *     .confirmText("Yes, delete account")
 *     .variant(Alert.Variant.DESTRUCTIVE)
 *     .onConfirm(() -> accountService.delete(currentUser))
 *     .open();
 * }</pre>
 *
 * <h3>With header icon badge (Tailwind UI pattern)</h3>
 * <p>Pair a colored circular badge with the dialog variant to instantly communicate intent:</p>
 * <pre>{@code
 * AlertDialog.builder()
 *     .title("Delete project?")
 *     .description("All files and collaborators will be removed. This cannot be undone.")
 *     .headerIcon(new Icon(VaadinIcon.WARNING), Alert.Variant.DESTRUCTIVE)
 *     .variant(Alert.Variant.DESTRUCTIVE)
 *     .confirmText("Delete project")
 *     .onConfirm(() -> projectService.delete(project))
 *     .open();
 * }</pre>
 *
 * <h3>Centered layout</h3>
 * <p>Best used with a header icon for clean icon-centric confirmation dialogs:</p>
 * <pre>{@code
 * AlertDialog.builder()
 *     .title("Payment successful")
 *     .description("Your subscription has been activated.")
 *     .headerIcon(new Icon(VaadinIcon.CHECK_CIRCLE), Alert.Variant.SUCCESS)
 *     .centered()
 *     .cancelText(null)
 *     .withCancelButton(false)
 *     .confirmText("Continue")
 *     .alertRole(false)  // routine confirmation — downgrade to role="dialog"
 *     .open();
 * }</pre>
 *
 * <h3>Multiple action buttons (Tailwind UI multi-action footer)</h3>
 * <pre>{@code
 * AlertDialog.builder()
 *     .title("What would you like to do?")
 *     .description("Choose how to handle this item.")
 *     .secondaryAction("Move to archive", () -> service.archive(id))
 *     .confirmText("Delete permanently")
 *     .variant(Alert.Variant.DESTRUCTIVE)
 *     .onConfirm(() -> service.delete(id))
 *     .open();
 * }</pre>
 *
 * <h3>Scrollable body content slot</h3>
 * <p>Add arbitrary components between the header and the footer buttons:</p>
 * <pre>{@code
 * Grid<Item> itemGrid = buildItemGrid(selectedItems);
 * AlertDialog.builder()
 *     .title("Delete selected items?")
 *     .description("The following items will be permanently removed:")
 *     .bodyContent(itemGrid)
 *     .size(AlertDialog.Size.LG)
 *     .variant(Alert.Variant.DESTRUCTIVE)
 *     .confirmText("Delete all")
 *     .onConfirm(() -> service.deleteAll(selectedItems))
 *     .open();
 * }</pre>
 *
 * <h3>Async loading state</h3>
 * <pre>{@code
 * AlertDialog dialog = AlertDialog.builder()
 *     .title("Submit report?")
 *     .description("This will send the report to all team members.")
 *     .confirmText("Submit")
 *     .onConfirm(() -> {
 *         dialog.setLoading(true);
 *         try {
 *             reportService.submit(report);
 *             return true;
 *         } catch (Exception e) {
 *             Notification.show("Submit failed: " + e.getMessage());
 *             dialog.setLoading(false);
 *             return false;
 *         }
 *     })
 *     .build();
 * dialog.open();
 * }</pre>
 *
 * <h3>Full-screen on mobile (bottom-sheet)</h3>
 * <pre>{@code
 * AlertDialog.builder()
 *     .title("Sign out?")
 *     .description("You will be redirected to the login page.")
 *     .fullScreenOnMobile(true)
 *     .stackedButtons(true)
 *     .confirmText("Sign out")
 *     .onConfirm(() -> logoutSupport.logout())
 *     .open();
 * }</pre>
 *
 * <h3>Size presets</h3>
 * <ul>
 *   <li>{@link AlertDialog.Size#SM}  — 24 rem, compact confirmations</li>
 *   <li>{@link AlertDialog.Size#MD}  — 28 rem, default</li>
 *   <li>{@link AlertDialog.Size#LG}  — 32 rem, icon-heavy or descriptive dialogs</li>
 *   <li>{@link AlertDialog.Size#XL}  — 36 rem, dialogs with a form in the body slot</li>
 *   <li>{@link AlertDialog.Size#XL2} — 42 rem, rich content with a scrollable list</li>
 * </ul>
 *
 * <h3>Or use the {@link #open()} shortcut to build and open in one call:</h3>
 * <pre>{@code
 * AlertDialog.builder().title("Confirm?").onConfirm(action).open();
 * }</pre>
 *
 * @see AlertDialogConfigurator
 * @see AlertDialog
 * @see Alert.Variant
 */
public interface AlertDialogBuilder
        extends AlertDialogConfigurator<AlertDialogBuilder>, ComponentBuilder<AlertDialog, AlertDialogBuilder> {

    /**
     * Build the {@link AlertDialog} and immediately open it.
     *
     * @return the opened {@link AlertDialog} instance
     */
    default AlertDialog open() {
        AlertDialog dialog = build();
        dialog.open();
        return dialog;
    }

    /**
     * Create a new {@link AlertDialogBuilder}.
     *
     * @return a new {@link AlertDialogBuilder}
     */
    static AlertDialogBuilder create() {
        return new DefaultAlertDialogBuilder();
    }
}
