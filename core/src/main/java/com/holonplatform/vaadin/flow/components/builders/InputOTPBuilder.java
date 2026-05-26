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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultInputOTPBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTP;

/**
 * Fluent builder to create and configure {@link InputOTP} components.
 *
 * <p><strong>Usage examples:</strong>
 *
 * <p>6-digit numeric OTP (single group):
 * <pre>{@code
 * InputOTP otp = InputOTP.builder()
 *     .group(6)
 *     .pattern("[0-9]")
 *     .onComplete(value -> verifyCode(value))
 *     .build();
 * }</pre>
 *
 * <p>Classic 3-dash-3 layout:
 * <pre>{@code
 * InputOTP otp = InputOTP.builder()
 *     .group(3)
 *     .separator()
 *     .group(3)
 *     .pattern("[0-9]")
 *     .onComplete(value -> verifyCode(value))
 *     .build();
 * }</pre>
 *
 * <p>Alphanumeric, read-only preview:
 * <pre>{@code
 * InputOTP otp = InputOTP.builder()
 *     .group(4)
 *     .separator()
 *     .group(4)
 *     .readOnly(true)
 *     .build();
 * otp.setValue("ABCD1234");
 * }</pre>
 *
 * @see InputOTPConfigurator
 * @see InputOTP
 */
public interface InputOTPBuilder
        extends InputOTPConfigurator<InputOTPBuilder>,
        ComponentBuilder<InputOTP, InputOTPBuilder> {

    /**
     * Create a new {@link InputOTPBuilder}.
     *
     * @return a new {@link InputOTPBuilder}
     */
    static InputOTPBuilder create() {
        return new DefaultInputOTPBuilder();
    }
}

