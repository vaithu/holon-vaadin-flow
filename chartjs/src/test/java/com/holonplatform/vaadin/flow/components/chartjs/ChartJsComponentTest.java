package com.holonplatform.vaadin.flow.components.chartjs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChartJsComponentTest {

	@Test
	void shouldApplyBuilderConfigurationToElementProperties() {
		ChartJsData data = ChartJsData.create()
			.categories("A")
			.series(ChartJsDataset.create().name("Series 1").values(10).build())
			.build();
		ChartJsOptions options = ChartJsOptions.create().responsive(true)
			.legendEnabled(true)
			.tooltipShared(true)
			.title("Sample")
			.subtitle("Sub")
			.build();

		ChartJsComponent component = ChartJs.builder(ChartType.BAR, data)
			.options(options)
			.styleName("chart-root")
			.width("100%")
			.height("320px")
			.build();

		assertEquals("bar", component.getElement().getProperty("chartType"));
		assertEquals("{\"labels\":[\"A\"],\"datasets\":[{\"label\":\"Series 1\",\"data\":[10]}]}",
			component.getElement().getProperty("dataJson"));
		String optionsJson = component.getElement().getProperty("optionsJson");
		assertTrue(optionsJson.contains("\"responsive\":true"));
		assertTrue(optionsJson.contains("\"legend\":{\"display\":true}"));
		assertTrue(optionsJson.contains("\"title\":{\"display\":true,\"text\":\"Sample\"}"));
		assertTrue(optionsJson.contains("\"subtitle\":{\"display\":true,\"text\":\"Sub\"}"));
	}

	@Test
	void shouldRejectNullArguments() {
		assertThrows(IllegalArgumentException.class, () -> new ChartJsConfig(null, "{}", null));
		assertThrows(IllegalArgumentException.class, () -> new ChartJsConfig(ChartType.LINE, null, null));
		assertThrows(IllegalArgumentException.class, () -> ChartJs.builder().config(null));
		assertThrows(IllegalArgumentException.class, () -> ChartJs.builder().data((ChartJsData) null));
	}

	@Test
	void shouldProvideVaadinLikeOptionsDefaults() {
		String optionsJson = ChartJsOptions.create().build().toJson();

		assertEquals(
			"{\"responsive\":true,\"maintainAspectRatio\":false,\"plugins\":{\"legend\":{\"display\":true},\"tooltip\":{\"mode\":\"index\",\"intersect\":false}},\"interaction\":{\"mode\":\"index\",\"intersect\":false}}",
			optionsJson);
	}

	@Test
	void shouldAllowOverridingDefaultOptions() {
		String optionsJson = ChartJsOptions.create().responsive(false).nestedProperty("plugins.tooltip.intersect", true)
			.build().toJson();

		assertTrue(optionsJson.contains("\"responsive\":false"));
		assertTrue(optionsJson.contains("\"intersect\":true"));
	}

	@Test
	void shouldApplyShortcutOptions() {
		String optionsJson = ChartJsOptions.create()
			.legendEnabled(false)
			.tooltipShared(false)
			.xAxisTitle("Month")
			.yAxisTitle("Revenue")
			.title("Revenue")
			.subtitle("FY")
			.build()
			.toJson();

		assertTrue(optionsJson.contains("\"legend\":{\"display\":false}"));
		assertTrue(optionsJson.contains("\"tooltip\":{\"mode\":\"nearest\",\"intersect\":true}"));
		assertTrue(optionsJson.contains("\"interaction\":{\"mode\":\"nearest\",\"intersect\":true}"));
		assertTrue(optionsJson.contains("\"scales\":{\"x\":{\"title\":{\"display\":true,\"text\":\"Month\"}},\"y\":{\"title\":{\"display\":true,\"text\":\"Revenue\"}}}"));
		assertTrue(optionsJson.contains("\"title\":{\"display\":true,\"text\":\"Revenue\"}"));
		assertTrue(optionsJson.contains("\"subtitle\":{\"display\":true,\"text\":\"FY\"}"));
	}

	@Test
	void shouldSupportSingleChainFluentBuilder() {
		ChartJsComponent component = ChartJs.builder()
			.type(ChartType.BAR)
			.xAxis(axis -> axis.categories("Jan", "Feb").title("Month"))
			.yAxis(axis -> axis.title("Revenue"))
			.addSeries("Revenue", 12, 19)
			.chartTitle("Quarterly Revenue")
			.chartSubTitle("FY 2026")
			.legendEnabled(true)
			.tooltipShared(true)
			.build();

		assertEquals("bar", component.getElement().getProperty("chartType"));
		assertTrue(component.getElement().getProperty("dataJson").contains("\"labels\":[\"Jan\",\"Feb\"]"));
		assertTrue(component.getElement().getProperty("dataJson").contains("\"label\":\"Revenue\""));
		String optionsJson = component.getElement().getProperty("optionsJson");
		assertTrue(optionsJson.contains("\"title\":{\"display\":true,\"text\":\"Quarterly Revenue\"}"));
		assertTrue(optionsJson.contains("\"subtitle\":{\"display\":true,\"text\":\"FY 2026\"}"));
	}

	@Test
	void shouldNotHaveServerSideLightDomChildrenByDefault() {
		ChartJsComponent component = ChartJs.builder()
			.type(ChartType.BAR)
			.categories("Jan")
			.addSeries("Revenue", 12)
			.build();

		// Canvas and container are created in browser-side shadow DOM, not as server-side child nodes.
		assertEquals(0, component.getElement().getChildCount());
	}

	@Test
	void shouldCarryJavaScriptAnnotationOnComponent() {
		// ChartJsComponent serves the web-component JS as a plain static resource
		// (context://chartjs/holon-chartjs.js) so it works in every consuming project
		// without requiring a Vite frontend-bundle rebuild.
		com.vaadin.flow.component.dependency.JavaScript jsAnnotation =
			ChartJsComponent.class.getAnnotation(com.vaadin.flow.component.dependency.JavaScript.class);
		assertNotNull(jsAnnotation, "@JavaScript annotation must be present on ChartJsComponent");
		assertEquals("context://chartjs/holon-chartjs.js", jsAnnotation.value());

		// No @NpmPackage – Chart.js is loaded from CDN inside the web-component JS
		com.vaadin.flow.component.dependency.NpmPackage[] npmPackages =
			ChartJsComponent.class.getAnnotationsByType(com.vaadin.flow.component.dependency.NpmPackage.class);
		assertEquals(0, npmPackages.length, "No @NpmPackage annotations expected (CDN approach)");
	}

}









