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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultInputOTPConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTP;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;

/**
 * Fluent configurator for {@link InputOTP} components.
 *
 * <p>Covers all InputOTP-specific properties plus the standard Holon Platform component
 * properties inherited from {@link ComponentConfigurator}, {@link HasSizeConfigurator},
 * {@link HasStyleConfigurator} and {@link HasEnabledConfigurator}.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see InputOTPBuilder
 * @see InputOTP
 */
public interface InputOTPConfigurator<C extends InputOTPConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>,
        HasEnabledConfigurator<C> {

    // -----------------------------------------------------------------------
    // Structure
    // -----------------------------------------------------------------------

    /**
     * Appends a new {@link com.holonplatform.vaadin.flow.vaadinplus.components.InputOTPGroup}
     * containing {@code count} empty slots.
     *
     * <pre>{@code
     * InputOTP.builder().group(3).separator().group(3).build();
     * }</pre>
     *
     * @param count number of slots in this group (≥ 1)
     * @return this configurator for chaining
     */
    C group(int count);

    /**
     * Appends an {@link com.holonplatform.vaadin.flow.vaadinplus.components.InputOTPSeparator}
     * with the default en-dash ({@code –}) glyph.
     *
     * @return this configurator for chaining
     */
    C separator();

    /**
     * Appends an {@link com.holonplatform.vaadin.flow.vaadinplus.components.InputOTPSeparator}
     * with custom content.
     *
     * @param content the separator component (null falls back to the default en-dash)
     * @return this configurator for chaining
     */
    C separator(Component content);

    // -----------------------------------------------------------------------
    // Input constraints
    // -----------------------------------------------------------------------

    /**
     * Sets the per-slot character pattern (HTML {@code pattern} attribute applied to
     * each slot's internal text field).
     * <br>Common values: {@code "[0-9]"} for numeric OTP,
     * {@code "[0-9a-zA-Z]"} for alphanumeric.
     *
     * @param pattern the per-slot regex pattern (null removes the constraint)
     * @return this configurator for chaining
     */
    C pattern(String pattern);

    /**
     * Sets all slots as read-only.
     *
     * @param readOnly {@code true} to make all slots read-only
     * @return this configurator for chaining
     */
    C readOnly(boolean readOnly);

    // -----------------------------------------------------------------------
    // Callbacks
    // -----------------------------------------------------------------------

    /**
     * Registers a completion handler invoked once when all slots are filled.
     * The complete OTP string is passed to the handler.
     *
     * <pre>{@code
     * .onComplete(value -> verifyCode(value))
     * }</pre>
     *
     * @param handler the completion handler (not null)
     * @return this configurator for chaining
     */
    C onComplete(SerializableConsumer<String> handler);

    /**
     * Registers a value-change listener invoked whenever any slot value changes.
     *
     * @param listener the listener (not null)
     * @return this configurator for chaining
     */
    C onValueChange(SerializableConsumer<String> listener);

    /**
     * Sets a synchronous OTP validator that is invoked automatically the moment all
     * slots are filled.
     *
     * <p>The function receives the complete OTP string and must return:
     * <ul>
     *   <li>{@code null} or an empty string — the code is <strong>valid</strong>;
     *       the {@code onComplete} handler is fired normally.</li>
     *   <li>A non-empty error message string — the code is <strong>invalid</strong>;
     *       the component enters the error state (red slot borders + error label below
     *       the OTP slots) and the {@code onComplete} handler is <em>not</em> invoked.
     *       The error is cleared automatically the next time the user edits any slot.</li>
     * </ul>
     *
     * <p>Prefer this over calling a service inside {@code onComplete} when the validation
     * is <strong>synchronous</strong> (e.g. a Spring {@code @Service} call to the DB).
     * For async / reactive flows use {@link #onComplete(SerializableConsumer)} and call
     * {@link InputOTP#setErrorMessage(String)} explicitly when the result arrives.
     *
     * <pre>{@code
     * InputOTP.builder()
     *     .group(6)
     *     .pattern("[0-9]")
     *     .validator(code -> otpService.verify(code) ? null : "Invalid verification code")
     *     .build();
     * }</pre>
     *
     * @param validator function: OTP value → {@code null}/empty when valid, error message when invalid
     * @return this configurator for chaining
     */
    C validator(SerializableFunction<String, String> validator);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Returns a configurator for an <strong>existing</strong> {@link InputOTP} instance.
     *
     * <pre>{@code
     * InputOTPConfigurator.configure(myOtp)
     *     .pattern("[0-9]")
     *     .readOnly(true);
     * }</pre>
     *
     * @param otp the OTP component to configure (not null)
     * @return a {@link BaseInputOTPConfigurator}
     */
    static BaseInputOTPConfigurator configure(InputOTP otp) {
        return new DefaultInputOTPConfigurator(otp);
    }

    /**
     * Base configurator type returned by {@link #configure(InputOTP)}.
     */
    interface BaseInputOTPConfigurator extends InputOTPConfigurator<BaseInputOTPConfigurator> {
    }
}

