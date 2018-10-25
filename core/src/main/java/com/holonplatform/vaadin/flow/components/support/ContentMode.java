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
package com.holonplatform.vaadin.flow.components.support;

/**
 * Textual values representations.
 * <p>
 * Mainly intended as a replacement for the Vaadin 7/8 ContentMode class to ensure API backward compatibility.
 * </p>
 *
 * @since 5.2.0
 */
public enum ContentMode {

	/**
	 * Textual values are displayed as plain text.
	 */
	TEXT,

	/**
	 * Textual values are interpreted and displayed as HTML. Care should be taken when using this mode to avoid
	 * Cross-site Scripting (XSS) issues.
	 */
	HTML;

}