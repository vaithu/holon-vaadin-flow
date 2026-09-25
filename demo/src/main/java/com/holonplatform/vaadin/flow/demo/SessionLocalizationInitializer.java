package com.holonplatform.vaadin.flow.demo;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.holonplatform.core.i18n.Localization;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.i18n.MessageProvider;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;

/**
 * Binds a {@link LocalizationContext} to the current Vaadin session and localizes it using the session
 * {@link Locale} <em>and</em> the time zone reported by the user's browser.
 *
 * <p>
 * This is the piece which makes date/time rendering correct in a multi-tenant (SaaS) deployment: values stored as
 * {@link java.time.Instant} are absolute points in time, so they must be rendered in the <em>viewer's</em> zone, not in
 * the server zone. A user in India sees {@code 17:30 IST} while a user in New York sees {@code 08:00 EDT} for the very
 * same instant, DST transitions included (the zone is an IANA zone id, not a fixed offset).
 * </p>
 *
 * <p>
 * The context is stored as a Vaadin session attribute, so the Holon
 * {@link com.holonplatform.vaadin.flow.VaadinSessionScope} context scope publishes it as the <em>current</em>
 * {@code LocalizationContext} and all Holon components pick it up automatically.
 * </p>
 */
@Component
public class SessionLocalizationInitializer implements VaadinServiceInitListener {

	private static final long serialVersionUID = 1L;

	/** Session attribute holding the resolved browser time zone. */
	private static final String ZONE_ATTRIBUTE = SessionLocalizationInitializer.class.getName() + ".zone";

	/** Same resource bundle used by {@link DemoI18NProvider}. */
	private static final String BUNDLE_BASE = "messages";

	@Override
	public void serviceInit(ServiceInitEvent event) {
		final VaadinService service = event.getSource();

		service.addSessionInitListener(e -> localize(e.getSession(), e.getSession().getLocale(), null));
		service.addUIInitListener(e -> initBrowserTimeZone(e.getUI()));
	}

	/**
	 * Resolve the browser time zone for the given UI and apply it to the session {@link LocalizationContext}.
	 * @param ui The UI being initialized
	 */
	private static void initBrowserTimeZone(UI ui) {
		final VaadinSession session = ui.getSession();
		final ZoneId known = (session != null) ? (ZoneId) session.getAttribute(ZONE_ATTRIBUTE) : null;
		if (known != null) {
			// already resolved for this session: apply it before the view is rendered
			localize(session, ui.getLocale(), known);
			return;
		}
		// getExtendedClientDetails() always returns a (possibly not yet populated) instance: a screen width of
		// -1 means the browser has not reported the real values yet (retrieveExtendedClientDetails is deprecated
		// in favor of this cached getter plus an explicit refresh(...) when the values are still missing)
		final ExtendedClientDetails details = ui.getPage().getExtendedClientDetails();
		if (details.getScreenWidth() != -1) {
			// already fetched (e.g. by another listener for this same UI): reuse them synchronously and avoid
			// the extra client round trip / route refresh
			final ZoneId zone = resolveZone(details);
			if (session != null) {
				session.setAttribute(ZONE_ATTRIBUTE, zone);
			}
			localize(session, ui.getLocale(), zone);
			return;
		}
		// the browser zone requires a client round trip: the first view is rendered before it is available
		details.refresh(refreshed -> {
			final ZoneId zone = resolveZone(refreshed);
			final VaadinSession current = ui.getSession();
			if (current != null) {
				current.setAttribute(ZONE_ATTRIBUTE, zone);
				localize(current, ui.getLocale(), zone);
			}
			// re-render the already displayed route so temporal values use the browser zone
			ui.refreshCurrentRoute(true);
		});
	}

	/**
	 * Get (creating it if required) the session {@link LocalizationContext} and localize it.
	 * @param session The Vaadin session
	 * @param locale The locale to use
	 * @param zone The time zone to use, may be <code>null</code>
	 */
	private static void localize(VaadinSession session, Locale locale, ZoneId zone) {
		if (session == null) {
			return;
		}
		LocalizationContext localizationContext = session.getAttribute(LocalizationContext.class);
		if (localizationContext == null) {
			localizationContext = LocalizationContext.builder()
					.withMessageProvider(MessageProvider.fromProperties(BUNDLE_BASE).build()).build();
			session.setAttribute(LocalizationContext.class, localizationContext);
		}
		final Localization.Builder localization = Localization
				.builder((locale != null) ? locale : Locale.getDefault());
		if (zone != null) {
            //this needs to updated from holon core - srini 09/21/2026
			localization.zone(zone);
		}
		localizationContext.localize(localization.build());
	}

	/**
	 * Convert the client details into a {@link ZoneId}, preferring the IANA zone id (which is DST aware) over the raw
	 * offset.
	 * @param details The extended client details
	 * @return The resolved zone, never <code>null</code>
	 */
	private static ZoneId resolveZone(ExtendedClientDetails details) {
		final String zoneId = details.getTimeZoneId();
		if (zoneId != null && !zoneId.trim().isEmpty()) {
			try {
				return ZoneId.of(zoneId.trim());
			} catch (@SuppressWarnings("unused") DateTimeException e) {
				// unknown zone id: fall back to the raw offset below
			}
		}
		return ZoneOffset.ofTotalSeconds(details.getTimezoneOffset() / 1000);
	}

}
