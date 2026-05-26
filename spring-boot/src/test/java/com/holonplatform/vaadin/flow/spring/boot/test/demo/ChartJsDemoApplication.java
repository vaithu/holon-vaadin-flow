package com.holonplatform.vaadin.flow.spring.boot.test.demo;

import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Test-scope Spring Boot app to manually verify the ChartJs integration route.
 *
 * <p>Excludes:
 * <ul>
 *   <li>{@code com.holonplatform.spring.boot.DataSourceAutoConfiguration} — registered in
 *       holon-spring-boot spring.factories but the class lives in holon-datastore-jdbc which
 *       is not a dependency here.</li>
 *   <li>{@code org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration} — Spring
 *       Boot's own JDBC auto-config, triggered because HikariCP lands on the test-classpath via
 *       spring-boot-starter-web; there is no database in this demo.</li>
 * </ul>
 */
@SpringBootApplication(excludeName = {
		"com.holonplatform.spring.boot.DataSourceAutoConfiguration",
		"org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration"
})
public class ChartJsDemoApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ChartJsDemoApplication.class).run(args);
	}
}

