package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.builders.DetailPanelConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Applies a flat detail-panel header to a {@link Panel}:
 * {@code <div class="panel-header"><h4 class="panel-title">Title details…</h4>actions…</div>}.
 *
 * @param <C> concrete configurator type
 */
public abstract class AbstractDetailPanelConfigurator<C extends DetailPanelConfigurator<C>>
        extends AbstractPanelConfigurator<C>
        implements DetailPanelConfigurator<C> {

    private final Div header = new Div();
    private final H4 title = new H4();
    private final List<Component> details = new ArrayList<>();
    private final List<Component> actions = new ArrayList<>();

    protected AbstractDetailPanelConfigurator(Panel panel, String title) {
        super(panel);
        this.title.add(new Text(Objects.requireNonNull(title, "Title must not be null")));
        this.title.addClassName("panel-title");
        header.add(this.title);
        panel.setHeader(header);
    }

    @Override
    public C details(Component... components) {
        replace(title, details, components);
        return getConfigurator();
    }

    @Override
    public C actions(Component... components) {
        replace(header, actions, components);
        return getConfigurator();
    }

    private static void replace(HasComponents parent, List<Component> current,
                                Component... components) {
        current.forEach(parent::remove);
        current.clear();
        if (components != null) {
            Stream.of(components).filter(Objects::nonNull).forEach(current::add);
        }
        current.forEach(parent::add);
    }
}
