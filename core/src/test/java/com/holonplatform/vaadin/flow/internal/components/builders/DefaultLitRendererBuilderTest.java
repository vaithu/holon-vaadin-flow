package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.LitRendererBuilder;
import com.vaadin.flow.data.renderer.LitRenderer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link DefaultLitRendererBuilder} — verifies every element type
 * renders the correct HTML fragment and that properties / functions are registered.
 *
 * <p>Template-string rendering is pure Java (no Vaadin runtime required), so these
 * tests run without a servlet container or browser.
 */
class DefaultLitRendererBuilderTest {

    // ── Helper ──────────────────────────────────────────────────────────────

    /** Extracts the compiled template string from a {@link LitRenderer}. */
    private static String templateOf(LitRenderer<?> renderer) {
        // Try method first (protected in Vaadin 24+), then field fallbacks
        for (String name : new String[]{"getTemplateExpression"}) {
            try {
                var m = LitRenderer.class.getDeclaredMethod(name);
                m.setAccessible(true);
                return (String) m.invoke(renderer);
            } catch (Exception ignored) { /* try fields */ }
        }
        for (String fieldName : new String[]{"templateExpression", "template"}) {
            try {
                Field f = LitRenderer.class.getDeclaredField(fieldName);
                f.setAccessible(true);
                return (String) f.get(renderer);
            } catch (NoSuchFieldException | IllegalAccessException ignored) { /* try next */ }
        }
        return renderer.toString();
    }

    // ── span ────────────────────────────────────────────────────────────────

    @Test
    void span_minimalText() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .span(s -> s.text("${item.name}"))
                .build();
        assertThat(templateOf(r)).isEqualTo("<span>${item.name}</span>");
    }

    @Test
    void span_withClassNameAndStyle() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .span(s -> s.text("hello").className("bold").style("color:red"))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("class=\"bold\"");
        assertThat(html).contains("style=\"color:red\"");
        assertThat(html).startsWith("<span");
        assertThat(html).endsWith("</span>");
    }

    @Test
    void span_withArbitraryAttribute() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .span(s -> s.text("t").attribute("data-id", "42"))
                .build();
        assertThat(templateOf(r)).contains("data-id=\"42\"");
    }

    @Test
    void span_emptyText() {
        // no text() call → no text node in output
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .span(s -> s.className("x"))
                .build();
        assertThat(templateOf(r)).isEqualTo("<span class=\"x\"></span>");
    }

    // ── div ─────────────────────────────────────────────────────────────────

    @Test
    void div_withText() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .div(d -> d.text("hello"))
                .build();
        assertThat(templateOf(r)).isEqualTo("<div>hello</div>");
    }

    @Test
    void div_nestedSpan() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .div(d -> d.span(s -> s.text("inner")))
                .build();
        assertThat(templateOf(r)).isEqualTo("<div><span>inner</span></div>");
    }

    @Test
    void div_nestedDiv() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .div(outer -> outer.div(inner -> inner.text("deep")))
                .build();
        assertThat(templateOf(r)).isEqualTo("<div><div>deep</div></div>");
    }

    @Test
    void div_nestedIcon() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .div(d -> d.icon(i -> i.icon("vaadin:user")))
                .build();
        assertThat(templateOf(r)).isEqualTo("<div><vaadin-icon icon=\"vaadin:user\"></vaadin-icon></div>");
    }

    @Test
    void div_nestedImg() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .div(d -> d.img(i -> i.src("${item.photo}").alt("photo")))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("src=\"${item.photo}\"");
        assertThat(html).contains("alt=\"photo\"");
    }

    @Test
    void div_rawHtml() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .div(d -> d.html("<b>raw</b>"))
                .build();
        assertThat(templateOf(r)).isEqualTo("<div><b>raw</b></div>");
    }

        @Test
        void mobileGridColumn_withPrimaryAsLitEmbedsRendererTemplate() {
        LitRenderer<String> nested = LitRendererBuilder.<String>create()
            .span(s -> s.text("nested"))
            .build();

        LitRenderer<String> r = new MobileGridColumnLitRenderer<String>()
            .withPrimaryAsLit(nested)
            .build();

        assertThat(templateOf(r)).contains("<span>nested</span>");
        }

        // ── mobileGridColumn ───────────────────────────────────────────────────

        @Test
        void mobileGridColumn_configureSecondaryRendersStackedContent() {
        LitRenderer<String> r = new MobileGridColumnLitRenderer<String>()
            .configureSecondary(stack -> stack
                .className("stack")
                .span(s -> s.text("first"))
                .span(s -> s.text("second")))
            .build();

        String html = templateOf(r);
        assertThat(html).contains("<div class=\"mobile-grid-secondary\"");
        assertThat(html).contains("<div class=\"stack\"><span>first</span><span>second</span></div>");
        }

        @Test
        void mobileGridColumn_configureSecondaryRejectsNullConfigurator() {
        assertThatThrownBy(() -> new MobileGridColumnLitRenderer<String>().configureSecondary(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Configurator must not be null");
        }

    // ── horizontalLayout ────────────────────────────────────────────────────

    @Test
    void horizontalLayout_withTheme() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .horizontalLayout(h -> h.theme("spacing").span(s -> s.text("x")))
                .build();
        String html = templateOf(r);
        assertThat(html).startsWith("<vaadin-horizontal-layout");
        assertThat(html).contains("theme=\"spacing\"");
        assertThat(html).contains("<span>x</span>");
        assertThat(html).endsWith("</vaadin-horizontal-layout>");
    }

    @Test
    void horizontalLayout_withAvatar() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .horizontalLayout(h -> h
                        .avatar(a -> a.name("${item.name}").img("${item.photo}").abbr("JD")))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("name=\"${item.name}\"");
        assertThat(html).contains("img=\"${item.photo}\"");
        assertThat(html).contains("abbr=\"JD\"");
        assertThat(html).contains("<vaadin-avatar");
    }

    @Test
    void horizontalLayout_withButton() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .horizontalLayout(h -> h
                        .vaadinButton(b -> b
                                .text("Delete")
                                .theme("small error")
                                .onClick("handleDelete")))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("<vaadin-button");
        assertThat(html).contains("theme=\"small error\"");
        assertThat(html).contains("@click=\"${handleDelete}\"");
        assertThat(html).contains("Delete");
    }

    @Test
    void horizontalLayout_withCheckbox() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .horizontalLayout(h -> h.checkbox(c -> c.checked("${item.active}").readOnly()))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("?checked=\"${item.active}\"");
        assertThat(html).contains("onclick=\"return false;\"");
    }

    @Test
    void horizontalLayout_withProgressBar() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .horizontalLayout(h -> h.progressBar(p -> p.value("${item.progress}").min("0").max("1")))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("<vaadin-progress-bar");
        assertThat(html).contains("value=\"${item.progress}\"");
        assertThat(html).contains("min=\"0\"");
        assertThat(html).contains("max=\"1\"");
    }

    // ── verticalLayout ───────────────────────────────────────────────────────

    @Test
    void verticalLayout_tagName() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .verticalLayout(v -> v.span(s -> s.text("y")))
                .build();
        String html = templateOf(r);
        assertThat(html).startsWith("<vaadin-vertical-layout");
        assertThat(html).endsWith("</vaadin-vertical-layout>");
    }

    // ── avatar (top-level) ───────────────────────────────────────────────────

    @Test
    void avatar_allAttributes() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .avatar(a -> a.name("Jane").img("/jane.png").abbr("JD").className("small"))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("name=\"Jane\"");
        assertThat(html).contains("img=\"/jane.png\"");
        assertThat(html).contains("abbr=\"JD\"");
        assertThat(html).contains("class=\"small\"");
    }

    @Test
    void avatar_minimal() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .avatar(a -> a.name("${item.name}"))
                .build();
        assertThat(templateOf(r)).isEqualTo("<vaadin-avatar name=\"${item.name}\"></vaadin-avatar>");
    }

    // ── button (top-level) ───────────────────────────────────────────────────

    @Test
    void button_withIcon() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .vaadinButton(b -> b
                        .icon(i -> i.icon("vaadin:trash"))
                        .text("Remove")
                        .onClick("onRemove")
                        .disabled("${item.locked}"))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("<vaadin-icon icon=\"vaadin:trash\">");
        assertThat(html).contains("@click=\"${onRemove}\"");
        assertThat(html).contains("?disabled=\"${item.locked}\"");
        assertThat(html).contains("Remove");
    }

    // ── icon (top-level) ────────────────────────────────────────────────────

    @Test
    void icon_withName() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .icon(i -> i.icon("vaadin:check"))
                .build();
        assertThat(templateOf(r)).isEqualTo("<vaadin-icon icon=\"vaadin:check\"></vaadin-icon>");
    }

    @Test
    void icon_withSrc() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .icon(i -> i.src("${item.iconUrl}"))
                .build();
        assertThat(templateOf(r)).isEqualTo("<vaadin-icon src=\"${item.iconUrl}\"></vaadin-icon>");
    }

    // ── checkbox (top-level) ─────────────────────────────────────────────────

    @Test
    void checkbox_readOnly() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .checkbox(c -> c.checked("${item.active}").readOnly())
                .build();
        String html = templateOf(r);
        assertThat(html).contains("?checked=\"${item.active}\"");
        assertThat(html).contains("onclick=\"return false;\"");
        assertThat(html).contains("onkeydown=\"return false;\"");
    }

    @Test
    void checkbox_noReadOnly() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .checkbox(c -> c.checked("true"))
                .build();
        assertThat(templateOf(r)).doesNotContain("onclick=");
    }

    // ── img (top-level) ──────────────────────────────────────────────────────

    @Test
    void img_allAttributes() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .img(i -> i.src("/logo.png").alt("Logo").width("48").height("48"))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("src=\"/logo.png\"");
        assertThat(html).contains("alt=\"Logo\"");
        assertThat(html).contains("width=\"48\"");
        assertThat(html).contains("height=\"48\"");
        assertThat(html).endsWith(" />");
    }

    // ── progressBar (top-level) ───────────────────────────────────────────────

    @Test
    void progressBar_withBindings() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .progressBar(p -> p.value("${item.pct}").min("0").max("100"))
                .build();
        String html = templateOf(r);
        assertThat(html).startsWith("<vaadin-progress-bar");
        assertThat(html).contains("value=\"${item.pct}\"");
    }

    // ── rawHtml ───────────────────────────────────────────────────────────────

    @Test
    void html_rawTemplate() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .html("<b>raw ${item.name}</b>")
                .build();
        assertThat(templateOf(r)).isEqualTo("<b>raw ${item.name}</b>");
    }

    // ── multiple fragments ─────────────────────────────────────────────────────

    @Test
    void multipleFragmentsAreConcatenated() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .span(s -> s.text("A"))
                .span(s -> s.text("B"))
                .span(s -> s.text("C"))
                .build();
        assertThat(templateOf(r)).isEqualTo("<span>A</span><span>B</span><span>C</span>");
    }

    // ── withProperty ──────────────────────────────────────────────────────────

    @Test
    void withProperty_registeredOnRenderer() {
        // Build should not throw; properties are forwarded to LitRenderer.withProperty
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .span(s -> s.text("${item.upper}"))
                .withProperty("upper", String::toUpperCase)
                .build();
        assertThat(r).isNotNull();
        // Template still well-formed
        assertThat(templateOf(r)).isEqualTo("<span>${item.upper}</span>");
    }

    // ── withFunction ─────────────────────────────────────────────────────────

    @Test
    void withFunction_registeredOnRenderer() {
        // Verify build() does not throw even when a function is registered
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .vaadinButton(b -> b.text("Go").onClick("doGo"))
                .withFunction("doGo", (item, arg) -> { /* server handler */ })
                .build();
        assertThat(r).isNotNull();
    }

    // ── edge cases ───────────────────────────��────────────────────────────────

    @Test
    void emptyBuilder_producesEmptyTemplate() {
        LitRenderer<String> r = LitRendererBuilder.<String>create().build();
        assertThat(templateOf(r)).isEmpty();
    }

    @Test
    void className_style_attribute_chainable() {
        // Verify fluid chaining on a span element does not lose values
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .span(s -> s
                        .className("cls")
                        .style("color:blue")
                        .attribute("aria-label", "name")
                        .text("${item.name}"))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("class=\"cls\"");
        assertThat(html).contains("style=\"color:blue\"");
        assertThat(html).contains("aria-label=\"name\"");
        assertThat(html).contains("${item.name}");
    }

    @Test
    void nestedLayouts_withMultipleChildren() {
        LitRenderer<String> r = LitRendererBuilder.<String>create()
                .horizontalLayout(h -> h
                        .avatar(a -> a.name("${item.name}"))
                        .div(d -> d
                                .span(s -> s.text("${item.name}").className("primary"))
                                .span(s -> s.text("${item.email}").className("secondary"))))
                .build();
        String html = templateOf(r);
        assertThat(html).contains("<vaadin-avatar");
        assertThat(html).contains("<div>");
        assertThat(html).contains("class=\"primary\"");
        assertThat(html).contains("class=\"secondary\"");
    }
}

