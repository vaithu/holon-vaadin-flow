/*
 * Copyright 2016-2026 Axioma srl.
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

/**
 * Type-safe styling API for {@link LitRendererBuilder.GridCellBuilder} components.
 *
 * <p>Callers never write raw CSS class name strings — all visual properties are expressed
 * through this fluent builder API. The correct CSS classes are resolved internally.
 *
 * <p><b>Entry points:</b>
 * <pre>{@code
 * CellStyle.text()    // truncating <span> with ellipsis → TextStyle builder
 * CellStyle.span()    // wrapping <span> → SpanStyle builder
 * CellStyle.pill()    // badge / chip → PillStyle builder
 * CellStyle.icon()    // vaadin-icon → IconStyle builder
 * CellStyle.avatar()  // vaadin-avatar → AvatarStyle builder
 * CellStyle.image()   // <img> → ImageStyle builder
 * }</pre>
 *
 * <p><b>Common preset examples:</b>
 * <pre>{@code
 * CellStyle.text().title()    // semibold + header color  (primary name/title)
 * CellStyle.text().label()    // medium + secondary + small
 * CellStyle.text().caption()  // xs + muted               (timestamps, metadata)
 * CellStyle.text().amount()   // bold + monospace          (currency values)
 *
 * CellStyle.pill().success()  // green  (active, approved, paid)
 * CellStyle.pill().error()    // red    (failed, rejected, overdue)
 * CellStyle.pill().warning()  // amber  (pending, expiring)
 * CellStyle.pill().primary()  // blue   (in-progress, selected)
 * CellStyle.pill().contrast() // dark   (closed, archived)
 *
 * CellStyle.icon().primary()
 * CellStyle.icon().success()
 * CellStyle.icon().muted()
 * }</pre>
 *
 * @since 10.0.0
 */
public final class CellStyle {

    private CellStyle() {}

    // ── Entry points ─────────────────────────────────────────────────────────

    /** Start building a style for a truncating text span. */
    public static TextStyle text()   { return new TextStyle(); }

    /** Start building a style for a wrapping span. */
    public static SpanStyle span()   { return new SpanStyle(); }

    /** Start building a style for a badge / pill chip. */
    public static PillStyle pill()   { return new PillStyle(); }

    /** Start building a style for an icon. */
    public static IconStyle icon()   { return new IconStyle(); }

    /** Start building a style for an avatar. */
    public static AvatarStyle avatar() { return new AvatarStyle(); }

    /** Start building a style for an image. */
    public static ImageStyle image() { return new ImageStyle(); }

    // =========================================================================
    // TextStyle — truncating <span> with title/ellipsis
    // =========================================================================

    /**
     * Style configuration for truncating text components ({@code startText} / {@code endText}).
     *
     * <p>Start with {@link CellStyle#text()} and chain any combination of size,
     * weight and color modifiers, or use a named preset:
     * <pre>{@code
     * CellStyle.text().title()        // semibold + header color
     * CellStyle.text().bold().error() // bold red text
     * CellStyle.text().sm().muted()   // small muted text
     * }</pre>
     */
    public static final class TextStyle {

        private final StringBuilder css = new StringBuilder("gcb-text");

        private TextStyle() {}

        // ── Size ─────────────────────────────────────────────────────────────

        /** Extra-small font size. */
        public TextStyle xs()   { css.append(" gcb-text-xs"); return this; }

        /** Small font size. */
        public TextStyle sm()   { css.append(" gcb-text-s");  return this; }

        /** Medium (default) font size. */
        public TextStyle md()   { css.append(" gcb-text-m");  return this; }

        /** Large font size. */
        public TextStyle lg()   { css.append(" gcb-text-l");  return this; }

        /** Extra-large font size. */
        public TextStyle xl()   { css.append(" gcb-text-xl"); return this; }

        // ── Weight ─────────────────────────────────────────────────────��─────

        /** Font weight 700. */
        public TextStyle bold()     { css.append(" gcb-bold");     return this; }

        /** Font weight 600. */
        public TextStyle semibold() { css.append(" gcb-semibold"); return this; }

        /** Font weight 500. */
        public TextStyle medium()   { css.append(" gcb-medium");   return this; }

        /** Font weight 400 (default). */
        public TextStyle normal()   { css.append(" gcb-normal");   return this; }

        // ── Color ─────────────────────────────────────────────────────────────

        /** Primary brand color. */
        public TextStyle primary()   { css.append(" gcb-primary");   return this; }

        /** Secondary / supporting text color. */
        public TextStyle secondary() { css.append(" gcb-secondary"); return this; }

        /** Muted / tertiary text color — for metadata, hints. */
        public TextStyle muted()     { css.append(" gcb-tertiary");  return this; }

        /** Header text color — highest contrast. */
        public TextStyle header()    { css.append(" gcb-header");    return this; }

        /** Success / positive color (green). */
        public TextStyle success()   { css.append(" gcb-success");   return this; }

        /** Danger / error color (red). */
        public TextStyle error()     { css.append(" gcb-error");     return this; }

        /** Warning / caution color (amber). */
        public TextStyle warning()   { css.append(" gcb-warning");   return this; }

        // ── Monospace ────────────────────────────────────────────────────────

        /** Monospace font — for amounts, codes, IDs, file paths. */
        public TextStyle mono() { css.append(" gcb-mono"); return this; }

        // ── Named presets ─────────────────────────────────────────────────────

        /**
         * Semibold + header color.
         * Best for primary name or title fields in the top row.
         */
        public TextStyle title() { return semibold().header(); }

        /**
         * Medium weight + secondary color + small size.
         * Best for secondary label fields.
         */
        public TextStyle label() { return sm().medium().secondary(); }

        /**
         * XS size + muted color.
         * Best for timestamps, metadata, and supporting details in bottom rows.
         */
        public TextStyle caption() { return xs().muted(); }

        /**
         * Bold + monospace.
         * Best for monetary amounts or numeric KPI values.
         */
        public TextStyle amount() { return bold().mono(); }

        /**
         * Large + bold + header color.
         * Best for KPI headings.
         */
        public TextStyle heading() { return lg().bold().header(); }

        // ── Internal API ─────────────────────────────────────────────────────

        /** Returns the resolved CSS class string. */
        public String toCss() { return css.toString(); }
    }

    // =========================================================================
    // SpanStyle — wrapping <span>
    // =========================================================================

    /**
     * Style configuration for wrapping span components ({@code startSpan} / {@code endSpan}).
     */
    public static final class SpanStyle {

        private final StringBuilder css = new StringBuilder("gcb-span");

        private SpanStyle() {}

        // ── Size ─────────────────────────────────────────────────────────────

        public SpanStyle xs() { css.append(" gcb-text-xs"); return this; }
        public SpanStyle sm() { css.append(" gcb-text-s");  return this; }
        public SpanStyle md() { css.append(" gcb-text-m");  return this; }
        public SpanStyle lg() { css.append(" gcb-text-l");  return this; }

        // ── Weight ───────────────────────────────────────────────────────────

        public SpanStyle bold()     { css.append(" gcb-bold");     return this; }
        public SpanStyle semibold() { css.append(" gcb-semibold"); return this; }
        public SpanStyle medium()   { css.append(" gcb-medium");   return this; }

        // ── Color ─────────────────────────────────────────────────────────────

        public SpanStyle primary()   { css.append(" gcb-primary");   return this; }
        public SpanStyle secondary() { css.append(" gcb-secondary"); return this; }
        public SpanStyle muted()     { css.append(" gcb-tertiary");  return this; }
        public SpanStyle header()    { css.append(" gcb-header");    return this; }
        public SpanStyle success()   { css.append(" gcb-success");   return this; }
        public SpanStyle error()     { css.append(" gcb-error");     return this; }
        public SpanStyle warning()   { css.append(" gcb-warning");   return this; }

        // ── Named presets ─────────────────────────────────────────────────────

        /** Small + secondary color — default span/body style. */
        public SpanStyle body()    { return sm(); }

        /** XS + muted — caption/metadata lines. */
        public SpanStyle caption() { return xs().muted(); }

        // ── Internal API ─────────────────────────────────────────────────────

        public String toCss() { return css.toString(); }
    }

    // =========================================================================
    // PillStyle — badge / chip
    // =========================================================================

    /**
     * Style configuration for badge/pill components ({@code startPill} / {@code endPill}).
     *
     * <p>Colors are driven by the Vaadin badge {@code theme} attribute. Usage:
     * <pre>{@code
     * CellStyle.pill().success()  // green
     * CellStyle.pill().error()    // red
     * CellStyle.pill().warning()  // amber (custom CSS)
     * CellStyle.pill().primary()  // blue
     * CellStyle.pill().contrast() // dark
     * CellStyle.pill()            // neutral / default
     * }</pre>
     */
    public static final class PillStyle {

        private final StringBuilder cssCls = new StringBuilder("gcb-pill");
        private String theme = "badge small pill";

        private PillStyle() {}

        // ── Color / variant ───────────────────────────────────────────────────

        /**
         * Neutral pill (default) — no additional color.
         * Calling this is optional; it is the default.
         */
        public PillStyle neutral() {
            theme = "badge small pill";
            return this;
        }

        /** Blue / primary — in-progress, selected, info. */
        public PillStyle primary() {
            theme = "badge primary small pill";
            return this;
        }

        /** Green / success — active, approved, paid, complete. */
        public PillStyle success() {
            theme = "badge success small pill";
            return this;
        }

        /** Red / error — failed, rejected, expired, overdue. */
        public PillStyle error() {
            theme = "badge error small pill";
            return this;
        }

        /** Dark / contrast — closed, archived, inactive. */
        public PillStyle contrast() {
            theme = "badge contrast small pill";
            return this;
        }

        /** Amber / warning — pending, expiring, needs attention. */
        public PillStyle warning() {
            theme = "badge small pill";
            cssCls.append(" gcb-pill-warning");
            return this;
        }

        /** Alias for {@link #primary()} — informational pills. */
        public PillStyle info() { return primary(); }

        // ── Internal API ─────────────────────────────────────────────────────

        public String toCss()   { return cssCls.toString(); }
        public String toTheme() { return theme; }
    }

    // =========================================================================
    // IconStyle — vaadin-icon
    // =========================================================================

    /**
     * Style configuration for icon components ({@code startIcon} / {@code endIcon}).
     */
    public static final class IconStyle {

        private final StringBuilder css = new StringBuilder("gcb-icon");

        private IconStyle() {}

        // ── Color ─────────────────────────────────────────────────────────────

        public IconStyle primary()   { css.append(" gcb-primary");   return this; }
        public IconStyle secondary() { css.append(" gcb-secondary"); return this; }
        public IconStyle muted()     { css.append(" gcb-tertiary");  return this; }
        public IconStyle header()    { css.append(" gcb-header");    return this; }
        public IconStyle success()   { css.append(" gcb-success");   return this; }
        public IconStyle error()     { css.append(" gcb-error");     return this; }
        public IconStyle warning()   { css.append(" gcb-warning");   return this; }

        // ── Size ─────────────────────────────────────────────────────────────

        /** Small icon (--lumo-icon-size-s). */
        public IconStyle sm() { css.append(" gcb-icon-sm"); return this; }

        /** Large icon (--lumo-icon-size-l). */
        public IconStyle lg() { css.append(" gcb-icon-lg"); return this; }

        // ── Internal API ─────────────────────────────────────────────────────

        public String toCss() { return css.toString(); }
    }

    // =========================================================================
    // AvatarStyle — vaadin-avatar
    // =========================================================================

    /**
     * Style configuration for avatar components ({@code startAvatar} / {@code endAvatar}
     * and {@code mediaAvatar}).
     */
    public static final class AvatarStyle {

        private final StringBuilder css = new StringBuilder("gcb-avatar");

        private AvatarStyle() {}

        /** Small avatar. */
        public AvatarStyle sm() { css.append(" gcb-avatar-sm"); return this; }

        /** Large avatar. */
        public AvatarStyle lg() { css.append(" gcb-avatar-lg"); return this; }

        // ── Internal API ─────────────────────────────────────────────────────

        public String toCss() { return css.toString(); }
    }

    // =========================================================================
    // ImageStyle — <img>
    // =========================================================================

    /**
     * Style configuration for image components ({@code startImage} / {@code endImage}
     * and {@code mediaImage}).
     */
    public static final class ImageStyle {

        private final StringBuilder css = new StringBuilder("gcb-img");

        private ImageStyle() {}

        /** Circular border radius (100%). */
        public ImageStyle circle()  { css.append(" gcb-img-circle");  return this; }

        /** Rounded corners (medium border radius). */
        public ImageStyle rounded() { css.append(" gcb-img-rounded"); return this; }

        /** Small image size. */
        public ImageStyle sm()      { css.append(" gcb-img-sm");      return this; }

        /** Large image size. */
        public ImageStyle lg()      { css.append(" gcb-img-lg");      return this; }

        /** Object-fit: cover (fills the box, crops if needed). Default. */
        public ImageStyle cover()   { css.append(" gcb-img-cover");   return this; }

        /** Object-fit: contain (fits inside the box, may letterbox). */
        public ImageStyle contain() { css.append(" gcb-img-contain"); return this; }

        // ── Internal API ─────────────────────────────────────────────────────

        public String toCss() { return css.toString(); }
    }

    // ── Package-internal defaults (used when no style is provided) ────────────

    public static final TextStyle   DEFAULT_TEXT   = new TextStyle();
    public static final SpanStyle   DEFAULT_SPAN   = new SpanStyle();
    public static final PillStyle   DEFAULT_PILL   = new PillStyle();
    public static final IconStyle   DEFAULT_ICON   = new IconStyle();
    public static final AvatarStyle DEFAULT_AVATAR = new AvatarStyle();
    public static final ImageStyle  DEFAULT_IMAGE  = new ImageStyle();
}


