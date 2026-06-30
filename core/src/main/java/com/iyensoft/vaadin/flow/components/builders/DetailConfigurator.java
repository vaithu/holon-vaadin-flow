package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.*;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.Component;

import java.util.Optional;
import java.util.function.Consumer;

public interface DetailConfigurator<C extends DetailConfigurator<C>> extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasSizeConfigurator<C> {
    
    HeaderBuilder<C> header();

    FooterBuilder<C> footer();

    C content(Component... components);

    /**
     * Registers an explicit sync handler that is called whenever a row is selected
     * in the master grid. Use this to update components (e.g., the detail header title)
     * that cannot implement {@link com.iyensoft.vaadin.flow.components.DetailSyncAware}
     * directly.
     *
     * <pre>{@code
     * detail()
     *     .header().heading("Select a record").add()
     *     .withDetailSync((Order o) -> detailHeader.setHeading(o.getOrderNumber()))
     *     .content(new OrderTabsPanel())
     *     .add();
     * }</pre>
     *
     * @param <T>     the item type (must match the master listing's item type)
     * @param handler called on every row selection with the selected item
     */
    <T> C withDetailSync(Consumer<T> handler);

    C header(Header header);

    C footer(Footer footer);

    Optional<Header> getDetailHeader();
    Optional<Footer> getDetailFooter();

    interface BaseDetailConfigurator extends DetailConfigurator<BaseDetailConfigurator> {

    }

    interface HeaderBuilder<B extends DetailConfigurator<B>> extends HeaderConfigurator<HeaderBuilder<B>> {
        B add();
    }

    interface FooterBuilder<D extends DetailConfigurator<D>> extends FooterConfigurator<FooterBuilder<D>> {
        D add();
    }
    
}
