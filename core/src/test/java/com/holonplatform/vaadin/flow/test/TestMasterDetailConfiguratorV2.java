package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator;
import com.vaadin.flow.component.html.Span;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestMasterDetailConfiguratorV2 {

    public static final class Item {
        private final String name;

        public Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final List<Item> ITEMS = List.of(
            new Item("Alpha"),
            new Item("Beta"));

    @Test
    void mobileVariant_exposesConfiguredComponents() {
        var bundle = Components.listing(Item.class)
                .autoCreateColumns(false)
                .columns("name")
                .fetch((query, searchText, sort) -> ITEMS.stream())
                .build();
        var masterHeader = new Header("Master");

        var configurator = Components.masterDetailMobile(Item.class);
        MasterDetailConfigurator.MasterNode<Item, ?> master = configurator.masterView();
        master.header(masterHeader);
        master.listingBundle(bundle);
        master.add();

        configurator
                .defaultView(master)
                .detail()
                .content().content(new Span("Detail body")).add()
                .footer().content(new Span("Detail footer")).add()
                .add()
                .build();

        assertNotNull(configurator.getComponent());
        assertSame(masterHeader, configurator.getMasterHeader().orElseThrow());
        assertTrue(configurator.getSeparatorComponent().isPresent());
        assertTrue(configurator.getDetailHeader().isEmpty());
        assertTrue(configurator.getDetailTabs().isEmpty());
        assertEquals(1, configurator.getDetailContentComponents().size());
        assertEquals(1, configurator.getDetailFooterComponents().size());
        assertSame(bundle, configurator.getListingBundle().orElseThrow());
        assertTrue(configurator.getMasterDiv().isPresent());
        assertTrue(configurator.getDetailDiv().isPresent());
    }

    @Test
    void mobileVariant_withoutOptionalSections_keepsOptionalGettersEmpty() {
        var configurator = Components.masterDetailMobile(Item.class);
        MasterDetailConfigurator.MasterNode<Item, ?> master = configurator.masterView();
        master
                .listingBundle()
                .columns("name")
                .fetch((query, searchText, sort) -> ITEMS.stream())
                .add()
                .add();

        configurator.build();

        assertTrue(configurator.getMasterHeader().isEmpty());
        assertTrue(configurator.getSeparatorComponent().isEmpty());
        assertTrue(configurator.getDetailHeader().isEmpty());
        assertTrue(configurator.getDetailTabs().isEmpty());
        assertEquals(1, configurator.getDetailContentComponents().size());
        assertTrue(configurator.getDetailFooterComponents().isEmpty());
        assertTrue(configurator.getListingBundle().isPresent());
        assertTrue(configurator.getMasterDiv().isPresent());
        assertTrue(configurator.getDetailDiv().isPresent());
    }
}







