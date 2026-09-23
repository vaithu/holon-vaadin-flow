package com.holonplatform.vaadin.flow.ai.chart;

import tools.jackson.databind.JsonNode;
import com.holonplatform.vaadin.flow.ai.chart.ChartQuerySpec.AggregateOperation;
import com.holonplatform.vaadin.flow.ai.chart.ChartQuerySpec.ChartDataResolver;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsComponent;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsData;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsDataset;
import com.holonplatform.vaadin.flow.components.chartjs.ChartType;
import com.vaadin.flow.component.ai.orchestrator.AIController;
import com.vaadin.flow.component.ai.orchestrator.ResponseListener;
import com.vaadin.flow.component.ai.provider.LLMProvider;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A Holon-Datastore-first, non-commercial equivalent of Vaadin's commercial {@code ChartAIController}
 * for the Holon {@code holon-vaadin-flow-chartjs} {@link ChartJsComponent}.
 *
 * <p>Where the commercial {@code ChartAIController} lets the LLM query the database directly to
 * build a chart, this controller instead exposes a single, tightly-scoped tool
 * ({@code Chart_render}) whose parameters only allow grouping/aggregating by bean properties you
 * explicitly whitelist and an aggregate operation from a fixed set ({@code COUNT}, {@code SUM},
 * {@code AVG}, {@code MIN}, {@code MAX}). The actual data access is performed by the
 * application-supplied {@link ChartDataResolver}, which is expected to delegate to a Holon
 * {@code Datastore} / {@code BeanDatastoreHelper} group-by aggregate query — never to raw SQL
 * handed to the LLM.
 *
 * <h3>Wiring</h3>
 * <pre>{@code
 * ChartJsAIController chartController = ChartJsAIController.builder(chartComponent)
 *     .withGroupableProperty("tier")
 *     .withGroupableProperty("region")
 *     .withAggregateProperty("amount")
 *     .withAllowedChartTypes(ChartType.BAR, ChartType.PIE, ChartType.LINE)
 *     .withDataResolver(spec -> customerService.aggregate(spec)) // Holon Datastore-backed
 *     .build();
 *
 * AIOrchestrator orchestrator = AIOrchestrator.builder(provider,
 *         "You can render charts of the customers data using the Chart_render tool.")
 *     .withMessageList(messageList)
 *     .withInput(messageInput)
 *     .withController(chartController)
 *     .build();
 * }</pre>
 */
public final class ChartJsAIController implements AIController {

    private final ChartJsComponent chart;
    private final List<String> groupableProperties;
    private final List<String> aggregateProperties;
    private final List<ChartType> allowedChartTypes;
    private final ChartDataResolver dataResolver;

    private ChartJsAIController(ChartJsComponent chart, List<String> groupableProperties,
            List<String> aggregateProperties, List<ChartType> allowedChartTypes, ChartDataResolver dataResolver) {
        this.chart = chart;
        this.groupableProperties = groupableProperties;
        this.aggregateProperties = aggregateProperties;
        this.allowedChartTypes = allowedChartTypes;
        this.dataResolver = dataResolver;
    }

    /**
     * Returns a new builder for a {@link ChartJsAIController}.
     *
     * @param chart the target chart component (not null)
     * @return a new builder
     */
    public static Builder builder(ChartJsComponent chart) {
        return new Builder(chart);
    }

    @Override
    public List<LLMProvider.ToolSpec> getTools() {
        return List.of(new LLMProvider.ToolSpec() {

            @Override
            public String getName() {
                return "Chart_render";
            }

            @Override
            public String getDescription() {
                return "Renders a chart grouped by one of: " + String.join(", ", groupableProperties)
                        + ", aggregating one of: " + String.join(", ", aggregateProperties)
                        + " (or a row count), using one of the allowed chart types.";
            }

            @Override
            public String getParametersSchema() {
                String groupByEnum = quotedEnum(groupableProperties);
                String aggregateEnum = quotedEnum(aggregateProperties);
                String chartTypeEnum = allowedChartTypes.stream()
                        .map(t -> "\"" + t.name() + "\"").collect(Collectors.joining(","));
                return """
                        { "type": "object",
                          "properties": {
                            "chartType": { "type": "string", "enum": [%s] },
                            "groupBy": { "type": "string", "enum": [%s] },
                            "operation": { "type": "string", "enum": ["COUNT","SUM","AVG","MIN","MAX"] },
                            "aggregateProperty": { "type": "string", "enum": [%s],
                              "description": "Required unless operation is COUNT" }
                          },
                          "required": ["chartType","groupBy","operation"] }""".formatted(
                        chartTypeEnum, groupByEnum, aggregateEnum);
            }

            @Override
            public String execute(JsonNode arguments) {
                return render(arguments);
            }
        });
    }

    @Override
    public void onResponse(ResponseListener.ResponseEvent event) {
        // No per-turn state retained.
    }

    private String render(JsonNode arguments) {
        ChartType chartType = requireAllowedChartType(arguments.get("chartType").asText());
        String groupBy = requireAllowed(groupableProperties, arguments.get("groupBy").asText(), "group-by");
        AggregateOperation operation = AggregateOperation.valueOf(arguments.get("operation").asText());

        String aggregateProperty = null;
        if (operation != AggregateOperation.COUNT) {
            if (!arguments.hasNonNull("aggregateProperty")) {
                throw new IllegalArgumentException("aggregateProperty is required unless operation is COUNT");
            }
            aggregateProperty = requireAllowed(
                    aggregateProperties, arguments.get("aggregateProperty").asText(), "aggregate");
        }

        ChartQuerySpec spec = new ChartQuerySpec(groupBy, aggregateProperty, operation);
        Map<String, Number> data = dataResolver.resolve(spec);

        ChartJsData.Builder dataBuilder = ChartJsData.builder().labels(data.keySet().toArray(new String[0]));
        ChartJsDataset dataset = ChartJsDataset.create()
                .label(operation.name() + (aggregateProperty != null ? " of " + aggregateProperty : ""))
                .data(data.values().toArray(new Number[0]))
                .build();
        dataBuilder.dataset(dataset);

        chart.setType(chartType);
        chart.setData(dataBuilder.build());
        chart.refresh();

        return "Chart rendered as " + chartType.name() + " with " + data.size() + " categories.";
    }

    private ChartType requireAllowedChartType(String name) {
        try {
            ChartType type = ChartType.valueOf(name);
            if (!allowedChartTypes.contains(type)) {
                throw new IllegalArgumentException("Chart type [" + name + "] is not allowed");
            }
            return type;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown or disallowed chart type: " + name, e);
        }
    }

    private String requireAllowed(List<String> allowed, String value, String kind) {
        if (!allowed.contains(value)) {
            throw new IllegalArgumentException("The " + kind + " property [" + value + "] is not allowed");
        }
        return value;
    }

    private static String quotedEnum(List<String> values) {
        return values.stream().map(v -> "\"" + v + "\"").collect(Collectors.joining(","));
    }

    /**
     * Fluent builder for {@link ChartJsAIController}.
     */
    public static final class Builder {

        private final ChartJsComponent chart;
        private final java.util.LinkedHashSet<String> groupableProperties = new java.util.LinkedHashSet<>();
        private final java.util.LinkedHashSet<String> aggregateProperties = new java.util.LinkedHashSet<>();
        private final java.util.LinkedHashSet<ChartType> allowedChartTypes = new java.util.LinkedHashSet<>();
        private ChartDataResolver dataResolver;

        private Builder(ChartJsComponent chart) {
            this.chart = Objects.requireNonNull(chart, "chart must not be null");
        }

        /** Whitelists a bean property the LLM may group by (chart categories/labels). */
        public Builder withGroupableProperty(String property) {
            groupableProperties.add(Objects.requireNonNull(property));
            return this;
        }

        /** Whitelists a bean property the LLM may aggregate (SUM/AVG/MIN/MAX). */
        public Builder withAggregateProperty(String property) {
            aggregateProperties.add(Objects.requireNonNull(property));
            return this;
        }

        /** Whitelists the chart types the LLM may request. */
        public Builder withAllowedChartTypes(ChartType... types) {
            allowedChartTypes.addAll(List.of(types));
            return this;
        }

        /**
         * Sets the resolver that executes a validated {@link ChartQuerySpec} — must delegate to a
         * Holon {@code Datastore} / {@code BeanDatastoreHelper}, never to raw JDBC/SQL handed to
         * the LLM.
         */
        public Builder withDataResolver(ChartDataResolver dataResolver) {
            this.dataResolver = Objects.requireNonNull(dataResolver);
            return this;
        }

        /** Builds the controller. */
        public ChartJsAIController build() {
            if (groupableProperties.isEmpty()) {
                throw new IllegalStateException("At least one groupable property is required");
            }
            if (allowedChartTypes.isEmpty()) {
                throw new IllegalStateException("At least one allowed chart type is required");
            }
            if (dataResolver == null) {
                throw new IllegalStateException("A ChartDataResolver is required");
            }
            return new ChartJsAIController(chart, List.copyOf(groupableProperties),
                    List.copyOf(aggregateProperties), List.copyOf(allowedChartTypes), dataResolver);
        }

    }

}
