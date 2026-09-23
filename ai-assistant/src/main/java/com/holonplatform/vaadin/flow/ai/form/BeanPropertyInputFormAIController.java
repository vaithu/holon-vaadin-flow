package com.holonplatform.vaadin.flow.ai.form;

import tools.jackson.databind.JsonNode;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.ai.datastore.PropertyValues;
import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.vaadin.flow.component.ai.orchestrator.AIController;
import com.vaadin.flow.component.ai.orchestrator.ResponseListener;
import com.vaadin.flow.component.ai.provider.LLMProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A Holon-Datastore-first, non-commercial equivalent of Vaadin's commercial {@code FormAIController}
 * for the Holon {@link BeanPropertyInputForm} component.
 *
 * <p>Where the commercial {@code FormAIController} lets the LLM map unstructured/attachment text
 * directly onto arbitrary form fields, this controller exposes a single, tightly-scoped tool
 * ({@code Form_fillFromText}) whose JSON-Schema parameters only allow setting bean properties you
 * explicitly whitelist. Values extracted by the LLM (e.g. from a pasted email, an uploaded receipt
 * or invoice attachment) are converted to the declared property type and written into a fresh bean
 * instance via {@link BeanPropertySet}, then applied with {@link BeanPropertyInputForm#setBean(Object)}
 * — the existing field {@code Validator}s and converters configured on the form still run exactly
 * as if the user had typed the values in by hand; nothing bypasses the form's own validation.
 *
 * <h3>Wiring</h3>
 * <pre>{@code
 * BeanPropertyInputForm<Invoice> form = BeanPropertyInputForm.formLayout(Invoice.class).build();
 *
 * BeanPropertyInputFormAIController<Invoice> formController = BeanPropertyInputFormAIController
 *     .attach(form, Invoice.class, "supplierName", "invoiceNumber", "amount", "dueDate");
 *
 * AIOrchestrator orchestrator = AIOrchestrator.builder(provider,
 *         "Extract invoice fields from the text or attachment the user provides, "
 *       + "then call Form_fillFromText to populate the form. Always let the user review "
 *       + "and confirm before saving.")
 *     .withMessageList(messageList)
 *     .withInput(messageInput)
 *     .withFileReceiver(upload) // enables document attachments (receipts, invoices, ...)
 *     .withController(formController)
 *     .build();
 * }</pre>
 *
 * <p><b>Guardrail:</b> this controller only ever populates the form fields — it never persists
 * data. Saving remains a distinct, explicit user action (e.g. a "Save" button bound to
 * {@link BeanPropertyInputForm#getBean()}), so a hallucinated or malicious extraction can never
 * write to the database without human review.
 *
 * @param <T> the bean type of the target {@link BeanPropertyInputForm}
 */
public final class BeanPropertyInputFormAIController<T> implements AIController {

    private final BeanPropertyInputForm<T> form;
    private final Class<T> beanType;
    private final BeanPropertySet<T> propertySet;
    private final List<String> fillableProperties;
    private final List<Consumer<List<FieldValueChange>>> fieldValueChangeListeners = new CopyOnWriteArrayList<>();

    private static final String AI_HIGHLIGHT_CLASS_NAME = "ai-filled";

    private BeanPropertyInputFormAIController(BeanPropertyInputForm<T> form, Class<T> beanType,
            List<String> fillableProperties) {
        this.form = Objects.requireNonNull(form, "form must not be null");
        this.beanType = Objects.requireNonNull(beanType, "beanType must not be null");
        this.propertySet = BeanPropertySet.create(beanType);
        this.fillableProperties = List.copyOf(fillableProperties);
        if (this.fillableProperties.isEmpty()) {
            throw new IllegalArgumentException("At least one fillable property must be whitelisted");
        }
    }

    /**
     * Creates and attaches a new {@link BeanPropertyInputFormAIController}.
     *
     * @param <T>                 bean type
     * @param form                the target form (not null)
     * @param beanType            the form's bean type (not null)
     * @param fillableProperties  the bean property names the LLM is allowed to populate — keep
     *                            this list limited to fields that are safe/expected to be
     *                            filled from unstructured user/document input
     * @return a new controller
     */
    public static <T> BeanPropertyInputFormAIController<T> attach(BeanPropertyInputForm<T> form, Class<T> beanType,
            String... fillableProperties) {
        return new BeanPropertyInputFormAIController<>(form, beanType, List.of(fillableProperties));
    }

    @Override
    public List<LLMProvider.ToolSpec> getTools() {
        return List.of(new LLMProvider.ToolSpec() {

            @Override
            public String getName() {
                return "Form_fillFromText";
            }

            @Override
            public String getDescription() {
                return "Fills the form from extracted field values. Only the following fields may be "
                        + "set: " + String.join(", ", fillableProperties) + ". "
                        + "Do not guess values that are not clearly present in the source text/attachment.";
            }

            @Override
            public String getParametersSchema() {
                String propertyEnum = fillableProperties.stream()
                        .map(p -> "\"" + p + "\"").collect(Collectors.joining(","));
                return """
                        { "type": "object",
                          "properties": {
                            "fields": { "type": "array", "items": { "type": "object",
                              "properties": {
                                "property": { "type": "string", "enum": [%s] },
                                "value": {}
                              }, "required": ["property","value"] } }
                          },
                          "required": ["fields"] }""".formatted(propertyEnum);
            }

            @Override
            public String execute(JsonNode arguments) {
                return fillForm(arguments);
            }
        });
    }

    @Override
    public void onResponse(ResponseListener.ResponseEvent event) {
        // No per-turn state retained.
    }

    private String fillForm(JsonNode arguments) {
        if (!arguments.hasNonNull("fields") || arguments.get("fields").isEmpty()) {
            throw new IllegalArgumentException("At least one field is required");
        }

        T previousBean = form.getBean();

        PropertyBox.Builder boxBuilder = PropertyBox.builder(propertySet);
        List<String> appliedProperties = new ArrayList<>();
        for (JsonNode field : arguments.get("fields")) {
            String name = field.get("property").asText();
            if (!fillableProperties.contains(name)) {
                throw new IllegalArgumentException("Property [" + name + "] is not allowed to be AI-filled");
            }
            Property<?> property = propertySet.property(name);
            @SuppressWarnings("unchecked")
            Property<Object> objectProperty = (Property<Object>) property;
            boxBuilder.set(objectProperty, PropertyValues.convert(property, field.get("value")));
            appliedProperties.add(name);
        }

        T filled;
        try {
            T instance = beanType.getDeclaredConstructor().newInstance();
            filled = propertySet.write(boxBuilder.build(), instance);
            form.setBean(filled);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Could not instantiate [" + beanType.getName() + "] — a public no-args constructor is required", e);
        }

        List<FieldValueChange> changes = new ArrayList<>();
        for (String name : appliedProperties) {
            Object oldValue = previousBean != null ? propertySet.read(name, previousBean) : null;
            Object newValue = propertySet.read(name, filled);
            if (!Objects.equals(oldValue, newValue)) {
                changes.add(new FieldValueChange(name, oldValue, newValue));
                showHighlight(name);
            }
        }
        if (!changes.isEmpty()) {
            fieldValueChangeListeners.forEach(listener -> listener.accept(changes));
        }

        return "Form pre-filled with " + appliedProperties.size() + " field(s). Please review before saving.";
    }

    // ------------------------------------------------------------------ //
    // Field highlighting & change notification — a dependency-free equivalent
    // of the commercial FormAIController's vaadin-field-highlighter-based
    // showHighlight()/hideHighlight()/addFieldValueChangedListener() API.
    // ------------------------------------------------------------------ //

    /**
     * A single AI-driven field change, reported to {@link #addFieldValueChangedListener(Consumer)}
     * listeners right after {@code Form_fillFromText} is applied.
     *
     * @param property the bean property name that changed
     * @param oldValue the value before the AI fill (may be {@code null})
     * @param newValue the value after the AI fill
     */
    public record FieldValueChange(String property, Object oldValue, Object newValue) {
    }

    /**
     * Registers a listener invoked with the list of fields the LLM actually changed, right after
     * each successful {@code Form_fillFromText} tool call — e.g. to log which fields were
     * AI-populated, or to drive a custom highlight/audit UI.
     *
     * @param listener the listener to register (not null)
     */
    public void addFieldValueChangedListener(Consumer<List<FieldValueChange>> listener) {
        fieldValueChangeListeners.add(Objects.requireNonNull(listener, "listener must not be null"));
    }

    /**
     * Adds a {@code "ai-filled"} CSS class name to the given property's input component, so it can
     * be visually flagged (e.g. with a themed border/background) as AI-populated. Automatically
     * invoked for every field changed by {@code Form_fillFromText}; call {@link #hideHighlight(String)}
     * once the user has reviewed/edited the field.
     *
     * @param property the bean property name whose input should be highlighted
     */
    public void showHighlight(String property) {
        form.getInput(objectProperty(property))
                .ifPresent(input -> input.getComponent().getElement().getClassList().add(AI_HIGHLIGHT_CLASS_NAME));
    }

    /**
     * Removes the {@code "ai-filled"} highlight previously applied by {@link #showHighlight(String)}.
     *
     * @param property the bean property name whose input should no longer be highlighted
     */
    public void hideHighlight(String property) {
        form.getInput(objectProperty(property))
                .ifPresent(input -> input.getComponent().getElement().getClassList().remove(AI_HIGHLIGHT_CLASS_NAME));
    }

    @SuppressWarnings("unchecked")
    private Property<Object> objectProperty(String name) {
        return (Property<Object>) propertySet.property(name);
    }

}
