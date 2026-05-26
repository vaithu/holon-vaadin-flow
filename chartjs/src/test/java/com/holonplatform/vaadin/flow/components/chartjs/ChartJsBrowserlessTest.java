package com.holonplatform.vaadin.flow.components.chartjs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.NpmPackage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Server-side (browserless) tests for {@link ChartJsComponent}.
 *
 * <p>All assertions exercise the <em>Java / Element API</em> layer only.
 * Vaadin's {@code Element} stores properties in memory; no Vaadin session,
 * UI, or browser is required to read or write them. This mirrors the
 * Vaadin "browserless testing" pattern described in the Vaadin 25.1 docs,
 * implemented here without the optional {@code browserless-test-junit6}
 * artifact (which lives in the Vaadin Pro repository).
 *
 * <p>Client-side concerns – Chart.js rendering, CDN loading, canvas paint,
 * shadow-DOM content – require a real browser and belong in Playwright /
 * TestBench end-to-end tests.
 */
class ChartJsBrowserlessTest {

	// ─────────────────────────────────────────────────────────────────────────
	// Helpers – build chart variants used across multiple tests
	// ─────────────────────────────────────────────────────────────────────────

	private static ChartJsComponent bar() {
		return ChartJs.builder()
			.type(ChartType.BAR)
			.xAxis(a -> a.categories("Jan", "Feb", "Mar", "Apr"))
			.addSeries("Revenue", 10, 20, 30, 40)
			.addSeries("Cost", 5, 8, 12, 15)
			.chartTitle("Monthly Revenue vs Cost")
			.chartSubTitle("FY 2026")
			.legendEnabled(true)
			.tooltipShared(true)
			.width("600px").height("400px")
			.build();
	}

	private static ChartJsComponent line() {
		return ChartJs.builder()
			.type(ChartType.LINE)
			.xAxis(a -> a.categories("Q1", "Q2", "Q3", "Q4"))
			.addSeries("Sales", 100, 200, 150, 300)
			.chartTitle("Quarterly Sales Trend")
			.width("600px").height("400px")
			.build();
	}

	private static ChartJsComponent pie() {
		return ChartJs.builder()
			.type(ChartType.PIE)
			.xAxis(a -> a.categories("Alpha", "Beta", "Gamma"))
			.addSeries("Distribution", 40, 35, 25)
			.legendEnabled(true)
			.width("400px").height("400px")
			.build();
	}

	private static ChartJsComponent doughnut() {
		return ChartJs.builder()
			.type(ChartType.DOUGHNUT)
			.xAxis(a -> a.categories("Red", "Blue", "Green"))
			.addSeries("Segments", 10, 60, 30)
			.width("400px").height("400px")
			.build();
	}

	private static ChartJsComponent scatter() {
		return ChartJs.builder()
			.type(ChartType.SCATTER)
			.addSeries("Points", 5, 10, 15)
			.width("500px").height("350px")
			.build();
	}

	private static List<ChartJsComponent> allCharts() {
		return List.of(bar(), line(), pie(), doughnut(), scatter());
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Annotation-level contract
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void annotation_javascript_points_to_static_resource_bypassing_vite() {
		JavaScript js = ChartJsComponent.class.getAnnotation(JavaScript.class);
		assertNotNull(js, "@JavaScript must be present – serves holon-chartjs.js outside the Vite bundle");
		assertEquals(
			"context://chartjs/holon-chartjs.js", js.value(),
			"Path must resolve to META-INF/resources/chartjs/holon-chartjs.js in the JAR"
		);
	}

	@Test
	void annotation_no_npm_packages_present() {
		NpmPackage[] pkgs = ChartJsComponent.class.getAnnotationsByType(NpmPackage.class);
		assertEquals(0, pkgs.length,
			"CDN approach: no @NpmPackage expected – Chart.js loads from jsDelivr at runtime");
	}

	@Test
	void annotation_tag_matches_custom_elements_define_call() {
		Tag tag = ChartJsComponent.class.getAnnotation(Tag.class);
		assertNotNull(tag);
		assertEquals("holon-chartjs", tag.value(),
			"@Tag must exactly match customElements.define('holon-chartjs', …) in holon-chartjs.js");
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Element-tag contract
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void element_tag_is_holon_chartjs() {
		assertEquals("holon-chartjs", bar().getElement().getTag(),
			"<holon-chartjs> element tag must match @Tag so the browser upgrades it");
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Property propagation – bar chart (full detail)
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void bar_chart_type_property_is_set() {
		assertEquals("bar", bar().getElement().getProperty("chartType"));
	}

	@Test
	void bar_chart_data_json_contains_labels_and_series() {
		String dataJson = bar().getElement().getProperty("dataJson");
		assertNotNull(dataJson, "dataJson must be non-null");
		assertTrue(dataJson.contains("\"Jan\""),     "labels must include Jan");
		assertTrue(dataJson.contains("\"Revenue\""), "Revenue series must be present");
		assertTrue(dataJson.contains("\"Cost\""),    "Cost series must be present");
	}

	@Test
	void bar_chart_options_json_contains_title_subtitle_and_legend() {
		String optionsJson = bar().getElement().getProperty("optionsJson");
		assertNotNull(optionsJson, "optionsJson must be non-null when options are set");
		assertTrue(optionsJson.contains("\"Monthly Revenue vs Cost\""), "title text must be in options JSON");
		assertTrue(optionsJson.contains("\"FY 2026\""),                 "subtitle must be in options JSON");
		assertTrue(optionsJson.contains("\"display\":true"),            "legend.display must be true");
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Chart type property – one test per type
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void line_chart_produces_correct_chart_type_property() {
		assertEquals("line", line().getElement().getProperty("chartType"));
	}

	@Test
	void pie_chart_produces_correct_chart_type_property() {
		assertEquals("pie", pie().getElement().getProperty("chartType"));
	}

	@Test
	void doughnut_chart_produces_correct_chart_type_property() {
		assertEquals("doughnut", doughnut().getElement().getProperty("chartType"));
	}

	@Test
	void scatter_chart_produces_correct_chart_type_property() {
		assertEquals("scatter", scatter().getElement().getProperty("chartType"));
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Shadow-DOM isolation – no server-side light-DOM children
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void no_server_side_light_dom_children_on_any_chart() {
		// <canvas> and container <div> live inside the web component's shadow DOM
		// on the client side. The server-side element must have zero children.
		for (ChartJsComponent chart : allCharts()) {
			assertEquals(0, chart.getElement().getChildCount(),
				"chartType=" + chart.getElement().getProperty("chartType")
				+ " must have 0 server-side children – canvas belongs to shadow DOM");
		}
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Sizing – HasSize round-trip
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void set_width_and_height_round_trip_correctly() {
		// HasSize stores dimensions as element inline-style; no UI required
		ChartJsComponent c = bar();
		assertEquals("600px", c.getWidth(),  "bar chart width");
		assertEquals("400px", c.getHeight(), "bar chart height");

		ChartJsComponent p = pie();
		assertEquals("400px", p.getWidth(),  "pie chart width");
		assertEquals("400px", p.getHeight(), "pie chart height");

		ChartJsComponent s = scatter();
		assertEquals("500px", s.getWidth(),  "scatter chart width");
		assertEquals("350px", s.getHeight(), "scatter chart height");
	}

	@Test
	void set_width_after_build_updates_element_style() {
		ChartJsComponent c = bar();
		c.setWidth("800px");
		assertEquals("800px", c.getWidth());
	}

	// ─────────────────────────────────────────────────────────────────────────
	// setConfig – atomic replacement of all three properties
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void set_config_replaces_all_three_properties_atomically() {
		ChartJsComponent c = bar();
		assertEquals("bar", c.getElement().getProperty("chartType"));

		c.setConfig(ChartJsConfig.of(
			ChartType.LINE,
			ChartJsData.create()
				.categories("X1", "X2", "X3")
				.series(ChartJsDataset.create().name("Updated Series").values(7, 14, 21).build())
				.build(),
			ChartJsOptions.create().title("Updated Title").build()
		));

		assertEquals("line", c.getElement().getProperty("chartType"),
			"chartType must switch to 'line' after setConfig");

		String updatedData = c.getElement().getProperty("dataJson");
		assertTrue(updatedData.contains("\"X1\""),             "new labels must be present");
		assertTrue(updatedData.contains("\"Updated Series\""), "new series name must be present");

		String updatedOptions = c.getElement().getProperty("optionsJson");
		assertTrue(updatedOptions.contains("\"Updated Title\""), "new title must appear in options");
	}

	// ─────────────────────────────────────────────────────────────────────────
	// JSON validity across all chart types
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void data_json_is_valid_json_object_for_all_chart_types() {
		for (ChartJsComponent chart : allCharts()) {
			String dataJson = chart.getElement().getProperty("dataJson");
			assertNotNull(dataJson,
				"dataJson must not be null for chartType=" + chart.getElement().getProperty("chartType"));
			assertTrue(dataJson.startsWith("{"),         "dataJson must be a JSON object");
			assertTrue(dataJson.contains("\"datasets\""), "dataJson must contain datasets array");
		}
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Null / absent options
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void set_options_json_null_removes_property_from_element() {
		ChartJsComponent c = bar();
		assertNotNull(c.getElement().getProperty("optionsJson"), "pre-condition: options set");

		c.setOptionsJson(null);

		assertNull(c.getElement().getProperty("optionsJson"),
			"optionsJson must be absent (null) after setOptionsJson(null)");
	}

	@Test
	void builder_without_options_leaves_options_json_absent() {
		// No call to .options(), .chartTitle(), .legendEnabled() etc.
		ChartJsComponent plain = ChartJs.builder()
			.type(ChartType.BAR)
			.categories("A", "B")
			.addSeries("S", 1, 2)
			.build();

		assertNull(plain.getElement().getProperty("optionsJson"),
			"optionsJson must be absent when no options were provided to the builder");
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Component class contract
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void component_extends_vaadin_component() {
		assertInstanceOf(Component.class, new ChartJsComponent(),
			"ChartJsComponent must extend com.vaadin.flow.component.Component");
	}

	@Test
	void component_implements_has_size() {
		assertInstanceOf(HasSize.class, new ChartJsComponent(),
			"ChartJsComponent must implement HasSize for responsive layouts");
	}

	@Test
	void default_visibility_is_true() {
		// isVisible() reads the element's 'hidden' attribute; no UI required
		assertTrue(new ChartJsComponent().isVisible(),
			"A freshly created chart must be visible by default");
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Layout composition sanity
	// ─────────────────────────────────────────────────────────────────────────

	@Test
	void charts_can_be_composed_inside_a_layout() {
		// Constructing ChartJsTestView adds three charts to a VerticalLayout.
		// This verifies no exception is thrown and the component fields are populated.
		ChartJsTestView view = new ChartJsTestView();

		assertNotNull(view.barChart,  "barChart must be initialised");
		assertNotNull(view.lineChart, "lineChart must be initialised");
		assertNotNull(view.pieChart,  "pieChart must be initialised");

		assertEquals("bar",  view.barChart.getElement().getProperty("chartType"));
		assertEquals("line", view.lineChart.getElement().getProperty("chartType"));
		assertEquals("pie",  view.pieChart.getElement().getProperty("chartType"));

		// All three must be children of the layout
		assertEquals(3, view.getComponentCount(),
			"VerticalLayout must contain exactly 3 chart children");
	}
}


