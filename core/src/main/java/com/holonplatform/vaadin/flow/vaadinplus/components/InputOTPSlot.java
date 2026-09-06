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

import java.io.Serial;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * A single character input slot inside an {@link InputOTPGroup} / {@link InputOTP}.
 *
 * <p>Renders as:
 * <pre>
 * &lt;div class="input-otp__slot"&gt;
 *   &lt;vaadin-text-field class="input-otp__slot-input" maxlength="1"&gt;&lt;/vaadin-text-field&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>Slots are added to an {@link InputOTPGroup}, which is then added to the root
 * {@link InputOTP}.  Direct construction is only needed when building the OTP manually;
 * the fluent builder creates slots automatically via
 * {@link com.holonplatform.vaadin.flow.components.builders.InputOTPConfigurator#group(int)}.
 *
 * <p>All visual styling is defined in {@code input-otp.css}.
 *
 * @see InputOTP
 * @see InputOTPGroup
 */
public class InputOTPSlot extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    private final TextField input;
    private InputOTP manager;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates a new, empty OTP slot.
     */
    public InputOTPSlot() {
        addClassName("input-otp__slot");

        this.input = new TextField();
        this.input.setMaxLength(1);
        this.input.addClassName("input-otp__slot-input");
        this.input.setValueChangeMode(ValueChangeMode.EAGER);
        // Aura built-in variant  centres text AND caret via the theme's own <input> styling.
        // In Vaadin 25 the native <input> is a light-DOM child, so ::part(value) is a no-op;
        // TextFieldVariant.ALIGN_CENTER is the correct theme-level hook.
        this.input.addThemeVariants(TextFieldVariant.ALIGN_CENTER);

        // Select-all on focus so that typing always replaces the current character
        // and auto-advance works even when the slot is already filled.
        this.input.addFocusListener(e ->
                input.getElement().executeJs("this.inputElement.select()"));

        // Auto-advance / notify on user-driven value change only
        this.input.addValueChangeListener(e -> {
            if (!e.isFromClient() || manager == null) return;
            if (!e.getValue().isEmpty()) {
                manager.onSlotFilled(this);
            } else {
                manager.onSlotCleared(this);
            }
        });

        // Retreat to previous slot on backspace when the slot is already empty
        this.input.addKeyDownListener(Key.BACKSPACE, e -> {
            if (manager != null && input.getValue().isEmpty()) {
                manager.retreatFocus(this);
            }
        });

        add(input);
    }

    // -----------------------------------------------------------------------
    // Package-private API used by InputOTP
    // -----------------------------------------------------------------------

    /**
     * Sets the ARIA label for this slot (e.g. "Digit 1 of 6").
     * Called by the parent {@link InputOTP} when slots are registered.
     *
     * @param label the accessible name (not null)
     */
    void setSlotAriaLabel(String label) {
        input.getElement().setAttribute("aria-label", label);
    }

    void setManager(InputOTP manager) {
        this.manager = manager;
    }

    void focus() {
        input.focus();
    }

    public String getSlotValue() {
        return input.getValue();
    }

    public void setSlotValue(String value) {
        input.setValue(value == null ? "" : value);
    }

    public void setPattern(String pattern) {
        if (pattern != null) {
            // setAllowedCharPattern blocks invalid characters client-side, BEFORE they
            // enter the input value  unlike setPattern(), which is HTML5 form validation
            // and does nothing during typing.
            input.setAllowedCharPattern(pattern);
            // Keep the DOM attribute so tests and CSS attribute selectors still work
            input.getElement().setAttribute("pattern", pattern);
            attachInvalidAttemptFeedback(pattern);
        } else {
            input.setAllowedCharPattern(null);
            input.getElement().removeAttribute("pattern");
        }
    }

    /**
     * Registers a client-side {@code keydown} listener (once per slot) that adds the
     * {@code input-otp__slot--invalid} CSS class when the user presses a character that is
     * blocked by {@code allowedCharPattern}.  The class is removed automatically after
     * 500 ms, triggering the shake + red-border CSS animation defined in
     * {@code input-otp.css}.
     *
     * <p>The listener is registered via {@code executeJs} so it fires entirely on the
     * client  no server round-trip on each invalid keystroke.  The current pattern regex
     * is stored on the element ({@code el.__otpPattern}) and updated if
     * {@link #setPattern(String)} is called again with a different pattern.</p>
     */
    private void attachInvalidAttemptFeedback(String pattern) {
        input.getElement().executeJs("""
                (function(el, pattern) {
                    el.__otpPattern = new RegExp(pattern);
                    if (!el.__otpListener) {
                        el.__otpListener = true;
                        el.addEventListener('keydown', function(e) {
                            if (!el.__otpPattern) return;
                            if (e.key.length !== 1 || e.ctrlKey || e.metaKey || e.altKey) return;
                            if (!el.__otpPattern.test(e.key)) {
                                var slot = el.closest('.input-otp__slot');
                                if (!slot) return;
                                slot.classList.add('input-otp__slot--invalid');
                                setTimeout(function() {
                                    slot.classList.remove('input-otp__slot--invalid');
                                }, 500);
                            }
                        });
                    }
                })(this, $0);
                """, pattern);
    }

    public void setReadOnly(boolean readOnly) {
        input.setReadOnly(readOnly);
        // Mirror as a DOM attribute so hasAttribute("readonly") works in tests and CSS selectors
        if (readOnly) {
            input.getElement().setAttribute("readonly", "");
        } else {
            input.getElement().removeAttribute("readonly");
        }
    }

    public void setSlotEnabled(boolean enabled) {
        input.setEnabled(enabled);
    }

    /**
     * Returns the backing {@link TextField} for advanced customisation (e.g. adding
     * ARIA labels from test code).
     *
     * @return the internal text field (never null)
     */
    public TextField getTextField() {
        return input;
    }
}

