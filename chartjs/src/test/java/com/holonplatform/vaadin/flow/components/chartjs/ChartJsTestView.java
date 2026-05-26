package com.holonplatform.vaadin.flow.components.chartjs;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Composition-sanity helper – verifies that multiple {@link ChartJsComponent} instances
 * can be added to a layout without any exception.
 * Used by {@link ChartJsBrowserlessTest#charts_can_be_composed_inside_a_layout()}.
 */
class ChartJsTestView extends VerticalLayout {

	final ChartJsComponent barChart;
	final ChartJsComponent lineChart;
	final ChartJsComponent pieChart;

	ChartJsTestView() {
		barChart = ChartJs.builder()
			.type(ChartType.BAR)
			.xAxis(a -> a.categories("Jan", "Feb", "Mar"))
			.addSeries("Revenue", 10, 20, 30)
			.chartTitle("Bar")
			.width("600px").height("400px")
			.build();

		lineChart = ChartJs.builder()
			.type(ChartType.LINE)
			.xAxis(a -> a.categories("Q1", "Q2"))
			.addSeries("Sales", 100, 200)
			.width("600px").height("400px")
			.build();

		pieChart = ChartJs.builder()
			.type(ChartType.PIE)
			.xAxis(a -> a.categories("A", "B", "C"))
			.addSeries("Share", 33, 34, 33)
			.width("400px").height("400px")
			.build();

		add(barChart, lineChart, pieChart);
	}
}
