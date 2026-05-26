package com.holonplatform.vaadin.flow.demo;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Vaadin {@link I18NProvider} for the demo application.
 *
 * <p>Loads translations from {@code messages*.properties} files on the classpath.
 * The bundle covers all {@link com.holonplatform.vaadin.flow.components.kanban.KanbanI18n}
 * message codes (e.g. {@code kanban.column.add-card}) and any other keys the demo uses.</p>
 *
 * <p>Vaadin picks up any Spring {@link I18NProvider} bean automatically via
 * {@code VaadinService}. {@link com.holonplatform.vaadin.flow.i18n.LocalizationProvider}
 * checks for this bean first — so all Holon components that call
 * {@code LocalizationProvider.localize()} will use these translations.</p>
 *
 * <p>Supported locales: {@link Locale#ENGLISH} (default) and {@link Locale#GERMAN}.</p>
 */
@Component
public class DemoI18NProvider implements I18NProvider {

    private static final String BUNDLE_BASE = "messages";

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(Locale.ENGLISH, Locale.GERMAN);
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            return "";
        }
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE, locale);
            final String pattern = bundle.getString(key);
            return (params != null && params.length > 0)
                    ? MessageFormat.format(pattern, params)
                    : pattern;
        } catch (MissingResourceException e) {
            // Return the key itself as fallback so missing translations are visible during development
            return key;
        }
    }
}

