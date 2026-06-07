package com.holonplatform.vaadin.flow.demo;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * Entry point for the Holon Vaadin Flow component showcase.
 *
 * <p>Run with {@code mvn spring-boot:run} from the {@code demo/} directory,
 * or execute the resulting JAR directly.  The UI is served at
 * {@code http://localhost:8080/}.</p>
 *
 * <p>Stylesheet load order (top = first injected into &lt;head&gt;):
 * <ol>
 *   <li>{@code Lumo.STYLESHEET} — Vaadin Lumo base theme: shadow-DOM resets and
 *       component internals. Must be first so every Vaadin component has its
 *       structural baseline before any token overrides are applied.</li>
 *   <li>{@code vaadin-shell-theme.css} — design tokens ({@code --brand-*},
 *       {@code --surface-*}, {@code --text-*}, {@code --zinc-*}, spacing, radius,
 *       shadows, transitions) <em>plus</em> Vaadin component wiring: bridges tokens
 *       → {@code --lumo-*}; styles vaadin-app-layout, vaadin-side-nav, vaadin-button,
 *       vaadin-grid, and form inputs.</li>
 *   <li>{@code brand-override-example.css} — demo app brand: overrides the default
 *       blue {@code --brand-*} palette with the Altezza Travel amber/gold colours
 *       and a dark navy sidebar. Loaded <strong>after</strong> vaadin-shell-theme.css
 *       so its {@code :root} values win. In a real application replace this with
 *       your own {@code my-brand.css}.</li>
 *   <li>{@code app-shell.css} — application shell utility classes:
 *       {@code .app-view}, {@code .app-card}, {@code .app-badge--*},
 *       {@code .app-page-header}, {@code .app-toolbar}, {@code .app-empty}, etc.</li>
 * </ol>
 * To apply the same theme in a new project, copy these annotations onto
 * your {@code Application implements AppShellConfigurator} class and supply
 * your own {@code my-brand.css} with the {@code --brand-*} overrides loaded after
 * {@code vaadin-shell-theme.css}.
 * </p>
 */
@SpringBootApplication(excludeName = {
        "com.holonplatform.spring.boot.DataSourceAutoConfiguration"
})
@EntityScan(basePackages = {
        "com.holonplatform.vaadin.flow.demo.data.entity",          // Product
        "com.holonplatform.vaadin.flow.chat",                       // ChatRoom, ChatMessage, …
        "com.holonplatform.vaadin.flow.customer.entity"             // Customer
})
// 1. Vaadin Lumo base — shadow-DOM resets + component internals (must be first)
// 2. Shell theme     — design tokens + Lumo bridge + app-layout / nav / button / grid / inputs
// 3. Brand override  — loaded AFTER shell-theme so its :root values WIN
// 4. App shell       — .app-view, .app-card, .app-badge--* utility classes
@Push   // Required for Collaboration Kit real-time sync (LiveChat, TypingIndicator, etc.)
@StyleSheet(Lumo.STYLESHEET)
//@StyleSheet(Aura.STYLESHEET)
@StyleSheet("context://vaadin-shell-theme.css")
//@StyleSheet("context://brand-amber-studio.css")
@StyleSheet("context://tailadmin-brand.css")
//@StyleSheet("context://brand-override-example.css")
//@StyleSheet("context://brand-teal-ocean.css")
// @StyleSheet("context://brand-gold-leaf.css")
//@StyleSheet("context://brand-cobalt.css")
//@StyleSheet("context://brand-violet-studio.css")
//@StyleSheet("context://brand-azure.css")
@StyleSheet("context://app-shell.css")
public class DemoApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

