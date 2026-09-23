package com.holonplatform.vaadin.flow.ai.datastore;

import com.vaadin.flow.component.ai.orchestrator.AIController;
import com.vaadin.flow.component.ai.orchestrator.ResponseListener;
import com.vaadin.flow.component.ai.provider.LLMProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A Holon-Datastore-backed {@link AIController} implementation.
 *
 * <p>Instead of the commercial {@code GridAIController} / {@code ChartAIController} /
 * {@code FormAIController} (which require a {@code vaadin-ai-extensions-flow} subscription and
 * are banned in this stack), this controller exposes a fixed, well-described set of
 * {@link AITool}s whose {@code execute} bodies delegate to a Holon {@code Datastore} /
 * {@code BeanDatastoreHelper} / {@code *Service}. This bounds what the LLM can do to exactly the
 * operations you register, which is preferable to open-ended SQL whenever the use case allows it.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * DatastoreAIController controller = DatastoreAIController.builder()
 *     .withTool(new CustomerFindByTierTool(customerService))
 *     .withTool(new CustomerCountTool(customerService))
 *     .build();
 *
 * AIOrchestrator orchestrator = AIOrchestrator.builder(provider, systemPrompt)
 *     .withMessageList(messageList)
 *     .withInput(messageInput)
 *     .withController(controller)
 *     .build();
 * }</pre>
 *
 * <p><b>One controller per orchestrator.</b> To combine tools from several feature areas,
 * register them all on a single {@link DatastoreAIController} instance rather than trying to
 * attach more than one controller to the same {@code AIOrchestrator}.
 */
public final class DatastoreAIController implements AIController {

    private static final Pattern TOOL_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,64}$");

    private final Map<String, AITool> tools;

    private DatastoreAIController(Map<String, AITool> tools) {
        this.tools = Collections.unmodifiableMap(tools);
    }

    @Override
    public List<LLMProvider.ToolSpec> getTools() {
        List<LLMProvider.ToolSpec> specs = new ArrayList<>(tools.size());
        tools.values().forEach(tool -> specs.add(toToolSpec(tool)));
        return specs;
    }

    @Override
    public void onResponse(ResponseListener.ResponseEvent event) {
        // No per-turn state is captured by this controller; nothing to release/apply here.
        // Subclass or wrap this controller if deferred UI updates are required.
    }

    private static LLMProvider.ToolSpec toToolSpec(AITool tool) {
        return new LLMProvider.ToolSpec() {
            @Override
            public String getName() {
                return tool.getName();
            }

            @Override
            public String getDescription() {
                return tool.getDescription();
            }

            @Override
            public String getParametersSchema() {
                return tool.getParametersSchema();
            }

            @Override
            public String execute(tools.jackson.databind.JsonNode arguments) {
                return tool.execute(arguments);
            }
        };
    }

    /**
     * Creates a new builder for a {@link DatastoreAIController}.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for {@link DatastoreAIController}.
     */
    public static final class Builder {

        private final Map<String, AITool> tools = new LinkedHashMap<>();

        private Builder() {
        }

        /**
         * Registers a tool. Tool names must be unique and match
         * {@code ^[a-zA-Z0-9_-]{1,64}$}.
         *
         * @param tool the tool to register (not null)
         * @return this builder
         * @throws IllegalArgumentException if the tool name is invalid or already registered
         */
        public Builder withTool(AITool tool) {
            Objects.requireNonNull(tool, "tool must not be null");
            final String name = tool.getName();
            if (name == null || !TOOL_NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalArgumentException(
                        "Invalid tool name [" + name + "]: must match " + TOOL_NAME_PATTERN.pattern());
            }
            if (tools.containsKey(name)) {
                throw new IllegalArgumentException("A tool named [" + name + "] is already registered");
            }
            tools.put(name, tool);
            return this;
        }

        /**
         * Builds the controller.
         *
         * @return a new {@link DatastoreAIController}
         * @throws IllegalStateException if no tool has been registered
         */
        public DatastoreAIController build() {
            if (tools.isEmpty()) {
                throw new IllegalStateException(
                        "At least one AITool is required. Call .withTool(...) before .build().");
            }
            return new DatastoreAIController(tools);
        }

    }

}
