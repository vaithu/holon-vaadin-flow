package com.holonplatform.vaadin.flow.demo.test;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.internal.CurrentInstance;
import com.vaadin.flow.server.InitParameters;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;
import com.vaadin.flow.server.WrappedSession;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractViewSessionTest {

    protected static final String TEST_SESSION_ID = "TestSessionID";
    protected static final int TEST_UIID = 1;

    protected VaadinService vaadinService;
    protected VaadinSession vaadinSession;
    protected UI ui;

    @BeforeEach
    public void setUpViewSession() throws Exception {
        vaadinService = createVaadinService();
        vaadinSession = createVaadinSession(vaadinService, Locale.US);
        VaadinRequest request = buildVaadinRequest();

        CurrentInstance.set(VaadinSession.class, vaadinSession);
        CurrentInstance.set(VaadinRequest.class, request);

        ui = new UI();
        ui.getInternals().setSession(vaadinSession);
        ui.doInit(request, TEST_UIID, "TestSession");
        CurrentInstance.setCurrent(ui);
    }

    @AfterEach
    public void tearDownViewSession() {
        CurrentInstance.set(VaadinSession.class, null);
        CurrentInstance.set(VaadinRequest.class, null);
        CurrentInstance.set(UI.class, null);
    }

    protected VaadinService createVaadinService() throws Exception {
        VaadinServletService service = Mockito.mock(VaadinServletService.class);

        DeploymentConfiguration deployConfig = Mockito.mock(DeploymentConfiguration.class);
        Mockito.when(deployConfig.isProductionMode()).thenReturn(true);
        Mockito.when(deployConfig.getHeartbeatInterval()).thenReturn(300);
        Mockito.when(deployConfig.isCloseIdleSessions()).thenReturn(false);
        Mockito.when(deployConfig.getMaxMessageSuspendTimeout()).thenReturn(5000);
        Mockito.when(service.getDeploymentConfiguration()).thenReturn(deployConfig);

        Mockito.when(service.getMainDivId(Mockito.any(VaadinSession.class), Mockito.any(VaadinRequest.class)))
                .thenReturn("test-main-div-id");
        Mockito.when(service.getInstantiator()).thenReturn(new DefaultInstantiator(service));
        return service;
    }

    protected VaadinSession createVaadinSession(VaadinService service, Locale locale) throws Exception {
        WrappedSession wrappedSession = Mockito.mock(WrappedSession.class);
        VaadinSession session = Mockito.mock(VaadinSession.class);
        Mockito.when(session.getState()).thenReturn(VaadinSessionState.OPEN);
        Mockito.when(session.getSession()).thenReturn(wrappedSession);
        Mockito.when(session.getService()).thenReturn(service);
        Mockito.when(session.getSession().getId()).thenReturn(TEST_SESSION_ID);
        Mockito.when(session.hasLock()).thenReturn(true);
        Mockito.when(session.getLocale()).thenReturn(locale != null ? locale : Locale.US);

        Map<String, Object> sessionAttributes = new ConcurrentHashMap<>();
        Mockito.doAnswer(inv -> sessionAttributes.get((String) inv.getArgument(0)))
            .when(session).getAttribute(Mockito.any(String.class));
        Mockito.doAnswer(inv -> {
            String key = inv.getArgument(0);
            Object val = inv.getArgument(1);
            if (val == null) {
                sessionAttributes.remove(key);
            } else {
                sessionAttributes.put(key, val);
            }
            return null;
        }).when(session).setAttribute(Mockito.any(String.class), Mockito.any());

        DeploymentConfiguration sessionConfig = service.getDeploymentConfiguration();
        Mockito.when(session.getConfiguration()).thenReturn(sessionConfig);
        return session;
    }

    protected VaadinServletRequest buildVaadinRequest() {
        return new VaadinServletRequest(buildHttpServletRequest(), (VaadinServletService) vaadinSession.getService());
    }

    protected HttpServletRequest buildHttpServletRequest() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getProtocol()).thenReturn("http");
        Mockito.when(request.getScheme()).thenReturn("http");
        Mockito.when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        Mockito.when(request.getRemoteHost()).thenReturn("localhost");
        Mockito.when(request.getRemotePort()).thenReturn(80);
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http").append("://").append("localhost"));
        Mockito.when(request.getRequestURI()).thenReturn("/");
        Mockito.when(request.getLocalName()).thenReturn("localhost");
        Mockito.when(request.getLocalPort()).thenReturn(80);
        Mockito.when(request.isSecure()).thenReturn(false);
        Mockito.when(request.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        Mockito.when(request.getCharacterEncoding()).thenReturn("utf-8");
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        return request;
    }

    protected Properties getDeploymentProperties() {
        Properties properties = new Properties();
        properties.put(InitParameters.SERVLET_PARAMETER_PRODUCTION_MODE, "true");
        return properties;
    }
}