/*
 * Copyright 2016-2018 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal.converters;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 * {@link Instant} to {@link LocalDateTime} converter for UI presentation.
 * <p>
 * Converts between Instant (UTC, suitable for storage/transmission) and 
 * LocalDateTime (for UI input components) using a specified timezone.
 * </p>
 * <p>
 * Typical usage in global SaaS applications:
 * <ul>
 *   <li>Model: Instant (UTC) - stored in database/API</li>
 *   <li>Presentation: LocalDateTime in user's timezone</li>
 *   <li>Conversion: Applied automatically during binding</li>
 * </ul>
 * </p>
 *
 * @since 5.2.0
 */
public class InstantToLocalDateTimeConverter implements Converter<LocalDateTime, Instant> {

	@Serial
	private static final long serialVersionUID = 8774267222144386869L;

	private final ZoneId zoneId;

	/**
	 * Create a new converter using the user's browser/system timezone.
	 * <p>
	 * Note: For consistent timezone handling across users, prefer passing an explicit
	 * timezone (e.g., user's configured timezone from preferences).
	 * </p>
	 */
	public InstantToLocalDateTimeConverter() {
		this(ZoneId.systemDefault());
	}

	/**
	 * Create a new converter with specified timezone.
	 * <p>
	 * Use the user's configured timezone to ensure consistent display across
	 * different client locations. Example:
	 * <pre>
	 * ZoneId userZone = ZoneId.of("America/New_York");
	 * return new InstantToLocalDateTimeConverter(userZone);
	 * </pre>
	 * 
	 * @param zoneId The timezone to use for display (not null). 
	 *               Use user's configured timezone for SaaS applications
	 */
	public InstantToLocalDateTimeConverter(ZoneId zoneId) {
		this.zoneId = zoneId != null ? zoneId : ZoneId.systemDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.converter.Converter#convertToModel(java.lang.Object,
	 * com.vaadin.flow.data.binder.ValueContext)
	 */
	@Override
	public Result<Instant> convertToModel(LocalDateTime value, ValueContext context) {
		if (value != null) {
			return Result.ok(value.atZone(zoneId).toInstant());
		}
		return Result.ok(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.converter.Converter#convertToPresentation(java.lang.Object,
	 * com.vaadin.flow.data.binder.ValueContext)
	 */
	@Override
	public LocalDateTime convertToPresentation(Instant value, ValueContext context) {
		if (value != null) {
			return LocalDateTime.ofInstant(value, zoneId);
		}
		return null;
	}

}
