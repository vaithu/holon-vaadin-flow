package com.holonplatform.vaadin.flow.components.chartjs;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChartJsSeriesMapperTest {

	@Test
	void shouldMapCategoryValueMap() {
		Map<String, Number> revenueByMonth = new LinkedHashMap<>();
		revenueByMonth.put("Jan", 12);
		revenueByMonth.put("Feb", 19);

		assertArrayEquals(new String[] { "Jan", "Feb" }, ChartJsSeriesMapper.categories(revenueByMonth));
		assertArrayEquals(new Number[] { 12, 19 }, ChartJsSeriesMapper.values(revenueByMonth));
	}

	@Test
	void shouldBuildStrictMatrix() {
		List<ChartJsSeriesMapper.SeriesPoint> points = List.of(
			new ChartJsSeriesMapper.SeriesPoint("Jan", "Revenue", 12),
			new ChartJsSeriesMapper.SeriesPoint("Feb", "Revenue", 19),
			new ChartJsSeriesMapper.SeriesPoint("Jan", "Cost", 8),
			new ChartJsSeriesMapper.SeriesPoint("Feb", "Cost", 14));

		ChartJsSeriesMapper.ChartMatrix matrix = ChartJsSeriesMapper.matrix(points,
			ChartJsSeriesMapper.MissingCategoryStrategy.STRICT);

		assertArrayEquals(new String[] { "Jan", "Feb" }, matrix.categories());
		assertEquals(2, matrix.series().size());
		assertArrayEquals(new Number[] { 12, 19 }, matrix.series().get(0).values());
		assertArrayEquals(new Number[] { 8, 14 }, matrix.series().get(1).values());
	}

	@Test
	void shouldFailInStrictModeWhenCategoryMissing() {
		List<ChartJsSeriesMapper.SeriesPoint> points = List.of(
			new ChartJsSeriesMapper.SeriesPoint("Jan", "Revenue", 12),
			new ChartJsSeriesMapper.SeriesPoint("Feb", "Revenue", 19),
			new ChartJsSeriesMapper.SeriesPoint("Jan", "Cost", 8));

		assertThrows(IllegalArgumentException.class,
			() -> ChartJsSeriesMapper.matrix(points, ChartJsSeriesMapper.MissingCategoryStrategy.STRICT));
	}

	@Test
	void shouldFillMissingWithZero() {
		List<ChartJsSeriesMapper.SeriesPoint> points = List.of(
			new ChartJsSeriesMapper.SeriesPoint("Jan", "Revenue", 12),
			new ChartJsSeriesMapper.SeriesPoint("Feb", "Revenue", 19),
			new ChartJsSeriesMapper.SeriesPoint("Jan", "Cost", 8));

		ChartJsSeriesMapper.ChartMatrix matrix = ChartJsSeriesMapper.matrix(points,
			ChartJsSeriesMapper.MissingCategoryStrategy.FILL_ZERO);

		assertArrayEquals(new Number[] { 8, 0d }, matrix.series().get(1).values());
	}

}

