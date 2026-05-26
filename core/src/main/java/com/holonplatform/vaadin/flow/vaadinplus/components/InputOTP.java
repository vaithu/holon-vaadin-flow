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
package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.InputOTPBuilder;
import com.holonplatform.vaadin.flow.components.builders.InputOTPConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;

/**
 * A One-Time Password (OTP) input that groups individual character slots into a single,
 * accessible, visually unified control.
 *
 * <p>Inspired by shadcn/ui {@code InputOTP}
 * (<a href="https://ui.shadcn.com/docs/components/radix/input-otp">docs</a>).
 *
 * <p>Renders as:
 * <pre>
 * &lt;div class="input-otp"&gt;
 *   &lt;div class="input-otp__group"&gt;
 *     &lt;div class="input-otp__slot"&gt;...&lt;/div&gt;
 *     &lt;div class="input-otp__slot"&gt;...&lt;/div&gt;
 *     &lt;div class="input-otp__slot"&gt;...&lt;/div&gt;
 *   &lt;/div&gt;
 *   &lt;div class="input-otp__separator"&gt;–&lt;/div&gt;
 *   &lt;div class="input-otp__group"&gt;
 *     &lt;div class="input-otp__slot"&gt;...&lt;/div&gt;
 *     &lt;div class="input-otp__slot"&gt;...&lt;/div&gt;
 *     &lt;div class="input-otp__slot"&gt;...&lt;/div&gt;
 *   &lt;/div&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p><strong>Fluent builder (recommended):</strong>
 * <pre>{@code
 * InputOTP otp = InputOTP.builder()
 *     .group(3)
 *     .separator()
 *     .group(3)
 *     .onComplete(value -> verifyCode(value))
 *     .build();
 * }</pre>
 *
 * <p><strong>Manual assembly:</strong>
 * <pre>{@code
 * InputOTPGroup group1 = new InputOTPGroup();
 * group1.add(new InputOTPSlot(), new InputOTPSlot(), new InputOTPSlot());
 *
 * InputOTPGroup group2 = new InputOTPGroup();
 * group2.add(new InputOTPSlot(), new InputOTPSlot(), new InputOTPSlot());
 *
 * InputOTP otp = new InputOTP();
 * otp.add(group1, new InputOTPSeparator(), group2);
 * }</pre>
 *
 * <p><strong>Digit-only, numeric pattern:</strong>
 * <pre>{@code
 * InputOTP otp = InputOTP.builder()
 *     .group(6)
 *     .pattern("[0-9]")
 *     .onComplete(this::handleOTP)
 *     .build();
 * }</pre>
 *
 * <p>Each slot uses {@code ValueChangeMode.EAGER} so focus auto-advances to the next slot
 * immediately after a character is entered (server round-trip; acceptable for OTP entry speed).
 *
 * <p>All visual styling is defined in {@code input-otp.css}.  No inline styles or Lumo
 * tokens are used.
 *
 * @see InputOTPGroup
 * @see InputOTPSlot
 * @see InputOTPSeparator
 */
@StyleSheet("context://material-symbols.css")
@StyleSheet("context://input-otp.css")
public class InputOTP extends Div implements HasSize, HasStyle {

    private static final long serialVersionUID = 1L;

    /** Error message label rendered below the slots row. Added to the DOM lazily. */
    private final Span errorSpan = Components.span().build();

    /** All slots across all groups, in insertion order. */
    private final List<InputOTPSlot> allSlots = new ArrayList<>();

    /** Value-change listeners (fire on any slot change). */
    private final List<SerializableConsumer<String>> valueListeners = new ArrayList<>();

    /** Fired once when every slot is filled AND the optional validator passes. */
    private SerializableConsumer<String> onCompleteHandler;

    /**
     * Optional synchronous validator invoked automatically when all slots are filled.
     * Return {@code null} (or an empty string) when the OTP is valid;
     * return a non-empty error message string when invalid.
     * When a non-empty error is returned the component enters the error state and
     * {@code onCompleteHandler} is NOT invoked.
     */
    private SerializableFunction<String, String> validator;

    /** Stored pattern applied to newly-registered slots. */
    private String pattern;

    /** Stored readOnly flag applied to newly-registered slots. */
    private boolean readOnly = false;

    /** Whether the component is currently in the error/invalid state. */
    private boolean invalid = false;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates an empty OTP container.  Add {@link InputOTPGroup}s and optional
     * {@link InputOTPSeparator}s via {@link #add(Component...)}.
     */
    public InputOTP() {
        addClassName("input-otp");

        // errorSpan is attached lazily to the DOM only when an error occurs
        errorSpan.addClassName("input-otp__error-message");
        errorSpan.setVisible(false);
    }

    // -----------------------------------------------------------------------
    // Add — groups and separators
    // -----------------------------------------------------------------------

    /**
     * Appends {@link InputOTPGroup}s, {@link InputOTPSeparator}s, or any other
     * {@link Component} to this container.
     *
     * <p>When an {@link InputOTPGroup} is added, all of its {@link InputOTPSlot}s are
     * automatically registered with this {@code InputOTP} as their manager so that
     * focus auto-advance, backspace retreat, and value-change callbacks work correctly.
     * The stored {@link #setPattern(String)}, read-only, and enabled states are applied
     * to each newly-registered slot.
     *
     * @param components the components to add (null-safe; individual null elements skipped)
     */
    @Override
    public void add(Component... components) {
        if (components == null) return;
        for (Component c : components) {
            if (c == null) continue;
            if (c instanceof InputOTPGroup group) {
                for (InputOTPSlot slot : group.getSlots()) {
                    slot.setManager(this);
                    if (pattern != null) slot.setPattern(pattern);
                    if (readOnly)        slot.setReadOnly(true);
                    if (!isEnabled())    slot.setSlotEnabled(false);
                    allSlots.add(slot);
                }
            }
            // Groups and separators are direct children of this InputOTP container
            super.add(c);
        }
    }

    // -----------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------

    /**
     * Returns the current OTP value — the concatenation of all slot values in order.
     * Slots that are still empty contribute an empty string to the result.
     *
     * @return the current value (never null; empty string if no slots are filled)
     */
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        for (InputOTPSlot slot : allSlots) {
            sb.append(slot.getSlotValue());
        }
        return sb.toString();
    }

    /**
     * Programmatically sets the OTP value, distributing characters to individual slots.
     * Characters beyond {@link #getLength()} are silently ignored.
     * Slots beyond the length of {@code value} are cleared.
     *
     * <p>This does <em>not</em> fire value-change listeners or the completion handler.
     *
     * @param value the value to set (null is treated as empty string)
     */
    public void setValue(String value) {
        if (value == null) value = "";
        for (int i = 0; i < allSlots.size(); i++) {
            allSlots.get(i).setSlotValue(i < value.length() ? String.valueOf(value.charAt(i)) : "");
        }
    }

    /**
     * Clears all slots and resets any error state. Equivalent to {@code setValue("")}.
     */
    public void clear() {
        setValue("");
        clearError();
    }

    /**
     * Returns the total number of character slots across all groups.
     *
     * @return total slot count
     */
    public int getLength() {
        return allSlots.size();
    }

    // -----------------------------------------------------------------------
    // Configuration
    // -----------------------------------------------------------------------

    /**
     * Sets a character-level regex pattern that each individual slot must satisfy.
     * Common values: {@code "[0-9]"} for numeric OTP, {@code "[0-9a-zA-Z]"} for alphanumeric.
     *
     * <p>The pattern is stored and applied automatically to slots added via {@link #add(Component...)}
     * after this call.
     *
     * @param pattern the per-slot regex pattern (null clears the constraint)
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
        allSlots.forEach(slot -> slot.setPattern(pattern));
    }

    /**
     * Sets whether all slots are read-only.
     *
     * @param readOnly {@code true} to make all slots read-only
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        allSlots.forEach(slot -> slot.setReadOnly(readOnly));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Also propagates the enabled/disabled state to every registered slot.
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        allSlots.forEach(slot -> slot.setSlotEnabled(enabled));
    }

    // -----------------------------------------------------------------------
    // Validator
    // -----------------------------------------------------------------------

    /**
     * Sets a synchronous OTP validator that is invoked automatically the moment all
     * slots are filled.
     *
     * <p>The function receives the complete OTP string and must return:
     * <ul>
     *   <li>{@code null} or an empty string — the code is <strong>valid</strong>;
     *       the {@code onComplete} handler is fired normally.</li>
     *   <li>A non-empty error message string — the code is <strong>invalid</strong>;
     *       the component enters the error state (red slot borders + error label),
     *       and the {@code onComplete} handler is <em>not</em> invoked.
     *       The error is automatically cleared the next time the user edits any slot.</li>
     * </ul>
     *
     * <p><strong>Use this for synchronous validation</strong> (e.g. a Spring {@code @Service}
     * method that queries the database).  For non-blocking / asynchronous flows, use
     * {@link #setOnComplete(SerializableConsumer)} and call
     * {@link #setErrorMessage(String)} explicitly after the async result arrives.
     *
     * <pre>{@code
     * otp.setValidator(code ->
     *     otpService.verify(code) ? null : "Invalid verification code");
     * }</pre>
     *
     * @param validator the validator function (not null)
     */
    public void setValidator(SerializableFunction<String, String> validator) {
        this.validator = validator;
    }

    // -----------------------------------------------------------------------
    // Error message
    // -----------------------------------------------------------------------

    /**
     * Displays an error message below the OTP slots and marks the component as invalid
     * (red slot borders).  Calling with {@code null} or an empty string clears the error.
     *
     * <p>This method is typically called from an async {@code onComplete} handler after
     * a DB validation failure:
     * <pre>{@code
     * otp.setOnComplete(code -> {
     *     uiAsyncTasks.supplierConfigurer(() -> otpService.verify(code))
     *         .withResultHandler(valid -> {
     *             if (!valid) otp.setErrorMessage("Invalid verification code");
     *         })
     *         .supplyAsync();
     * });
     * }</pre>
     *
     * @param message the error message to display, or {@code null} / empty to clear
     */
    public void setErrorMessage(String message) {
        if (message != null && !message.isEmpty()) {
            // Attach errorSpan to the DOM on first use
            if (errorSpan.getParent().isEmpty()) {
                super.add(errorSpan);
            }
            errorSpan.setText(message);
            errorSpan.setVisible(true);
            addClassName("input-otp--error");
            invalid = true;
        } else {
            clearError();
        }
    }

    /**
     * Returns the currently displayed error message, or {@code null} if none.
     *
     * @return error message string, or {@code null}
     */
    public String getErrorMessage() {
        return invalid ? errorSpan.getText() : null;
    }

    /**
     * Clears any active error state — hides the error label and removes the error
     * CSS class from all slots.
     */
    public void clearError() {
        errorSpan.setText("");
        errorSpan.setVisible(false);
        removeClassName("input-otp--error");
        invalid = false;
    }

    /**
     * Returns whether the component is currently in the invalid/error state.
     *
     * @return {@code true} if an error message is currently displayed
     */
    public boolean isInvalid() {
        return invalid;
    }

    // -----------------------------------------------------------------------
    // Callbacks
    // -----------------------------------------------------------------------

    /**
     * Registers a handler invoked <strong>once</strong> when every slot contains a character
     * AND the optional {@link #setValidator(SerializableFunction) validator} passes.
     * The full OTP value string is passed to the handler.
     *
     * <pre>{@code
     * otp.setOnComplete(value -> verifyCode(value));
     * }</pre>
     *
     * @param handler the completion handler (not null)
     */
    public void setOnComplete(SerializableConsumer<String> handler) {
        this.onCompleteHandler = handler;
    }

    /**
     * Registers a listener invoked whenever the OTP value changes (any slot changes).
     *
     * @param listener the value-change listener (not null)
     * @return a {@link Registration} to remove the listener
     */
    public Registration addValueChangeListener(SerializableConsumer<String> listener) {
        valueListeners.add(listener);
        return () -> valueListeners.remove(listener);
    }

    // -----------------------------------------------------------------------
    // Package-private callbacks from InputOTPSlot
    // -----------------------------------------------------------------------

    void onSlotFilled(InputOTPSlot slot) {
        // Clear error state whenever the user edits any slot
        clearError();
        notifyValueChange();

        // Auto-advance focus to the next slot
        int idx = allSlots.indexOf(slot);
        if (idx >= 0 && idx + 1 < allSlots.size()) {
            allSlots.get(idx + 1).focus();
        }

        // Check if all slots are now filled; if so, run validator then onComplete
        boolean allFilled = allSlots.stream().noneMatch(s -> s.getSlotValue().isEmpty());
        if (allFilled) {
            String value = getValue();
            // Run the synchronous validator first, if one is configured
            if (validator != null) {
                String error = validator.apply(value);
                if (error != null && !error.isEmpty()) {
                    setErrorMessage(error);
                    return; // Validation failed — do not fire onComplete
                }
            }
            // Validation passed (or no validator set) — fire the completion handler
            if (onCompleteHandler != null) {
                onCompleteHandler.accept(value);
            }
        }
    }

    void onSlotCleared(InputOTPSlot slot) {
        // Clear error state whenever the user edits any slot
        clearError();
        notifyValueChange();
    }

    void retreatFocus(InputOTPSlot fromSlot) {
        int idx = allSlots.indexOf(fromSlot);
        if (idx > 0) {
            InputOTPSlot prev = allSlots.get(idx - 1);
            prev.setSlotValue("");
            prev.focus();
        }
    }

    // -----------------------------------------------------------------------
    // Internal
    // -----------------------------------------------------------------------

    private void notifyValueChange() {
        if (valueListeners.isEmpty()) return;
        String value = getValue();
        valueListeners.forEach(l -> l.accept(value));
    }

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Returns a new fluent {@link InputOTPBuilder} to build an {@link InputOTP}.
     *
     * <pre>{@code
     * InputOTP otp = InputOTP.builder()
     *     .group(3).separator().group(3)
     *     .onComplete(value -> verify(value))
     *     .build();
     * }</pre>
     *
     * @return a new {@link InputOTPBuilder}
     */
    public static InputOTPBuilder builder() {
        return InputOTPBuilder.create();
    }

    /**
     * Returns a fluent configurator for an <strong>existing</strong> {@link InputOTP} instance.
     *
     * @param otp the OTP component to configure (not null)
     * @return a {@link InputOTPConfigurator.BaseInputOTPConfigurator}
     */
    public static InputOTPConfigurator.BaseInputOTPConfigurator configure(InputOTP otp) {
        return InputOTPConfigurator.configure(otp);
    }
}

