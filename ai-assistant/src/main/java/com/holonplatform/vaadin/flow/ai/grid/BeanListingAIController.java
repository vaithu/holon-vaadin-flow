package com.holonplatform.vaadin.flow.ai.grid;

import tools.jackson.databind.JsonNode;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.flow.ai.datastore.PropertyValues;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.vaadin.flow.component.ai.orchestrator.AIController;
import com.vaadin.flow.component.ai.provider.LLMProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A Holon-Datastore-first, non-commercial equivalent of Vaadin's commercial {@code GridAIController}
 * for the Holon {@link BeanListing} component.
 *
 * <p>Where the commercial {@code GridAIController} hands the LLM the database schema and lets it
 * write arbitrary SQL, this controller instead exposes a single, tightly-scoped tool
 * ({@code Grid_query}) whose JSON-Schema parameters only allow filtering/sorting by the bean
 * properties you explicitly whitelist. The LLM never sees SQL or table names — only the bean
 * property names you register — and the resulting {@link QueryFilter} / {@link QuerySort} are
 * applied through the listing's existing Holon {@code Datastore}-backed data provider, so all
 * existing query-time authorization/scoping already configured on that data provider still
 * applies.
 *
 * <h3>Wiring</h3>
 * <pre>{@code
 * BeanListingAIController<Customer> gridController =
 *         BeanListingAIController.attach(customerListing, Customer.class, "name", "email", "tier");
 *
 * AIOrchestrator orchestrator = AIOrchestrator.builder(provider,
 *         "You can filter and sort the customers grid using the Grid_query tool.")
 *     .withMessageList(messageList)
 *     .withInput(messageInput)
 *     .withController(gridController)
 *     .build();
 * }</pre>
 *
 * <p>The {@code customerListing} must have been built with a
 * {@code .withQueryConfigurationProvider(gridController)} call on its
 * {@code BeanListingBuilder} (this controller <em>is</em> a {@link QueryConfigurationProvider}),
 * so that the filter/sort produced from the LLM's tool calls is applied on every subsequent
 * lazy-loading query:
 * <pre>{@code
 * BeanListing<Customer> customerListing = BeanListing.builder(Customer.class)
 *     .dataSource(datastore, CUSTOMER_TARGET, filterConverter)
 *     .withQueryConfigurationProvider(gridController)
 *     .build();
 * }</pre>
 *
 * @param <T> the bean/item type of the target {@link BeanListing}
 */
public final class BeanListingAIController<T> implements AIController, QueryConfigurationProvider {

    private final BeanListing<T> listing;
    private final BeanPropertySet<T> propertySet;
    private final List<String> allowedProperties;

    private final AtomicReference<QueryFilter> currentFilter = new AtomicReference<>();
    private final AtomicReference<QuerySort> currentSort = new AtomicReference<>();
    private final List<Consumer<State>> stateChangeListeners = new CopyOnWriteArrayList<>();

    private BeanListingAIController(BeanListing<T> listing, Class<T> beanType, List<String> allowedProperties) {
        this.listing = Objects.requireNonNull(listing, "listing must not be null");
        this.propertySet = BeanPropertySet.create(Objects.requireNonNull(beanType, "beanType must not be null"));
        this.allowedProperties = List.copyOf(allowedProperties);
        if (this.allowedProperties.isEmpty()) {
            throw new IllegalArgumentException("At least one queryable property must be whitelisted");
        }
    }

    /**
     * Creates and attaches a new {@link BeanListingAIController} for the given {@link BeanListing}.
     *
     * @param <T>               bean type
     * @param listing           the target listing (not null) — used only to trigger a refresh
     *                          after each tool call; the actual filter/sort is applied through
     *                          this controller acting as a {@link QueryConfigurationProvider}
     * @param beanType          the listing's bean type (not null)
     * @param allowedProperties the bean property names the LLM is allowed to filter/sort by —
     *                          keep this list minimal and free of sensitive columns
     * @return a new controller
     */
    public static <T> BeanListingAIController<T> attach(BeanListing<T> listing, Class<T> beanType,
            String... allowedProperties) {
        return new BeanListingAIController<>(listing, beanType, List.of(allowedProperties));
    }

    // ------------------------------------------------------------------ //
    // QueryConfigurationProvider — supplies the current LLM-driven filter/sort
    // ------------------------------------------------------------------ //

    @Override
    public QueryFilter getQueryFilter() {
        return currentFilter.get();
    }

    @Override
    public QuerySort getQuerySort() {
        return currentSort.get();
    }

    // ------------------------------------------------------------------ //
    // Session persistence — capture/restore the LLM-driven filter/sort across
    // sessions (analogous to the commercial GridAIController's GridState).
    // ------------------------------------------------------------------ //

    /**
     * A snapshot of the current LLM-driven filter/sort, suitable for persisting across sessions
     * (a database row, a file, a {@code VaadinSession} attribute, ...) and restoring later with
     * {@link #restoreState(State)}.
     *
     * @param filter the current query filter, or {@code null} if none is applied
     * @param sort   the current query sort, or {@code null} if none is applied
     */
    public record State(QueryFilter filter, QuerySort sort) {
    }

    /**
     * Returns the current filter/sort state.
     *
     * @return the current state
     */
    public State getState() {
        return new State(currentFilter.get(), currentSort.get());
    }

    /**
     * Restores a previously captured state — e.g. after a new session is created for a returning
     * user. Does <b>not</b> trigger {@link #addStateChangeListener(Consumer)} (mirroring the
     * commercial controller's behavior: listeners fire only when the LLM itself updates the
     * grid, not on restoration), but does refresh the grid's data provider.
     *
     * @param state the state to restore (not null)
     */
    public void restoreState(State state) {
        Objects.requireNonNull(state, "state must not be null");
        currentFilter.set(state.filter());
        currentSort.set(state.sort());
        listing.getGrid().getDataProvider().refreshAll();
    }

    /**
     * Registers a listener invoked every time the LLM successfully updates the grid's filter/sort
     * — typically used to persist the new {@link State} for the current session/user.
     *
     * @param listener the listener to register (not null)
     */
    public void addStateChangeListener(Consumer<State> listener) {
        stateChangeListeners.add(Objects.requireNonNull(listener, "listener must not be null"));
    }

    // ------------------------------------------------------------------ //
    // AIController — a single, tightly-scoped tool
    // ------------------------------------------------------------------ //

    @Override
    public List<LLMProvider.ToolSpec> getTools() {
        return List.of(new LLMProvider.ToolSpec() {

            @Override
            public String getName() {
                return "Grid_query";
            }

            @Override
            public String getDescription() {
                return "Filters and sorts the grid. Only the following properties may be used: "
                        + String.join(", ", allowedProperties) + ".";
            }

            @Override
            public String getParametersSchema() {
                String propertyEnum = allowedProperties.stream()
                        .map(p -> "\"" + p + "\"").collect(Collectors.joining(","));
                return """
                        { "type": "object",
                          "properties": {
                            "filters": { "type": "array", "items": { "type": "object",
                              "properties": {
                                "property": { "type": "string", "enum": [%s] },
                                "operator": { "type": "string",
                                  "enum": ["eq","neq","gt","gte","lt","lte","contains"] },
                                "value": {}
                              }, "required": ["property","operator","value"] } },
                            "sorts": { "type": "array", "items": { "type": "object",
                              "properties": {
                                "property": { "type": "string", "enum": [%s] },
                                "direction": { "type": "string", "enum": ["ASC","DESC"] }
                              }, "required": ["property","direction"] } }
                          } }""".formatted(propertyEnum, propertyEnum);
            }

            @Override
            public String execute(JsonNode arguments) {
                return applyQuery(arguments);
            }
        });
    }

    @Override
    public void onResponse(Throwable error) {
        // No per-turn state retained; the applied filter/sort persists until the next tool call.
    }

    private String applyQuery(JsonNode arguments) {
        List<QueryFilter> filters = new ArrayList<>();
        if (arguments.hasNonNull("filters")) {
            for (JsonNode f : arguments.get("filters")) {
                filters.add(toQueryFilter(f));
            }
        }
        currentFilter.set(filters.isEmpty() ? null : QueryFilter.allOf(filters).orElse(null));

        QuerySort sort = null;
        if (arguments.hasNonNull("sorts")) {
            for (JsonNode s : arguments.get("sorts")) {
                QuerySort next = toQuerySort(s);
                sort = (sort == null) ? next : sort.and(next);
            }
        }
        currentSort.set(sort);

        listing.getGrid().getDataProvider().refreshAll();

        State state = new State(currentFilter.get(), currentSort.get());
        stateChangeListeners.forEach(listener -> listener.accept(state));

        return "Grid updated with " + filters.size() + " filter(s)"
                + (sort != null ? " and sorting applied" : "") + ".";
    }

    private QueryFilter toQueryFilter(JsonNode node) {
        PathProperty<?> property = requireAllowedProperty(node.get("property").asText());
        String operator = node.get("operator").asText();
        Object value = PropertyValues.convert(property, node.get("value"));
        return buildFilter(property, operator, value);
    }

    @SuppressWarnings("unchecked")
    private <V> QueryFilter buildFilter(PathProperty<?> property, String operator, Object value) {
        PathProperty<V> typedProperty = (PathProperty<V>) property;
        V typedValue = (V) value;
        return switch (operator) {
            case "eq" -> QueryFilter.eq(typedProperty, typedValue);
            case "neq" -> QueryFilter.neq(typedProperty, typedValue);
            case "gt" -> QueryFilter.gt(typedProperty, typedValue);
            case "gte" -> QueryFilter.goe(typedProperty, typedValue);
            case "lt" -> QueryFilter.lt(typedProperty, typedValue);
            case "lte" -> QueryFilter.loe(typedProperty, typedValue);
            case "contains" -> QueryFilter.contains((PathProperty<String>) property, String.valueOf(value), true);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    private QuerySort toQuerySort(JsonNode node) {
        PathProperty<?> property = requireAllowedProperty(node.get("property").asText());
        SortDirection direction = "DESC".equalsIgnoreCase(node.get("direction").asText())
                ? SortDirection.DESCENDING
                : SortDirection.ASCENDING;
        return QuerySort.of(property, direction);
    }

    private PathProperty<?> requireAllowedProperty(String name) {
        if (!allowedProperties.contains(name)) {
            throw new IllegalArgumentException("Property [" + name + "] is not allowed for AI-driven queries");
        }
        return propertySet.property(name);
    }

}
