package com.holonplatform.vaadin.flow.ai.datastore;

import tools.jackson.databind.JsonNode;

/**
 * A single, named tool that a {@link DatastoreAIController} contributes to the LLM.
 *
 * <p>Implementations delegate their {@link #execute(JsonNode)} logic to a Holon
 * {@code Datastore} / {@code BeanDatastoreHelper} / {@code *Service} — never to raw JDBC — so
 * the AI assistant reuses exactly the same persistence layer as the rest of the application.
 *
 * <h3>Naming</h3>
 * <p>Tool names must match {@code ^[a-zA-Z0-9_-]{1,64}$}. Prefix names with the owning feature
 * (e.g. {@code "Customer_findByTier"}) to avoid collisions when multiple controllers are
 * combined. {@link DatastoreAIController} validates this at registration time.
 *
 * @see DatastoreAIController
 */
public interface AITool {

    /**
     * The tool name, as presented to the LLM. Must match {@code ^[a-zA-Z0-9_-]{1,64}$}.
     *
     * @return the tool name (not null)
     */
    String getName();

    /**
     * A short, precise description of what the tool does and when to use it. This text is sent
     * to the LLM and directly influences whether/how the tool is invoked.
     *
     * @return the tool description (not null)
     */
    String getDescription();

    /**
     * The JSON-Schema describing the tool's input parameters.
     *
     * @return a JSON-Schema object as a string (not null)
     */
    String getParametersSchema();

    /**
     * Executes the tool against the given arguments.
     *
     * <p>Implementations must validate/whitelist {@code arguments} before use and delegate the
     * actual data access to a Holon {@code Datastore}-backed service. The returned string is the
     * only information handed back to the LLM — never return raw, unfiltered persistent entities
     * or sensitive columns the current user is not authorized to see.
     *
     * @param arguments the tool call arguments, as supplied by the LLM (not null)
     * @return the tool result, as a plain string handed back to the LLM
     */
    String execute(JsonNode arguments);

}
