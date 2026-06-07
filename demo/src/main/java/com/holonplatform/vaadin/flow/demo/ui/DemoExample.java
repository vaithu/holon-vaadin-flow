package com.holonplatform.vaadin.flow.demo.ui;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.tabs.Tabs;

/**
 * Reusable demo card that renders a named example with a two-tab toggle:
 * <ul>
 *   <li><strong>Preview</strong> – the live component</li>
 *   <li><strong>Code</strong>    – the corresponding Java snippet in a {@code <pre>} block</li>
 * </ul>
 *
 * <p>No CSS class names are assigned; styling comes from the global theme.</p>
 */
public class DemoExample extends ResponsiveDiv {

    /**
     * @param title   short name displayed above the tabs
     * @param preview the live component to show in the Preview pane
     * @param code    Java code snippet shown in the Code pane (leading indent is stripped)
     */
    public DemoExample(String title, Component preview, String code) {
        addClassName("rdiv-card");
        // ── Heading ─────────────────────────────────────────────────────────
        var heading = new H3(title);

        // ── Code pane ────────────────────────────────────────────────────────
        var pre = new Pre();
        pre.getElement().setText(stripCommonIndent(code));

        var codePane = new Div(pre);

        Div container = ResponsiveDiv.flex().column().gapM().marginS().build();

        Tabs tabs = Components.lazyTabs()
                .withContainer(container)
                .withLazyTab("Preview", () -> preview)
                .withLazyTab("Code", () -> codePane)
                .build();

        ResponsiveDiv.configure(this)
                        .add(heading, tabs,container);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Removes common leading whitespace from every non-blank line (like Java 15
     * text-block {@code stripIndent()}), so snippets can be indented naturally
     * inside the calling code without that indent appearing in the rendered block.
     */
    private static String stripCommonIndent(String raw) {
        if (raw == null || raw.isBlank()) return "";

        String[] lines = raw.split("\n", -1);

        // Find the minimum indentation among non-blank lines
        int minIndent = Integer.MAX_VALUE;
        for (String line : lines) {
            if (line.isBlank()) continue;
            int indent = 0;
            for (char c : line.toCharArray()) {
                if (c == ' ')       indent++;
                else if (c == '\t') indent += 4;
                else break;
            }
            minIndent = Math.min(minIndent, indent);
        }
        if (minIndent == Integer.MAX_VALUE) minIndent = 0;

        // Strip that many leading spaces from every line
        int strip = minIndent;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isBlank() && line.length() >= strip) {
                line = line.substring(strip);
            } else if (line.isBlank()) {
                line = "";
            }
            if (i > 0) sb.append('\n');
            sb.append(line);
        }
        return sb.toString().strip();
    }
}

