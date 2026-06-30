package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultMasterDetailBuilder;

/**
 * Fluent builder for {@link MasterDetailLayout}.
 *
 * <p>Entry points:</p>
 * <ul>
 *   <li>{@link #create(Class)} — bean-typed builder; enables type-witness-free
 *       {@code selectionKey} and uncast {@code withDetailSync} lambdas</li>
 *   <li>{@link #create(PropertySet)} — property-set builder (item type {@link PropertyBox})</li>
 * </ul>
 *
 * <pre>{@code
 * MasterDetailLayout<Order> mdl = MasterDetailBuilder.create(Order.class)
 *     .master(m -> m
 *         .listing(l -> l
 *             .columns("id", "customer", "status")
 *             .fetch((q, t, s) -> service.fetch(q)))
 *         .selectionKey(Order::getId)
 *         .card())
 *     .detail(d -> d
 *         .header(h -> h.heading("Order Details"))
 *         .withDetailSync(o -> populate(o))
 *         .content(formPanel))
 *     .build();
 * }</pre>
 *
 * @param <T> item type of the master listing
 */
public interface MasterDetailBuilder<T>
        extends MasterDetailConfigurator<T, MasterDetailBuilder<T>>,
                ComponentBuilder<MasterDetailLayout<T>, MasterDetailBuilder<T>> {

    /**
     * Creates a typed builder. The supplied {@code beanType} enables
     * type-witness-free {@code selectionKey} and uncast {@code withDetailSync} lambdas.
     *
     * @param <T>      item type
     * @param beanType bean class (not null)
     * @return a new typed builder
     */
    static <T> MasterDetailBuilder<T> create(Class<T> beanType) {
        return new DefaultMasterDetailBuilder<>(beanType);
    }

    /**
     * Creates a typed builder backed by a Holon {@link PropertySet}.
     * The item type is fixed to {@link PropertyBox}.
     *
     * <pre>{@code
     * MasterDetailLayout<PropertyBox> mdl = MasterDetailBuilder.create(PRODUCT_SET)
     *     .master(m -> m
     *         .listing(l -> l
     *             .fetch((q, text, sort) -> datastore.query(TARGET)
     *                 .restrict(q.getLimit(), q.getOffset())
     *                 .stream(PRODUCT_SET)))
     *         .selectionKey(pb -> pb.getValue(ID)))
     *     .detail(d -> d
     *         .withDetailSync(pb -> populate(pb))
     *         .content(detailPanel))
     *     .build();
     * }</pre>
     *
     * @param propertySet the Holon property set that drives the listing (not null)
     * @return a new {@code MasterDetailBuilder<PropertyBox>}
     */
    static MasterDetailBuilder<PropertyBox> create(PropertySet<?> propertySet) {
        return new DefaultMasterDetailBuilder<>(propertySet);
    }

}
