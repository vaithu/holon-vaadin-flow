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
import com.vaadin.flow.component.html.Div;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A group of adjacent {@link InputOTPSlot}s inside an {@link InputOTP}.
 *
 * <p>Renders as:
 * <pre>
 * &lt;div class="input-otp__group"&gt;
 *   &lt;div class="input-otp__slot"&gt;...&lt;/div&gt;
 *   &lt;div class="input-otp__slot"&gt;...&lt;/div&gt;
 *   &lt;div class="input-otp__slot"&gt;...&lt;/div&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>Mirrors the shadcn/ui {@code InputOTPGroup} element. Use multiple groups with an
 * {@link InputOTPSeparator} between them to create the classic {@code 3â€“3} or {@code 3â€“2â€“1}
 * OTP patterns.
 *
 * <p>All visual styling is defined in {@code input-otp.css}.
 *
 * @see InputOTP
 * @see InputOTPSlot
 * @see InputOTPSeparator
 */
public class InputOTPGroup extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<InputOTPSlot> slots = new ArrayList<>();

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates an empty group. Add slots via {@link #add(InputOTPSlot...)}.
     */
    public InputOTPGroup() {
        addClassName("input-otp__group");
    }

    // -----------------------------------------------------------------------
    // Slot management
    // -----------------------------------------------------------------------

    /**
     * Appends one or more {@link InputOTPSlot}s to this group.
     *
     * @param slots the slots to content (null-safe; individual null elements are skipped)
     */
    public void add(InputOTPSlot... slots) {
        if (slots == null) return;
        for (InputOTPSlot slot : slots) {
            if (slot != null) {
                this.slots.add(slot);
                super.add(slot);
            }
        }
    }

    /**
     * Returns an unmodifiable view of the slots currently in this group,
     * in insertion order.
     *
     * @return immutable list of slots
     */
    public List<InputOTPSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }
}
