package com.holonplatform.vaadin.flow.components.builders;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Mixin interface that adds conditional configuration methods to any fluent builder.
 *
 * <p>Added to {@link ComponentConfigurator} as a parent interface, so every builder
 * in the framework inherits these methods automatically — no concrete class changes needed.</p>
 *
 * <p>All methods return {@code SELF} (the concrete builder type), so they slot into
 * any existing chain without breaking it:</p>
 *
 * <pre>{@code
 * Components.<Deal>listing(Deal.class)
 *     .fetch(svc::findAll)
 *     .columns("name", "stage", "amount")
 *     .applyIf(canSeeMargin, b -> b.columns("name", "stage", "amount", "margin"))
 *     .applyIf(canEdit,      b -> b.withEditAction(this::openEdit))
 *     .applyIf(canDelete,    b -> b.withDeleteAction(this::delete))
 *     .paginated()
 *     .build();
 *
 * EntityFormPanel.bean(Deal.class)
 *     .applyIf(isAdmin, b -> b.properties("name", "stage", "margin"))
 *     .applyUnless(isAdmin, b -> b.properties("name", "stage"))
 *     .saveButton(btn -> {}, this::save)
 *     .build();
 * }</pre>
 *
 * @param <SELF> the concrete builder type, enabling fluent self-return
 */
public interface ConditionalConfigurable<SELF extends ConditionalConfigurable<SELF>> {

    /**
     * Applies {@code configure} to this builder only when {@code condition} is {@code true};
     * returns this builder unchanged otherwise.
     *
     * @param condition  when {@code true} the configurator is applied
     * @param configure  the configurator to apply
     * @return this builder (configured or unchanged)
     */
    @SuppressWarnings("unchecked")
    default SELF applyIf(boolean condition, UnaryOperator<SELF> configure) {
        return condition ? configure.apply((SELF) this) : (SELF) this;
    }

    /**
     * Applies {@code configure} to this builder only when {@code condition} is {@code false};
     * returns this builder unchanged otherwise.
     *
     * <p>Convenience inverse of {@link #applyIf(boolean, UnaryOperator)}.</p>
     *
     * @param condition  when {@code false} the configurator is applied
     * @param configure  the configurator to apply
     * @return this builder (configured or unchanged)
     */
    @SuppressWarnings("unchecked")
    default SELF applyUnless(boolean condition, UnaryOperator<SELF> configure) {
        return condition ? (SELF) this : configure.apply((SELF) this);
    }

    /**
     * Lazily evaluates {@code condition} and applies {@code configure} when it
     * returns {@code true}. Use this overload when the condition itself is expensive
     * or has side effects that should only run when needed.
     *
     * @param condition  supplier evaluated at call time; configurator applied when {@code true}
     * @param configure  the configurator to apply
     * @return this builder (configured or unchanged)
     */
    default SELF applyIf(Supplier<Boolean> condition, UnaryOperator<SELF> configure) {
        return applyIf(Boolean.TRUE.equals(condition.get()), configure);
    }

    /**
     * Always applies {@code configure} to this builder and returns it.
     *
     * <p>This is the <em>tap</em> pattern: useful for inserting logging, metrics, or
     * unconditional side-effect calls in the middle of a chain without breaking it:</p>
     *
     * <pre>{@code
     * Components.<Deal>listing(Deal.class)
     *     .fetch(svc::findAll)
     *     .also(b -> log.debug("Building deal listing for tenant {}", tenantId))
     *     .applyIf(canEdit, b -> b.withEditAction(this::openEdit))
     *     .build();
     * }</pre>
     *
     * @param configure  consumer applied unconditionally to this builder
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    default SELF also(Consumer<SELF> configure) {
        configure.accept((SELF) this);
        return (SELF) this;
    }
}
