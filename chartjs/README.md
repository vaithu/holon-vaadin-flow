# holon-vaadin-flow-chartjs

Chart.js integration module for Vaadin Flow.

## What it provides

- `ChartJsComponent`: Flow component wrapping a custom `holon-chartjs` web component
- `ChartJsConfig`: immutable configuration record
- `ChartType`: supported Chart.js chart types
- Frontend bridge at `META-INF/resources/frontend/chartjs/holon-chartjs.js`

## Example

```java
@Route("chart")
public class ChartView extends VerticalLayout {

  public ChartView() {
var chart = ChartJs.builder()
    .type(ChartType.BAR)
    .xAxis(axis -> axis
        .categories("Jan", "Feb", "Mar")
        .title("Month"))
    .yAxis(axis -> axis.title("Revenue"))
    .addSeries("Revenue", 12, 19, 7)
    .chartTitle("Quarterly Revenue")
    .chartSubTitle("FY 2026")
    .responsive(true)
    .maintainAspectRatio(false)
    .legendEnabled(true)
    .tooltipShared(true)
    .fullWidth()
    .height("320px")
    .build();

    add(chart);
  }
}
```

> **Important**
>
> When using the `ChartJs.builder()` facade, frontend dependencies are discovered
> automatically by Vaadin scanning through the public chart API.
>
> If you instantiate `new ChartJsComponent(...)` directly (without using the
> facade), add `@Uses(ChartJsComponent.class)` on your route/layout to ensure the
> `holon-chartjs` frontend module is bundled.

## Programmatic data mapping utilities

Use `ChartJsSeriesMapper` to map backend collections into fluent builder calls.

```java
Map<String, Number> revenueByMonth = reportService.revenueByMonth(year);

var chart = ChartJsSeriesMapper.applySeries(
        ChartJs.builder().type(ChartType.LINE),
        "Revenue",
        revenueByMonth)
    .chartTitle("Revenue trend")
    .build();
```

```java
List<ChartJsSeriesMapper.SeriesPoint> rows = repository.fetchSeriesPoints(filters);

var matrix = ChartJsSeriesMapper.matrix(rows, ChartJsSeriesMapper.MissingCategoryStrategy.STRICT);

var chart = ChartJsSeriesMapper.applyMatrix(
        ChartJs.builder().type(ChartType.BAR),
        matrix)
    .xAxis(axis -> axis.title("Month"))
    .yAxis(axis -> axis.title("Amount"))
    .build();
```

## Defaults aligned with Vaadin Charts behavior

- If you do not set dataset colors, the component applies a Vaadin Charts-like palette automatically.
- Pie/Doughnut/Polar charts get per-point palette colors.
- Other chart types get one default series color.
- Options default to a Vaadin-like profile:
  - `responsive = true`
  - `maintainAspectRatio = false`
  - legend enabled
  - shared-tooltip style interaction (`mode = index`, `intersect = false`)

You can still override any default through the typed options builder.

## Vaadin Charts Migration Cheat-Sheet

Use this quick mapping when moving from Vaadin Charts APIs to this `chartjs` module.

| Vaadin Charts concept | `chartjs` typed API |
| --- | --- |
| `Configuration.setTitle("...")` | `ChartJsOptions.create().title("...")` |
| `Configuration.setSubTitle("...")` | `ChartJsOptions.create().subtitle("...")` |
| `XAxis.setCategories(...)` | `ChartJsData.create().categories(...)` |
| `DataSeriesItem[]` / series data | `ChartJsDataset.create().values(...)` |
| `DataSeries.setName("...")` | `ChartJsDataset.create().name("...")` |
| `DataSeries.setColor(...)` | `ChartJsDataset.create().color("...")` |
| `Legend.setEnabled(true/false)` | `ChartJsOptions.create().legendEnabled(...)` |
| `Tooltip.setShared(true/false)` | `ChartJsOptions.create().tooltipShared(...)` |
| Axis titles (`XAxis`/`YAxis`) | `xAxisTitle("...")` / `yAxisTitle("...")` |

### Notes

- Vaadin Charts is Highcharts-based; this module is Chart.js-based, so some advanced features are not 1:1.
- For unsupported typed options, use `nestedProperty("...")` as an escape hatch.
- If you do not set series colors, a Vaadin Charts-like default palette is applied automatically.

## Build

```bash
mvn -pl chartjs -am test
```











