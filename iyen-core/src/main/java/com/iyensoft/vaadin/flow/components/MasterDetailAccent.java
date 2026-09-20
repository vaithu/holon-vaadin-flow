package com.iyensoft.vaadin.flow.components;

/**
 * Pre-defined accent colour variants for {@link MasterDetailLayout} row highlighting.
 *
 * <p>Pass a variant's {@link #cssClass()} to
 * {@code MasterDetailConfigurator.withAccentColorProvider(Function)} to change the
 * left-bar colour and background tint dynamically per selected item:</p>
 *
 * <pre>{@code
 * Components.masterDetail(Order.class)
 *     .withAccentColorProvider(order -> switch (order.getStatus()) {
 *         case ACTIVE   -> MasterDetailAccent.SUCCESS.cssClass();
 *         case OVERDUE  -> MasterDetailAccent.DANGER.cssClass();
 *         case PENDING  -> MasterDetailAccent.WARNING.cssClass();
 *         default       -> MasterDetailAccent.DEFAULT.cssClass();
 *     })
 *     .master(m -> m.listing(l -> l.fetch(...)))
 *     .detail(d -> d.withDetailSync(o -> populate(o)))
 *     .build();
 * }</pre>
 *
 * <p>Each variant maps to a CSS class defined in {@code master-detail-v2.css} that
 * overrides {@code --mdl-selected-accent} and {@code --mdl-selected-bg} on the
 * {@code .master-detail-container} element.  Java code only assigns the class name;
 * all colour values live in CSS.</p>
 *
 * <p>Custom colours: define your own CSS class anywhere in your stylesheet and return
 * its name directly from the provider function — no enum value needed:</p>
 * <pre>{@code
 * // In your CSS:
 * .my-teal { --mdl-selected-accent: #0d9488; --mdl-selected-bg: rgba(13,148,136,0.08); }
 *
 * // In Java:
 * .withAccentColorProvider(item -> "my-teal")
 * }</pre>
 */
public enum MasterDetailAccent {

    /** Default blue (#1864ff). */
    DEFAULT("mdl-accent--blue"),

    /** Success green. Resolves to {@code --success} token or {@code #16a34a}. */
    SUCCESS("mdl-accent--success"),

    /** Warning amber. Resolves to {@code --warning} token or {@code #d97706}. */
    WARNING("mdl-accent--warning"),

    /** Danger red. Resolves to {@code --error} token or {@code #dc2626}. */
    DANGER("mdl-accent--danger"),

    /** Info cyan. Resolves to {@code --info} token or {@code #0891b2}. */
    INFO("mdl-accent--info"),

    /** Purple (#7c3aed). */
    PURPLE("mdl-accent--purple"),

    /** Neutral gray (#6b7280). */
    GRAY("mdl-accent--gray");

    private final String cssClass;

    MasterDetailAccent(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * The CSS class name that must be applied to the {@code .master-detail-container}
     * element to activate this accent colour.
     *
     * @return CSS class name string, never {@code null}
     */
    public String cssClass() {
        return cssClass;
    }
}

