package com.holonplatform.vaadin.flow.ai.chart;

import java.util.Map;

/**
 * A validated, whitelisted description of an aggregate query to run in order to render an
 * AI-requested chart.
 *
 * <p>Instances are produced by {@link ChartJsAIController} only after {@code groupByProperty} and
 * {@code aggregateProperty} have been checked against the controller's whitelist — implementations
 * of {@link ChartDataResolver} can therefore trust these values are safe to use in a Holon
 * {@code Datastore} group-by/aggregate query.
 *
 * @param groupByProperty   the bean property to group by (categorical axis / labels)
 * @param aggregateProperty the bean property to aggregate, or {@code null} when
 *                          {@code operation} is {@code COUNT}
 * @param operation         the aggregate operation to apply
 */
public record ChartQuerySpec(String groupByProperty, String aggregateProperty, AggregateOperation operation) {

    /**
     * Supported aggregate operations.
     */
    public enum AggregateOperation {
        COUNT, SUM, AVG, MIN, MAX
    }

    /**
     * A resolver that executes a {@link ChartQuerySpec} — typically by delegating to a Holon
     * {@code Datastore} / {@code BeanDatastoreHelper} group-by aggregate query — and returns the
     * resulting {@code label -> aggregate value} pairs, in display order.
     */
    @FunctionalInterface
    public interface ChartDataResolver {

        /**
         * Executes the given query specification.
         *
         * @param spec the validated query specification (not null)
         * @return the resulting {@code label -> aggregate value} pairs, in display order
         */
        Map<String, Number> resolve(ChartQuerySpec spec);
    }

}
