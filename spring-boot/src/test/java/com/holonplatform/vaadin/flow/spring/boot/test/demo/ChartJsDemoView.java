package com.holonplatform.vaadin.flow.spring.boot.test.demo;

import java.io.Serial;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJs;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsComponent;
import com.holonplatform.vaadin.flow.components.chartjs.ChartType;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Manual verification view for ChartJs host element rendering and options propagation.
 */
//@Uses(ChartJsComponent.class)
@Route("chartjs-demo")
public class ChartJsDemoView extends VerticalLayout {

	@Serial
	private static final long serialVersionUID = 5634082114263453572L;

	public ChartJsDemoView() {
		setPadding(true);
		setSpacing(true);

		ChartJsComponent chart = ChartJs.builder()
				.type(ChartType.BAR)
				.title("This is title")
				.subtitle("This is subtitle")
				.categories("Jan", "Feb", "Mar")
				.addSeries("Revenue", 12, 19, 3)
				.width("600px")
				.height("600px")
				.build();

//		Div container = new Div(chart);
//		container.addClassName("chartjs-demo-container");
		add(chart);
	}
}
