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
package com.holonplatform.vaadin.flow.navigator.exceptions;

/**
 * Exception to notify invalid navigation parameter values.
 *
 * @since 5.2.0
 */
public class InvalidNavigationParameterException extends RuntimeException {

	private static final long serialVersionUID = -6645496038897867290L;

	/**
	 * Constructor.
	 */
	public InvalidNavigationParameterException() {
		super();
	}

	/**
	 * Constructor with error message.
	 * @param message Error message
	 */
	public InvalidNavigationParameterException(String message) {
		super(message);
	}

	/**
	 * Constructor with nested exception.
	 * @param cause Nested exception
	 */
	public InvalidNavigationParameterException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor with error message and nested exception.
	 * @param message Error message
	 * @param cause Nested exception
	 */
	public InvalidNavigationParameterException(String message, Throwable cause) {
		super(message, cause);
	}

}
