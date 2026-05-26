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
package com.holonplatform.vaadin.flow.test;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.internal.CurrentInstance;
import com.vaadin.flow.server.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public abstract class AbstractSessionTest {

	protected static final String TEST_SESSION_ID = "TestSessionID";
	protected static final int TEST_UIID = 1;

	protected VaadinService vaadinService;

	protected VaadinSession vaadinSession;

	protected UI ui;

	@BeforeEach
	public void _beforeEach() throws Exception {

		vaadinService = createVaadinService();
		vaadinSession = createVaadinSession(vaadinService, Locale.US);
		VaadinRequest request = buildVaadinRequest();
		CurrentInstance.set(VaadinSession.class, vaadinSession);
		CurrentInstance.set(VaadinRequest.class, request);

		ui = new UI();
		// Session must be set BEFORE doInit — UIInternals.addComponentDependencies
		// reads this.session during doInit (Vaadin 25 changed the call order).
		ui.getInternals().setSession(vaadinSession);
		ui.doInit(request, TEST_UIID, "TestSession");

		CurrentInstance.setCurrent(ui);
	}

	@AfterEach
	public void _afterEach() throws Exception {
		CurrentInstance.set(VaadinSession.class, null);
		CurrentInstance.set(VaadinRequest.class, null);
		CurrentInstance.set(UI.class, null);
	}

	protected VaadinService createVaadinService() throws Exception {
		VaadinServletService vaadinService = mock(VaadinServletService.class);

		// Mock com.vaadin.flow.function.DeploymentConfiguration (the interface used by
		// VaadinSession.getConfiguration() and VaadinService.getDeploymentConfiguration()).
		// Avoids DefaultDeploymentConfiguration constructor calling FeatureFlags.get().
		DeploymentConfiguration deployConfig = mock(DeploymentConfiguration.class);
		when(deployConfig.isProductionMode()).thenReturn(true);
		when(deployConfig.getHeartbeatInterval()).thenReturn(300);
		when(deployConfig.isCloseIdleSessions()).thenReturn(false);
		when(deployConfig.getMaxMessageSuspendTimeout()).thenReturn(5000);
		when(vaadinService.getDeploymentConfiguration()).thenReturn(deployConfig);

		when(vaadinService.getMainDivId(any(VaadinSession.class), any(VaadinRequest.class)))
				.thenReturn("test-main-div-id");
		when(vaadinService.getInstantiator()).thenReturn(new DefaultInstantiator(vaadinService));
		return vaadinService;
	}

	protected VaadinSession createVaadinSession(VaadinService service, Locale locale) throws Exception {
		WrappedSession wrappedSession = mock(WrappedSession.class);
		VaadinSession session = mock(VaadinSession.class);
		when(session.getState()).thenReturn(VaadinSessionState.OPEN);
		when(session.getSession()).thenReturn(wrappedSession);
		when(session.getService()).thenReturn(service);
		when(session.getSession().getId()).thenReturn(TEST_SESSION_ID);
		when(session.hasLock()).thenReturn(true);
		when(session.getLocale()).thenReturn(locale != null ? locale : Locale.US);

		// Use a real HashMap for session attributes so that:
		//   a) Vaadin internals that store/read attributes work correctly, and
		//   b) Holon's DefaultVaadinSessionScope.get() receives null for unregistered
		//      resources (e.g. BeanIntrospector), causing it to return Optional.empty()
		//      and fall through to the application scope where BeanIntrospector lives.
		// Returning a blanket "test-attribute" String would cause TypeMismatchException
		// whenever Holon looks up any typed resource from the session scope.
		Map<String, Object> sessionAttributes = new ConcurrentHashMap<>();
		doAnswer(inv -> sessionAttributes.get((String) inv.getArgument(0)))
				.when(session).getAttribute(any(String.class));
		doAnswer(inv -> {
			String key = inv.getArgument(0);
			Object val = inv.getArgument(1);
			if (val == null) sessionAttributes.remove(key);
			else sessionAttributes.put(key, val);
			return null;
		}).when(session).setAttribute(any(String.class), any());

		// UIInternals.triggerChunkLoading calls session.getConfiguration().isProductionMode().
		// Pre-resolve outside the when() chain to avoid nested mock interaction.
		DeploymentConfiguration sessionConfig = service.getDeploymentConfiguration();
		when(session.getConfiguration()).thenReturn(sessionConfig);
		return session;
	}

	/**
	 * Build VaadinServletRequest
	 * @return VaadinServletRequest
	 */
	protected VaadinServletRequest buildVaadinRequest() {
		return new VaadinServletRequest(buildHttpServletRequest(), (VaadinServletService) vaadinSession.getService());
	}

	/**
	 * Mocks a HttpServletRequest
	 * @return a HttpServletRequest
	 */
	protected HttpServletRequest buildHttpServletRequest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getProtocol()).thenReturn("http");
		when(request.getScheme()).thenReturn("http");
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(request.getRemoteHost()).thenReturn("localhost");
		when(request.getRemotePort()).thenReturn(80);
		when(request.getRequestURL()).thenReturn(new StringBuffer("http").append("://").append("localhost"));
		when(request.getRequestURI()).thenReturn("/");
		when(request.getLocalName()).thenReturn("localhost");
		when(request.getLocalPort()).thenReturn(80);
		when(request.isSecure()).thenReturn(false);
		when(request.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
		when(request.getCharacterEncoding()).thenReturn("utf-8");
		when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
		return request;
	}

	protected Properties getDeploymentProperties() {
		Properties properties = new Properties();
		properties.put(InitParameters.SERVLET_PARAMETER_PRODUCTION_MODE, "true");
		return properties;
	}

}
