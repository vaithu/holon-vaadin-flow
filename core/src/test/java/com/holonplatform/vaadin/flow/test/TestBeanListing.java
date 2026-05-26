/*
 * Copyright 2016-2018 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.Validator;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.beans.DataPath;
import com.holonplatform.core.beans.Identifier;
import com.holonplatform.core.beans.Version;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.beans.BeanDatastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QueryProjection;
import com.holonplatform.datastore.jdbc.JdbcDatastore;
import com.holonplatform.jdbc.BasicDataSource;
import com.holonplatform.jdbc.DatabasePlatform;
import com.holonplatform.vaadin.flow.components.*;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.holonplatform.vaadin.flow.internal.components.AbstractItemListing;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestBeanListing {

    public static class VersionedTestBeanTrigger implements org.h2.api.Trigger {

        @Override
        public void init(java.sql.Connection connection, String schemaName, String triggerName, String tableName,
                boolean before, int type) {
            // no-op
        }

        @Override
        public void fire(java.sql.Connection connection, Object[] oldRow, Object[] newRow) {
            if (oldRow == null) {
                if (newRow[2] == null) {
                    newRow[2] = 0L;
                }
                return;
            }
            newRow[2] = ((Number) oldRow[2]).longValue() + 1L;
        }

        @Override
        public void close() {
            // no-op
        }

        @Override
        public void remove() {
            // no-op
        }
    }

    @DataPath("test_version_grid")
    public static class VersionedTestBean {

        @Identifier
        @DataPath("id")
        private long id;

        @DataPath("name")
        private String name;

        @Version
        @DataPath("version")
        private Long version;

        public VersionedTestBean() {
            super();
        }

        public VersionedTestBean(long id, String name, Long version) {
            this.id = id;
            this.name = name;
            this.version = version;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getVersion() {
            return version;
        }

        public void setVersion(Long version) {
            this.version = version;
        }
    }

    @Getter
    @Setter
    public static class TestConstructorWithInstant {
        private Instant instant;
        public TestConstructorWithInstant(Instant instant) {
            this.instant = instant;
        }
    }

    /** Helper row bean used to project the instant_epoch column via BeanProjection. */
    @Getter
    @Setter
    public static class InstantEpochRow {
        @DataPath("instant_epoch")
        private Long instantEpoch;
        public InstantEpochRow() {}
    }

    public static class TestBean {

        private long id;
        private String name;

        public TestBean() {
            super();
        }

        public TestBean(long id, String name) {
            super();
            this.id = id;
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    private static final String ID = "id";
    private static final String NAME = "name";

    @Test
    public void testBuilders() {

        BeanListingBuilder<TestBean> builder = BeanListing.builder(TestBean.class);
        assertNotNull(builder);
        BeanListing<TestBean> listing = builder.build();
        assertNotNull(listing);

        builder = Components.listing.items(TestBean.class);
        assertNotNull(builder);
        listing = builder.build();
        assertNotNull(listing);

    }

  @Test
  public void testBeanWithConstructorAndInstantProperty() {
    final Instant now = Instant.parse("2026-04-06T12:30:00Z");
    final TestConstructorWithInstant item = new TestConstructorWithInstant(now);

    BeanListing<TestConstructorWithInstant> listing = BeanListing.builder(TestConstructorWithInstant.class)
        .items(item)
        .build();

    assertTrue(listing.getVisibleColumns().contains("instant"));

    List<TestConstructorWithInstant> items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(1, items.size());
    assertEquals(now, items.get(0).getInstant());
  }

  @Test
  public void testBeanWithConstructorAndInstantPropertyDatastoreDataSource() throws Exception {
    final DataTarget<?> TARGET = DataTarget.named("test_constructor_with_instant");
    final PathProperty<Long> EPOCH_MILLIS = PathProperty.create("instant_epoch", Long.class);

    final BasicDataSource dataSource = BasicDataSource.builder()
        .url("jdbc:h2:mem:test_constructor_with_instant;DB_CLOSE_DELAY=-1")
        .username("sa")
        .driverClassName(DatabasePlatform.H2.getDriverClassName())
        .build();

    final Instant expected = Instant.parse("2026-04-06T15:45:00Z");
    try (java.sql.Connection connection = dataSource.getConnection();
         java.sql.Statement statement = connection.createStatement()) {
      statement.execute("drop table if exists test_constructor_with_instant");
      statement.execute("create table test_constructor_with_instant (instant_epoch bigint)");
      statement.execute("insert into test_constructor_with_instant (instant_epoch) values (" + expected.toEpochMilli() + ")");
    }

    final Datastore datastore = JdbcDatastore.builder().dataSource(dataSource).traceEnabled(true).build();

    BeanListing<TestConstructorWithInstant> listing = BeanListing.builder(TestConstructorWithInstant.class)
        .dataSource(datastore, TARGET,
            propertyBox -> new TestConstructorWithInstant(Instant.ofEpochMilli(propertyBox.getValue(EPOCH_MILLIS))),
            List.of(EPOCH_MILLIS))
        .build();

    List<TestConstructorWithInstant> items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(1, items.size());
    assertEquals(expected, items.get(0).getInstant());
  }

  @Test
  public void testBeanWithConstructorAndInstantPropertyMultipleItems() {
    final Instant t1 = Instant.parse("2026-01-01T00:00:00Z");
    final Instant t2 = Instant.parse("2026-04-06T12:00:00Z");
    final Instant t3 = Instant.parse("2026-12-31T23:59:59Z");

    BeanListing<TestConstructorWithInstant> listing = BeanListing.builder(TestConstructorWithInstant.class)
        .items(new TestConstructorWithInstant(t1),
               new TestConstructorWithInstant(t2),
               new TestConstructorWithInstant(t3))
        .build();

    List<TestConstructorWithInstant> items = getDataProvider(listing)
        .fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(3, items.size());

    Set<Instant> instants = items.stream()
        .map(TestConstructorWithInstant::getInstant)
        .collect(Collectors.toSet());
    assertTrue(instants.contains(t1));
    assertTrue(instants.contains(t2));
    assertTrue(instants.contains(t3));
  }

  @Test
  public void testBeanWithConstructorAndInstantPropertyColumnVisibility() {
    // Default build: 'instant' column should be the only visible column
    BeanListing<TestConstructorWithInstant> listing = BeanListing.builder(TestConstructorWithInstant.class).build();
    List<String> visible = listing.getVisibleColumns();
    assertEquals(1, visible.size());
    assertTrue(visible.contains("instant"));

    // Hide column at runtime
    listing.setColumnVisible("instant", false);
    assertTrue(listing.getVisibleColumns().isEmpty());

    // Re-show column at runtime
    listing.setColumnVisible("instant", true);
    assertEquals(1, listing.getVisibleColumns().size());
    assertTrue(listing.getVisibleColumns().contains("instant"));

    // Builder-level hidden column
    BeanListing<TestConstructorWithInstant> hiddenListing = BeanListing.builder(TestConstructorWithInstant.class)
        .visible("instant", false)
        .build();
    assertFalse(hiddenListing.getVisibleColumns().contains("instant"));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testBeanWithConstructorAndInstantPropertyDatastoreWithEpochMillisRangeFilter() throws Exception {
    final DataTarget<?> TARGET = DataTarget.named("test_instant_epoch_range");
    final PathProperty<Long> EPOCH_MILLIS = PathProperty.create("instant_epoch", Long.class);
    final com.holonplatform.core.property.Property<FilterInput.Range<Long>> EPOCH_RANGE =
        (com.holonplatform.core.property.Property) PathProperty.create("epochRange", FilterInput.Range.class);

    final Instant t1 = Instant.parse("2026-01-01T00:00:00Z");
    final Instant t2 = Instant.parse("2026-04-06T12:00:00Z");
    final Instant t3 = Instant.parse("2026-12-31T23:59:59Z");

    final BasicDataSource dataSource = BasicDataSource.builder()
        .url("jdbc:h2:mem:test_instant_epoch_range;DB_CLOSE_DELAY=-1")
        .username("sa")
        .driverClassName(DatabasePlatform.H2.getDriverClassName())
        .build();

    try (java.sql.Connection connection = dataSource.getConnection();
         java.sql.Statement statement = connection.createStatement()) {
      statement.execute("drop table if exists test_instant_epoch_range");
      statement.execute("create table test_instant_epoch_range (instant_epoch bigint)");
      statement.execute("insert into test_instant_epoch_range (instant_epoch) values (" + t1.toEpochMilli() + ")");
      statement.execute("insert into test_instant_epoch_range (instant_epoch) values (" + t2.toEpochMilli() + ")");
      statement.execute("insert into test_instant_epoch_range (instant_epoch) values (" + t3.toEpochMilli() + ")");
    }

    final Datastore datastore = JdbcDatastore.builder().dataSource(dataSource).traceEnabled(true).build();

    final FilterInputForm<com.vaadin.flow.component.formlayout.FormLayout> filters = FilterInputForm.formLayout()
        .withFilter(EPOCH_RANGE, FilterInput.numberRange(EPOCH_MILLIS, Long.class))
        .build();

    final BeanListing<TestConstructorWithInstant> listing =
        BeanListing.builder(TestConstructorWithInstant.class).build();
    listing.setItems(filters, (query, filter) -> {
      var datastoreQuery = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
      if (filter != null) {
        datastoreQuery.filter(filter);
      }
      return datastoreQuery.stream(BeanProjection.of(InstantEpochRow.class))
          .map(row -> new TestConstructorWithInstant(Instant.ofEpochMilli(row.getInstantEpoch())));
    });
    com.holonplatform.core.Registration registration = listing.refreshOnFilterChange(filters);

    // No filter: all 3 rows returned
    List<TestConstructorWithInstant> all = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(3, all.size());

    // Filter: only t2 and t3 (from t2 epoch millis onward, no upper bound)
    filters.getFilterInput(EPOCH_RANGE)
        .orElseThrow(() -> new AssertionError("Epoch range filter not found"))
        .getInput()
        .setValue(new FilterInput.Range<>(t2.toEpochMilli(), null));

    List<TestConstructorWithInstant> filtered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(2, filtered.size());
    assertTrue(filtered.stream().anyMatch(i -> t2.equals(i.getInstant())));
    assertTrue(filtered.stream().anyMatch(i -> t3.equals(i.getInstant())));
    assertFalse(filtered.stream().anyMatch(i -> t1.equals(i.getInstant())));

    // Filter: only exact t2 match (same lower and upper bound)
    filters.getFilterInput(EPOCH_RANGE)
        .orElseThrow(() -> new AssertionError("Epoch range filter not found"))
        .getInput()
        .setValue(new FilterInput.Range<>(t2.toEpochMilli(), t2.toEpochMilli()));

    List<TestConstructorWithInstant> exact = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(1, exact.size());
    assertEquals(t2, exact.get(0).getInstant());

    registration.remove();
  }

    @Test
    public void testComponent() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).id("testid").build();
        assertNotNull(listing.getComponent());

        assertTrue(listing.getComponent().getId().isPresent());
        assertEquals("testid", listing.getComponent().getId().get());

        listing = BeanListing.builder(TestBean.class).build();
        assertTrue(listing.isVisible());

        listing = BeanListing.builder(TestBean.class).visible(true).build();
        assertTrue(listing.isVisible());

        listing = BeanListing.builder(TestBean.class).visible(false).build();
        assertFalse(listing.isVisible());

        listing = BeanListing.builder(TestBean.class).hidden().build();
        assertFalse(listing.isVisible());

        /*final AtomicBoolean attached = new AtomicBoolean(false);

        listing = BeanListing.builder(TestBean.class).withAttachListener(e -> {
            attached.set(true);
        }).build();

        ComponentUtil.onComponentAttach(listing.getComponent(), true);
        assertTrue(attached.get());*/

        final AtomicBoolean detached = new AtomicBoolean(false);

        listing = BeanListing.builder(TestBean.class).withDetachListener(e -> {
            detached.set(true);
        }).build();

        ComponentUtil.onComponentDetach(listing.getComponent());
        assertTrue(detached.get());
    }

    @Test
    public void testStyles() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).styleName("test").build();
        assertNotNull(listing);
        assertTrue(ComponentTestUtils.getClassNames(listing).contains("test"));

        listing = BeanListing.builder(TestBean.class).styleNames("test1", "test2").build();
        assertNotNull(listing);
        assertTrue(ComponentTestUtils.getClassNames(listing).contains("test1"));
        assertTrue(ComponentTestUtils.getClassNames(listing).contains("test2"));

        listing = BeanListing.builder(TestBean.class).withThemeVariants(GridVariant.LUMO_COMPACT).build();
        assertTrue(listing.getComponent() instanceof Grid);

        assertTrue(
                ((Grid<?>) listing.getComponent()).getThemeNames().contains(GridVariant.LUMO_COMPACT.getVariantName()));

    }

    @Test
    public void testSize() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).width("50em").build();
        assertEquals("50em", ComponentTestUtils.getWidth(listing));

        listing = BeanListing.builder(TestBean.class).width(50, Unit.EM).build();
        assertEquals("50em", ComponentTestUtils.getWidth(listing));

        listing = BeanListing.builder(TestBean.class).width(50.7f, Unit.EM).build();
        assertEquals("50.7em", ComponentTestUtils.getWidth(listing));

        listing = BeanListing.builder(TestBean.class).height("50em").build();
        assertEquals("50em", ComponentTestUtils.getHeight(listing));

        listing = BeanListing.builder(TestBean.class).height(50, Unit.EM).build();
        assertEquals("50em", ComponentTestUtils.getHeight(listing));

        listing = BeanListing.builder(TestBean.class).height(50.7f, Unit.EM).build();
        assertEquals("50.7em", ComponentTestUtils.getHeight(listing));

        listing = BeanListing.builder(TestBean.class).width("50%").height("100%").build();
        assertEquals("50%", ComponentTestUtils.getWidth(listing));
        assertEquals("100%", ComponentTestUtils.getHeight(listing));

        listing = BeanListing.builder(TestBean.class).widthUndefined().build();
        assertNull(ComponentTestUtils.getWidth(listing));

        listing = BeanListing.builder(TestBean.class).heightUndefined().build();
        assertNull(ComponentTestUtils.getHeight(listing));

        listing = BeanListing.builder(TestBean.class).sizeUndefined().build();
        assertNull(ComponentTestUtils.getWidth(listing));
        assertNull(ComponentTestUtils.getHeight(listing));

        listing = BeanListing.builder(TestBean.class).fullWidth().build();
        assertEquals("100%", ComponentTestUtils.getWidth(listing));

        listing = BeanListing.builder(TestBean.class).fullHeight().build();
        assertEquals("100%", ComponentTestUtils.getHeight(listing));

        listing = BeanListing.builder(TestBean.class).fullSize().build();
        assertEquals("100%", ComponentTestUtils.getWidth(listing));
        assertEquals("100%", ComponentTestUtils.getHeight(listing));

    }

    @Test
    public void testEnabled() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();
        assertTrue(ComponentTestUtils.isEnabled(listing));

        listing = BeanListing.builder(TestBean.class).enabled(true).build();
        assertTrue(ComponentTestUtils.isEnabled(listing));

        listing = BeanListing.builder(TestBean.class).enabled(false).build();
        assertFalse(ComponentTestUtils.isEnabled(listing));

        listing = BeanListing.builder(TestBean.class).disabled().build();
        assertFalse(ComponentTestUtils.isEnabled(listing));

    }

    @Test
    public void testFocus() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).tabIndex(77).build();
        assertTrue(listing.getComponent() instanceof Grid);

        assertEquals(77, ((Grid<?>) listing.getComponent()).getTabIndex());

    }

    @Test
    public void testConfiguration() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();
        assertTrue(listing.getComponent() instanceof Grid);
        assertFalse(((Grid<?>) listing.getComponent()).isColumnReorderingAllowed());
        assertFalse(((Grid<?>) listing.getComponent()).isAllRowsVisible());
        assertTrue(((Grid<?>) listing.getComponent()).isDetailsVisibleOnClick());
        assertFalse(((Grid<?>) listing.getComponent()).isMultiSort());


        listing = BeanListing.builder(TestBean.class).columnReorderingAllowed(true).build();
        assertTrue(((Grid<?>) listing.getComponent()).isColumnReorderingAllowed());

        listing = BeanListing.builder(TestBean.class).allRowsVisible(true).build();
        assertTrue(((Grid<?>) listing.getComponent()).isAllRowsVisible());

        listing = BeanListing.builder(TestBean.class).itemDetailsVisibleOnClick(false).build();
        assertFalse(((Grid<?>) listing.getComponent()).isDetailsVisibleOnClick());

        listing = BeanListing.builder(TestBean.class).multiSort(true).build();
        // assertTrue(((Grid<?>) listing.getComponent()).isMultiSort());
        assertNotNull(((Grid<?>) listing.getComponent()).getElement().getAttribute("multi-sort"));

        listing = BeanListing.builder(TestBean.class).pageSize(100).build();
        assertEquals(100, ((Grid<?>) listing.getComponent()).getPageSize());


    }

    @Test
    public void testColumnConfiguration() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();

        final ItemListingColumn<?, ?, ?> c = getImpl(listing).getColumnConfiguration(ID);
        assertNotNull(c.getColumnKey());
        assertEquals(ID, c.getColumnKey());
        assertFalse(c.isReadOnly());
        assertFalse(c.isFrozen());
        assertTrue(c.isVisible());
        assertEquals(SortMode.ENABLED, c.getSortMode());
        assertTrue(c.getSortProperties().size() > 0);

        listing = BeanListing.builder(TestBean.class).readOnly(ID, true).build();
        assertTrue(getImpl(listing).getColumnConfiguration(ID).isReadOnly());

        listing = BeanListing.builder(TestBean.class).visible(ID, false).build();
        assertFalse(getImpl(listing).getColumnConfiguration(ID).isVisible());

        listing = BeanListing.builder(TestBean.class).resizable(ID, true).build();
        assertTrue(getImpl(listing).getColumnConfiguration(ID).isResizable());

        listing = BeanListing.builder(TestBean.class).frozen(ID, true).build();
        assertTrue(getImpl(listing).getColumnConfiguration(ID).isFrozen());

        listing = BeanListing.builder(TestBean.class).width(ID, "50px").build();
        assertEquals("50px", getImpl(listing).getColumnConfiguration(ID).getWidth().orElse(null));

        listing = BeanListing.builder(TestBean.class).flexGrow(ID, 1).build();
        assertEquals(1, getImpl(listing).getColumnConfiguration(ID).getFlexGrow());

        listing = BeanListing.builder(TestBean.class).alignment(ID, ColumnAlignment.RIGHT)
                .alignment(NAME, ColumnAlignment.CENTER).build();
        assertEquals(ColumnAlignment.RIGHT, getImpl(listing).getColumnConfiguration(ID).getAlignment().orElse(null));
        assertEquals(ColumnAlignment.CENTER, getImpl(listing).getColumnConfiguration(NAME).getAlignment().orElse(null));

        listing = BeanListing.builder(TestBean.class).header(ID, "test").build();
        assertEquals("test", LocalizationContext
                .translate(getImpl(listing).getColumnConfiguration(ID).getHeaderText().orElse(null), true));

        listing = BeanListing.builder(TestBean.class).header(ID, "test", "mc").build();
        assertEquals("test", LocalizationContext
                .translate(getImpl(listing).getColumnConfiguration(ID).getHeaderText().orElse(null), true));

        listing = BeanListing.builder(TestBean.class).header(ID, Localizable.of("test")).build();
        assertEquals("test", LocalizationContext
                .translate(getImpl(listing).getColumnConfiguration(ID).getHeaderText().orElse(null), true));

        final Button btn = new Button("test");

        listing = BeanListing.builder(TestBean.class).headerComponent(ID, btn).build();
        assertEquals(btn, getImpl(listing).getColumnConfiguration(ID).getHeaderComponent().orElse(null));

        final Renderer<TestBean> rnd = LitRenderer.of("test");
        listing = BeanListing.builder(TestBean.class).renderer(ID, rnd).build();
        assertEquals(rnd, getImpl(listing).getColumnConfiguration(ID).getRenderer().orElse(null));

        final ValueProvider<TestBean, String> vp = item -> "test";
        listing = BeanListing.builder(TestBean.class).valueProvider(ID, vp).build();
        assertEquals(vp, getImpl(listing).getColumnConfiguration(ID).getValueProvider().orElse(null));

        final Comparator<TestBean> cmp = Comparator.<TestBean, Long>comparing(v -> v.getId(),
                Comparator.comparingLong(k -> k));
        listing = BeanListing.builder(TestBean.class).sortComparator(ID, cmp).build();
        assertEquals(cmp, getImpl(listing).getColumnConfiguration(ID).getComparator().orElse(null));

        listing = BeanListing.builder(TestBean.class).sortUsing(NAME, ID).build();
        assertEquals(ID, getImpl(listing).getColumnConfiguration(NAME).getSortProperties().get(0));

        final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

        final Input<Long> edt = Input.number(Long.class).build();
        listing = BeanListing.builder(TestBean.class).editor(ID, edt).build();
        assertEquals(edt, getImpl(listing).getColumnConfiguration(ID).getEditorInputRenderer()
                .map(r -> r.render(beanPropertySet.property(ID))).orElse(null));

        final Button ebtn = new Button("test");
        listing = BeanListing.builder(TestBean.class).editorComponent(ID, i -> ebtn).build();
        assertEquals(ebtn,
                getImpl(listing).getColumnConfiguration(ID).getEditorComponent().map(e -> e.apply(null)).orElse(null));
        listing = BeanListing.builder(TestBean.class).editorComponent(ID, ebtn).build();
        assertEquals(ebtn,
                getImpl(listing).getColumnConfiguration(ID).getEditorComponent().map(e -> e.apply(null)).orElse(null));

        final Validator<Long> vdt = Validator.max(3);
        listing = BeanListing.builder(TestBean.class).withValidator(ID, vdt).build();
        assertEquals(vdt, getImpl(listing).getColumnConfiguration(ID).getValidators().get(0));

    }

    @Test
    public void testColumns() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();
        assertTrue(listing.getComponent() instanceof Grid);

        final Grid<?> grid = (Grid<?>) listing.getComponent();
        assertEquals(2, grid.getColumns().size());

        assertNotNull(getImpl(listing).getColumnConfiguration(ID).getColumnKey());
        assertNotNull(getImpl(listing).getColumnConfiguration(NAME).getColumnKey());

        assertNotNull(grid.getColumnByKey(getImpl(listing).getColumnConfiguration(ID).getColumnKey()));
        assertNotNull(grid.getColumnByKey(getImpl(listing).getColumnConfiguration(NAME).getColumnKey()));
    }

    @Test
    public void testDefaultHeader() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();

        assertTrue(listing.getHeader().isPresent());
        assertEquals(1, listing.getHeader().get().getRows().size());
        assertTrue(listing.getHeader().get().getFirstRow().isPresent());

        assertTrue(listing.getComponent() instanceof Grid);
        final Grid<?> grid = (Grid<?>) listing.getComponent();
        assertEquals(2, grid.getColumns().size());

        Column<?> c1 = grid.getColumnByKey(getImpl(listing).getColumnConfiguration(ID).getColumnKey());
        Column<?> c2 = grid.getColumnByKey(getImpl(listing).getColumnConfiguration(NAME).getColumnKey());

        assertNotNull(c1);
        assertNotNull(c2);
    }

    @Test
    public void testHeader() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();

        assertTrue(listing.getHeader().isPresent());

        assertEquals(1, listing.getHeader().get().getRows().size());
        assertTrue(listing.getHeader().get().getFirstRow().isPresent());

        assertEquals(2, listing.getHeader().get().getFirstRow().get().getCells().size());
        assertTrue(listing.getHeader().get().getFirstRow().get().getCell(ID).isPresent());
        assertTrue(listing.getHeader().get().getFirstRow().get().getCell(NAME).isPresent());

        listing = BeanListing.builder(TestBean.class).header(header -> {
            header.prependRow().join(ID, NAME).setText("joined");
        }).build();

        assertEquals(2, listing.getHeader().get().getRows().size());
        assertEquals(1, listing.getHeader().get().getRows().get(0).getCells().size());
        assertEquals(2, listing.getHeader().get().getRows().get(1).getCells().size());

        LocalizationTestUtils.withTestLocalizationContext(() -> {
            BeanListing<TestBean> listing2 = BeanListing.builder(TestBean.class).header(header -> {
                header.prependRow().join(ID, NAME)
                        .setText(Localizable.builder().message("test").messageCode("test.code").build());
            }).build();
            assertEquals(2, listing2.getHeader().get().getRows().size());
        });
    }

    @Test
    public void testFooter() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).footer(footer -> {
            footer.appendRow().getCell(ID).get().setText("id");
        }).build();

        assertTrue(listing.getFooter().isPresent());
        assertEquals(1, listing.getFooter().get().getRows().size());

    }

    @Test
    public void testFooterPartName() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).footer(footer -> {
            footer.appendRow().getCell(ID).get().setPartName("red");
        }).build();

        assertTrue(listing.getFooter().isPresent());
        assertEquals("red", listing.getFooter().get().getFirstRow()
                .get().getCell(ID).get()
                .getPartName());

    }

    @Test
    public void testHeaderPartName() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).header(header -> {
            header.appendRow().getCell(ID).get().setPartName("red");
        }).build();

        assertTrue(listing.getHeader().isPresent());
        Optional<ItemListing.ItemListingRow<String>> found = Optional.empty();
        for (ItemListing.ItemListingRow<String> itemListingRow : listing.getHeader().get()
                .getRows()) {
            if (itemListingRow.getCell(ID).isPresent()) {
                found = Optional.of(itemListingRow);
                break;
            }
        }
        ItemListing.ItemListingRow<String> row = found.get();
        assertNotNull(row);
        assertNotNull(row.getCell(ID));
        ItemListing.ItemListingCell cell = row.getCell(ID).get();
        assertNotNull(cell);
        cell.setPartName("red");
        assertEquals("red", cell.getPartName());

    }

    @Test
    public void testItemsDataSource() {

        final TestBean ITEM1 = new TestBean(1L, "test1");
        final TestBean ITEM2 = new TestBean(2L, "test2");

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).items(Arrays.asList(ITEM1, ITEM2)).build();

        List<TestBean> items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, items.size());
        assertTrue(items.contains(ITEM1));
        assertTrue(items.contains(ITEM2));

        listing = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).build();

        items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, items.size());
        assertTrue(items.contains(ITEM1));
        assertTrue(items.contains(ITEM2));

        listing = BeanListing.builder(TestBean.class).addItem(ITEM1).addItem(ITEM2).build();

        items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, items.size());
        assertTrue(items.contains(ITEM1));
        assertTrue(items.contains(ITEM2));

        final List<TestBean> itemList = Arrays.asList(ITEM1, ITEM2);

        listing = BeanListing.builder(TestBean.class)
                .dataSource(DataProvider.fromCallbacks(q -> itemList.stream(), q -> 2)).build();
        items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, items.size());
        assertTrue(items.contains(ITEM1));
        assertTrue(items.contains(ITEM2));

    }

    @Test
    public void testBackendPagingWithQueryLimitOffset() {

        final List<TestBean> sourceItems = Arrays.asList(
                new TestBean(1L, "test1"),
                new TestBean(2L, "test2"),
                new TestBean(3L, "test3"));

        final AtomicInteger requestedLimit = new AtomicInteger(-1);
        final AtomicInteger requestedOffset = new AtomicInteger(-1);

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();
        listing.setItems(query -> {
            requestedLimit.set(query.getLimit());
            requestedOffset.set(query.getOffset());

            final int start = Math.min(query.getOffset(), sourceItems.size());
            final int end = Math.min(start + query.getLimit(), sourceItems.size());
            return sourceItems.subList(start, end).stream();
        });

        List<TestBean> firstPage = getDataProvider(listing)
                .fetch(new Query<>(0, 1, Collections.emptyList(), null, null))
                .collect(Collectors.toList());
        assertEquals(1, firstPage.size());
        assertEquals(1L, firstPage.get(0).getId());
        assertEquals(1, requestedLimit.get());
        assertEquals(0, requestedOffset.get());

        List<TestBean> secondPage = getDataProvider(listing)
                .fetch(new Query<>(1, 1, Collections.emptyList(), null, null))
                .collect(Collectors.toList());
        assertEquals(1, secondPage.size());
        assertEquals(2L, secondPage.get(0).getId());
        assertEquals(1, requestedLimit.get());
        assertEquals(1, requestedOffset.get());

        List<TestBean> tailPage = getDataProvider(listing)
                .fetch(new Query<>(2, 2, Collections.emptyList(), null, null))
                .collect(Collectors.toList());
        assertEquals(1, tailPage.size());
        assertEquals(3L, tailPage.get(0).getId());
        assertEquals(2, requestedLimit.get());
        assertEquals(2, requestedOffset.get());
    }

    @Test
    public void testDataSourceWithPagingFilteringAndSorting() {

        final DataTarget<?> TARGET = DataTarget.named("test2");

        final Datastore datastore = JdbcDatastore.builder()
                .dataSource(
                        BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
                                .username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
                .traceEnabled(true).build();

        final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class)
                .dataSource(datastore, TARGET)
                .withQueryFilter(beanPropertySet.property(ID).loe(2L))
                .withQuerySort(beanPropertySet.property(NAME).desc())
                .build();

        List<TestBean> firstPage = getDataProvider(listing)
                .fetch(new Query<>(0, 1, Collections.emptyList(), null, null))
                .collect(Collectors.toList());
        assertEquals(1, firstPage.size());
        assertEquals(2L, firstPage.get(0).getId());

        List<TestBean> secondPage = getDataProvider(listing)
                .fetch(new Query<>(1, 1, Collections.emptyList(), null, null))
                .collect(Collectors.toList());
        assertEquals(1, secondPage.size());
        assertEquals(1L, secondPage.get(0).getId());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDatastoreDataSource() {

        final DataTarget<?> TARGET = DataTarget.named("test2");

        final Datastore datastore = JdbcDatastore.builder()
                .dataSource(
                        BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
                                .username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
                .traceEnabled(true).build();

        final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET).build();

        List<TestBean> items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, items.size());
        assertTrue(items.stream().filter(i -> i.getId() == 1L).findFirst().isPresent());
        assertTrue(items.stream().filter(i -> i.getId() == 2L).findFirst().isPresent());

        listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET).build();

        items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, items.size());
        assertTrue(items.stream().filter(i -> i.getId() == 1L).findFirst().isPresent());
        assertTrue(items.stream().filter(i -> i.getId() == 2L).findFirst().isPresent());

        listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
                .withQueryFilter(beanPropertySet.property(ID).lt(2L)).build();

        items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(1, items.size());
        assertTrue(items.stream().filter(i -> i.getId() == 1L).findFirst().isPresent());
        assertFalse(items.stream().filter(i -> i.getId() == 2L).findFirst().isPresent());

        listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
                .withQueryConfigurationProvider(new QueryConfigurationProvider() {

                    @Override
                    public QueryFilter getQueryFilter() {
                        return beanPropertySet.property(ID).lt(2L);
                    }
                }).build();

        items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(1, items.size());
        assertTrue(items.stream().filter(i -> i.getId() == 1L).findFirst().isPresent());
        assertFalse(items.stream().filter(i -> i.getId() == 2L).findFirst().isPresent());

        listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
                .withQuerySort(beanPropertySet.property(NAME).desc()).build();
        items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, items.size());
        assertEquals(2L, items.get(0).getId());
        assertEquals(1L, items.get(1).getId());

        listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
                .withQueryFilter(beanPropertySet.property(ID).loe(2L))
                .withQuerySort(beanPropertySet.property(NAME).desc()).build();
        items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, items.size());
        assertEquals(2L, items.get(0).getId());
        assertEquals(1L, items.get(1).getId());

        listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
//                .itemCountEstimate(200) //this is not working due to
//                java.lang.IllegalStateException: GridLazyDataView only supports 'BackEndDataProvider' or it's subclasses
//                , but was given a 'AbstractDataProvider'.
//Use either 'getLazyDataView()', 'getListDataView()' or 'getGenericDataView()' according to the used data type.
                .withDefaultQuerySort(beanPropertySet.property(NAME).desc()).build();

        items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, items.size());
        assertEquals(2L, items.get(0).getId());
        assertEquals(1L, items.get(1).getId());

        listing.sort(ItemSort.asc(ID));
        List<QuerySortOrder> bs = getDataCommunicator(listing).getBackEndSorting();
        assertEquals(1, bs.size());
        assertEquals(ID, bs.get(0).getSorted());
        assertEquals(SortDirection.ASCENDING, bs.get(0).getDirection());

        listing.sort(Collections.singletonList(ItemSort.desc(ID)));
        bs = getDataCommunicator(listing).getBackEndSorting();
        assertEquals(1, bs.size());
        assertEquals(ID, bs.get(0).getSorted());
        assertEquals(SortDirection.DESCENDING, bs.get(0).getDirection());

    }

    @Test
    public void testRefresh() {

        final DataTarget<?> TARGET = DataTarget.named("test2");

        final Datastore datastore = JdbcDatastore.builder()
                .dataSource(
                        BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
                                .username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
                .traceEnabled(true).build();

        final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

        final AtomicInteger fired = new AtomicInteger(0);
        final ItemValue value = new ItemValue();

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
                .withItemRefreshListener(e -> {
                    fired.incrementAndGet();
                    value.item = e.getItem();
                }).build();

        assertNull(value.item);
        assertEquals(0, fired.get());

        listing.refresh();
        assertNull(value.item);
        assertEquals(1, fired.get());

        TestBean itm = datastore.query(TARGET).filter(beanPropertySet.property(ID).eq(1L))
                .findOne(QueryProjection.bean(TestBean.class))
                .orElseThrow(() -> new RuntimeException("item not found"));
        listing.refreshItem(itm);

        assertNotNull(value.item);
        assertEquals(itm, value.item);
        assertEquals(2, fired.get());

    }

    private class ItemValue {

        public TestBean item;

    }

    @Test
    public void testSelectable() {

        final TestBean ITEM1 = new TestBean(1L, "test1");
        final TestBean ITEM2 = new TestBean(2L, "test2");

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).build();

        assertEquals(SelectionMode.NONE, listing.getSelectionMode());
        assertEquals(0, listing.getSelectedItems().size());
        assertFalse(listing.getFirstSelectedItem().isPresent());
        assertFalse(listing.isSelected(ITEM1));
        assertFalse(listing.isSelected(ITEM2));

        assertThrows(IllegalStateException.class, () -> listing.select(ITEM1));

        BeanListing<TestBean> listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2)
                .selectionMode(SelectionMode.SINGLE).build();
        assertEquals(SelectionMode.SINGLE, listing2.getSelectionMode());
        assertEquals(0, listing2.getSelectedItems().size());
        assertFalse(listing2.getFirstSelectedItem().isPresent());

        listing2.select(ITEM2);
        assertEquals(1, listing2.getSelectedItems().size());
        assertTrue(listing2.getFirstSelectedItem().isPresent());
        assertEquals(ITEM2, listing2.getFirstSelectedItem().orElse(null));
        assertFalse(listing2.isSelected(ITEM1));
        assertTrue(listing2.isSelected(ITEM2));

        listing2.deselect(ITEM2);
        assertEquals(0, listing2.getSelectedItems().size());
        assertFalse(listing2.getFirstSelectedItem().isPresent());

        listing2.select(ITEM1);
        listing2.deselect(ITEM2);
        assertEquals(1, listing2.getSelectedItems().size());
        assertTrue(listing2.getFirstSelectedItem().isPresent());
        assertTrue(listing2.isSelected(ITEM1));

        listing2.deselectAll();
        assertEquals(0, listing2.getSelectedItems().size());
        assertFalse(listing2.getFirstSelectedItem().isPresent());

        listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).singleSelect().build();
        assertEquals(SelectionMode.SINGLE, listing2.getSelectionMode());
        listing2.select(ITEM2);
        assertEquals(1, listing2.getSelectedItems().size());

        listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).build();
        listing2.setSelectionMode(SelectionMode.SINGLE);
        assertEquals(SelectionMode.SINGLE, listing2.getSelectionMode());
        listing2.select(ITEM2);
        assertEquals(1, listing2.getSelectedItems().size());

        listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).multiSelect().build();
        assertEquals(SelectionMode.MULTI, listing2.getSelectionMode());
        assertEquals(0, listing.getSelectedItems().size());

        listing2.select(ITEM1);
        listing2.select(ITEM2);
        assertEquals(2, listing2.getSelectedItems().size());
        assertTrue(listing2.isSelected(ITEM1));
        assertTrue(listing2.isSelected(ITEM2));

        final Set<TestBean> selected = new HashSet<>();

        listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).singleSelect().withSelectionListener(e -> {
            selected.clear();
            selected.addAll(e.getAllSelectedItems());
        }).build();

        assertEquals(0, selected.size());

        listing2.select(ITEM1);

        assertEquals(1, selected.size());
        assertTrue(selected.contains(ITEM1));

        listing2.select(ITEM2);

        assertEquals(1, selected.size());
        assertTrue(selected.contains(ITEM2));

        listing2.deselectAll();
        assertEquals(0, selected.size());
        assertFalse(selected.contains(ITEM1));
        assertFalse(selected.contains(ITEM2));

        selected.clear();

        listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).singleSelect().multiSelect()
                .withSelectionListener(e -> {
                    selected.clear();
                    selected.addAll(e.getAllSelectedItems());
                }).build();

        assertEquals(0, selected.size());

        listing2.select(ITEM1);

        assertEquals(1, selected.size());
        assertTrue(selected.contains(ITEM1));

        listing2.select(ITEM2);

        assertEquals(2, selected.size());
        assertTrue(selected.contains(ITEM1));
        assertTrue(selected.contains(ITEM2));

        listing2.deselectAll();
        assertEquals(0, selected.size());
        assertFalse(selected.contains(ITEM1));
        assertFalse(selected.contains(ITEM2));

    }

    @Test
    public void testVisibleColumns() {

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).visibleColumns(NAME, ID).build();

        List<String> visible = listing.getVisibleColumns();
        assertEquals(2, visible.size());

        listing = BeanListing.builder(TestBean.class,false)
                .withComponentColumn(testBean -> new Button())
                .add().build();

        assertEquals(1,listing.getAllColumns().size());

        listing = BeanListing.builder(TestBean.class).build();
        assertEquals(2, listing.getVisibleColumns().size());

        listing = BeanListing.builder(TestBean.class).visibleColumns(NAME, ID).build();

        assertEquals(NAME, visible.get(0));
        assertEquals(ID, visible.get(1));

        listing.setColumnVisible(ID, false);

        visible = listing.getVisibleColumns();
        assertEquals(1, visible.size());
        assertTrue(visible.contains(NAME));
        assertFalse(visible.contains(ID));

        listing.setColumnVisible(ID, true);
        visible = listing.getVisibleColumns();
        assertEquals(2, visible.size());
        assertTrue(visible.contains(NAME));
        assertTrue(visible.contains(ID));

        listing = BeanListing.builder(TestBean.class).visible(ID, false).build();
        visible = listing.getVisibleColumns();
        assertEquals(1, visible.size());
        assertTrue(visible.contains(NAME));
        assertFalse(visible.contains(ID));

        /*listing.addComponentColumn(testBean -> new Button());
        List<Column<TestBean>> allColumns = listing.getAllColumns();
        assertEquals(3, allColumns.size());

        listing.addColumn(testBean -> testBean.getId()).setVisible(true);
        visible = listing.getVisibleColumns();
        assertEquals(1, visible.size());*/
    }

    @Test
    public void testToggleColumns() {
        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class)
                .toggleableColumns()
                .build();
        assertEquals(2, listing.getVisibleColumns().size());
        assertEquals(3,listing.getAllColumns().size());
    }

    @Test
    public void testAddComponentColumn() {
        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class,false)
                .build();

        listing.addComponentColumn(testBean -> new Button("sdjfhksjdhf"));
        assertEquals(1,listing.getVisibleColumns().size());
    }

    @Test
    public void testComponentColumns() {

      final   BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).withComponentColumn(item -> new Button("x"))
                .displayBefore(ID).add().build();

        List<String> visible = listing.getVisibleColumns();
        assertEquals(3, visible.size());
        assertNotNull(visible.get(0));
        assertEquals(ID, visible.get(1));
        assertEquals(NAME, visible.get(2));



        final BeanListingBuilder<TestBean> beanListingBuilder = BeanListing.builder(TestBean.class);

        beanListingBuilder.includeVirtualColumns(true)
                .visibleColumns(ID,NAME)
                .editable()
//                .editorBuffered(true)
                .editor(ID,Input.number(Long.class).build())
                .editor(NAME,Input.string().build())
//                .componentRenderer(ID,testBean -> new Button("skdksdf"))
                .withComponentColumn(item -> new Button("x"))
                .editorComponent(new Div(
                        Components.button("Save", e -> listing.saveEditingItem()),
                        Components.button("Cancel", e -> listing.cancelEditing())))
                .displayAsFirst()
                .header("Actions").add();

        final  BeanListing<TestBean>  beanListing = beanListingBuilder.build();
//        System.out.println("Before BeanListing");


        assertEquals(3,beanListing.getVisibleColumns().size());

        final DataTarget<?> TARGET = DataTarget.named("test2");

        final Datastore datastore = JdbcDatastore.builder()
                .dataSource(
                        BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
                                .username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
                .traceEnabled(true).build();

        beanListing.setItems(query -> datastore.query(TARGET)
                .restrict(query.getLimit(), query.getOffset()).stream(BeanProjection.of(TestBean.class)));

        datastore.query(TARGET).stream(BeanProjection.of(TestBean.class)).findFirst()
                .ifPresentOrElse(testBean -> beanListing.editItem(testBean), () -> new RuntimeException("No items found"));


//        beanListing.setItems(query -> datastore.query(TARGET).restrict(query.getLimit(), query.getOffset()).stream(ID,NAME))




    }

    @Test
    public void testItemDetails() {

        final TestBean ITEM1 = new TestBean(1L, "test1");
        final TestBean ITEM2 = new TestBean(2L, "test2");

        BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2)
                .itemDetailsText(item -> item.getName()).build();

        assertFalse(listing.isItemDetailsVisible(ITEM1));

        listing.setItemDetailsVisible(ITEM1, true);
        assertTrue(listing.isItemDetailsVisible(ITEM1));
        assertFalse(listing.isItemDetailsVisible(ITEM2));

        listing.setItemDetailsVisible(ITEM1, false);
        assertFalse(listing.isItemDetailsVisible(ITEM1));
    }

    @SuppressWarnings("unchecked")
    private static <T> DataProvider<T, ?> getDataProvider(BeanListing<T> listing) {
        assertTrue(listing.getComponent() instanceof Grid);
        return ((Grid<T>) listing.getComponent()).getDataProvider();
    }

    @SuppressWarnings("unchecked")
    private static DataCommunicator<TestBean> getDataCommunicator(BeanListing<TestBean> listing) {
        assertTrue(listing.getComponent() instanceof Grid);
        return ((Grid<TestBean>) listing.getComponent()).getDataCommunicator();
    }

    @SuppressWarnings("unchecked")
    private static AbstractItemListing<TestBean, String> getImpl(BeanListing<TestBean> listing) {
        assertTrue(listing instanceof AbstractItemListing);
        return (AbstractItemListing<TestBean, String>) listing;
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<VersionedTestBean, ?> getVersionedDataProvider(BeanListing<VersionedTestBean> listing) {
        assertTrue(listing.getComponent() instanceof Grid);
        return ((Grid<VersionedTestBean>) listing.getComponent()).getDataProvider();
    }

    @Test
    public void testBeanDatastoreVersionRetrievedInListingAfterEdit() throws Exception {

        final BasicDataSource dataSource = BasicDataSource.builder()
                .url("jdbc:h2:mem:test_version_grid;DB_CLOSE_DELAY=-1")
                .username("sa")
                .driverClassName(DatabasePlatform.H2.getDriverClassName())
                .build();

        try (java.sql.Connection connection = dataSource.getConnection();
             java.sql.Statement statement = connection.createStatement()) {
            statement.execute("drop table if exists test_version_grid");
            statement.execute("create table test_version_grid (id bigint primary key, name varchar(100), version bigint default 0 not null)");
            statement.execute("create trigger test_version_grid_version_trigger before insert, update on test_version_grid for each row call \"com.holonplatform.vaadin.flow.test.TestBeanListing$VersionedTestBeanTrigger\"");
        }

        final Datastore datastore = JdbcDatastore.builder()
                .dataSource(dataSource)
                .traceEnabled(true)
                .build();

        final BeanDatastore beanDatastore = BeanDatastore.of(datastore);
        final BeanPropertySet<VersionedTestBean> beanPropertySet = BeanPropertySet.create(VersionedTestBean.class);

        beanDatastore.insert(new VersionedTestBean(1L, "initial", null));

        final BeanListing<VersionedTestBean> listing = BeanListing.builder(VersionedTestBean.class)
                .dataSource(datastore, DataTarget.named("test_version_grid"))
                .build();

        VersionedTestBean initialItem = getVersionedDataProvider(listing)
                .fetch(new Query<>())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("item not found"));

        assertEquals(0L, initialItem.getVersion());
        assertEquals("initial", initialItem.getName());

        final long initialVersion = initialItem.getVersion();

        final VersionedTestBean editedItem = new VersionedTestBean(initialItem.getId(), "updated",
                initialItem.getVersion());
        beanDatastore.save(editedItem);

        VersionedTestBean reloaded = beanDatastore.query(VersionedTestBean.class)
                .filter(beanPropertySet.property("id").eq(1L))
                .findOne()
                .orElseThrow(() -> new RuntimeException("reloaded item not found"));

        assertEquals(initialVersion + 1L, reloaded.getVersion());
        assertEquals("updated", reloaded.getName());

        final BeanListing<VersionedTestBean> updatedListing = BeanListing.builder(VersionedTestBean.class)
                .dataSource(datastore, DataTarget.named("test_version_grid"))
                .build();

        VersionedTestBean updatedItem = getVersionedDataProvider(updatedListing)
                .fetch(new Query<>())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("updated item not found"));

        assertEquals(initialVersion + 1L, updatedItem.getVersion());
        assertEquals(reloaded.getVersion(), updatedItem.getVersion());
        assertEquals("updated", updatedItem.getName());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testFilterInputFormIntegrationWithBeanListing() {

        final DataTarget<?> TARGET = DataTarget.named("test2");
        final com.holonplatform.core.property.Property<FilterInput.Range<Long>> ID_RANGE =
                (com.holonplatform.core.property.Property) com.holonplatform.core.property.PathProperty
                        .create("idRange", FilterInput.Range.class);

        final Datastore datastore = JdbcDatastore.builder()
                .dataSource(
                        BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
                                .username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
                .traceEnabled(true).build();

        final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

        final FilterInputForm<com.vaadin.flow.component.formlayout.FormLayout> filters = FilterInputForm.formLayout()
                .withFilter(beanPropertySet.property(NAME), FilterInput.string(beanPropertySet.property(NAME)))
                .withFilter(ID_RANGE, FilterInput.numberRange(beanPropertySet.property(ID), Long.class))
                .build();

        final BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();
        listing.setItems(filters, (query, filter) -> {
            var datastoreQuery = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
            if (filter != null) {
                datastoreQuery.filter(filter);
            }
            return datastoreQuery.stream(BeanProjection.of(TestBean.class));
        });
        com.holonplatform.core.Registration registration = listing.refreshOnFilterChange(filters);

        List<TestBean> unfiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, unfiltered.size());

        filters.getFilterInput(beanPropertySet.property(NAME))
                .orElseThrow(() -> new AssertionError("Name filter not found"))
                .getInput()
                .setValue("test1");

        List<TestBean> nameFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(1, nameFiltered.size());
        assertEquals(1L, nameFiltered.get(0).getId());

        filters.getFilterInput(ID_RANGE)
                .orElseThrow(() -> new AssertionError("Id range filter not found"))
                .getInput()
                .setValue(new FilterInput.Range<>(2L, 2L));

        List<TestBean> combinedFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(0, combinedFiltered.size());

        filters.getFilterInput(beanPropertySet.property(NAME))
                .orElseThrow(() -> new AssertionError("Name filter not found"))
                .reset();

        List<TestBean> idOnlyFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(1, idOnlyFiltered.size());
        assertEquals(2L, idOnlyFiltered.get(0).getId());

        registration.remove();
    }

    @Test
    public void testItemListingFilterCallbackWithBeanListing() {

        final DataTarget<?> TARGET = DataTarget.named("test2");

        final Datastore datastore = JdbcDatastore.builder()
                .dataSource(
                        BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
                                .username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
                .traceEnabled(true).build();

        final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

        final FilterInputForm<com.vaadin.flow.component.formlayout.FormLayout> filters = FilterInputForm.formLayout()
                .withFilter(beanPropertySet.property(NAME))
                .withFilter(beanPropertySet.property(ID))
                .build();

        final BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();
        listing.setItems(filters, (query, filter) -> {
            var datastoreQuery = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
            if (filter != null) {
                datastoreQuery.filter(filter);
            }
            return datastoreQuery.stream(BeanProjection.of(TestBean.class));
        });

        com.holonplatform.core.Registration registration = listing.refreshOnFilterChange(filters);

        List<TestBean> unfiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, unfiltered.size());

        filters.getFilterInput(beanPropertySet.property(NAME))
                .orElseThrow(() -> new AssertionError("Name filter not found"))
                .getInput()
                .setValue("test1");

        List<TestBean> nameFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(1, nameFiltered.size());
        assertEquals(1L, nameFiltered.get(0).getId());

        filters.getFilterInput(beanPropertySet.property(ID))
                .orElseThrow(() -> new AssertionError("Id filter not found"))
                .getInput()
                .setValue(2L);

        List<TestBean> combinedFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(0, combinedFiltered.size());

        registration.remove();
    }

  @Test
  public void testBuilderLevelFilterInputMethods() {

    final DataTarget<?> TARGET = DataTarget.named("test2");

    final Datastore datastore = JdbcDatastore.builder()
        .dataSource(BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
            .username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
        .traceEnabled(true).build();

    final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

    final FilterInputForm<com.vaadin.flow.component.formlayout.FormLayout> filters = FilterInputForm.formLayout()
        .withFilter(beanPropertySet.property(NAME), FilterInput.string(beanPropertySet.property(NAME))).build();

    final BeanListing<TestBean> listing = BeanListing.builder(TestBean.class)
        .setItems(filters, (query, filter) -> {
          var datastoreQuery = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
          if (filter != null) {
            datastoreQuery.filter(filter);
          }
          return datastoreQuery.stream(BeanProjection.of(TestBean.class));
        })
        .refreshOnFilterChange(filters)
        .build();

    List<TestBean> unfiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(2, unfiltered.size());

    filters.getFilterInput(beanPropertySet.property(NAME))
        .orElseThrow(() -> new AssertionError("Name filter not found"))
        .getInput().setValue("test2");

    List<TestBean> filtered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(1, filtered.size());
    assertEquals(2L, filtered.get(0).getId());
  }

  @Test
  public void testBuilderLevelFilterSignalMethods() {

    final DataTarget<?> TARGET = DataTarget.named("test2");

    final Datastore datastore = JdbcDatastore.builder()
        .dataSource(BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
            .username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
        .traceEnabled(true).build();

    final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

    final FilterInputForm<com.vaadin.flow.component.formlayout.FormLayout> filters = FilterInputForm.formLayout()
        .withFilter(beanPropertySet.property(NAME), FilterInput.string(beanPropertySet.property(NAME))).build();

    final BeanListing<TestBean> listing = BeanListing.builder(TestBean.class)
        .setItems(filters, (query, filter) -> {
          var datastoreQuery = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
          if (filter != null) {
            datastoreQuery.filter(filter);
          }
          return datastoreQuery.stream(BeanProjection.of(TestBean.class));
        })
        .refreshOnFilterSignal(filters)
        .build();

    List<TestBean> unfiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(2, unfiltered.size());

    filters.getFilterInput(beanPropertySet.property(NAME))
        .orElseThrow(() -> new AssertionError("Name filter not found"))
        .getInput().setValue("test1");

    List<TestBean> filtered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(1, filtered.size());
    assertEquals(1L, filtered.get(0).getId());
  }

  @Test
  public void testBuilderLevelBindFiltersSignalMethods() {

    final DataTarget<?> TARGET = DataTarget.named("test2");

    final Datastore datastore = JdbcDatastore.builder()
        .dataSource(BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
            .username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
        .traceEnabled(true).build();

    final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

    final FilterInputForm<com.vaadin.flow.component.formlayout.FormLayout> filters = FilterInputForm.formLayout()
        .withFilter(beanPropertySet.property(NAME), FilterInput.string(beanPropertySet.property(NAME))).build();

    final BeanListing<TestBean> listing = BeanListing.builder(TestBean.class)
        .bindFiltersSignal(filters, (query, filter) -> {
          var datastoreQuery = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
          if (filter != null) {
            datastoreQuery.filter(filter);
          }
          return datastoreQuery.stream(BeanProjection.of(TestBean.class));
        })
        .build();

    List<TestBean> unfiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(2, unfiltered.size());

    filters.getFilterInput(beanPropertySet.property(NAME))
        .orElseThrow(() -> new AssertionError("Name filter not found"))
        .getInput().setValue("test2");

    List<TestBean> filtered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
    assertEquals(1, filtered.size());
    assertEquals(2L, filtered.get(0).getId());
  }

    @DataPath("test_filter_types")
    public static class FilterTypesBean {

        @DataPath("id")
        private long id;

        @DataPath("name")
        private String name;

        @DataPath("age")
        private Integer age;

        @DataPath("active")
        private Boolean active;

        @DataPath("birth")
        private LocalDate birth;

        public FilterTypesBean() {
            super();
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public LocalDate getBirth() {
            return birth;
        }

        public void setBirth(LocalDate birth) {
            this.birth = birth;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testFilterInputFormIntegrationWithBeanListingMultipleInputTypes() throws Exception {

        final DataTarget<?> TARGET = DataTarget.named("test_filter_types");

        final StringProperty NAME_FILTER = StringProperty.create("name");
        final NumericProperty<Integer> AGE_FILTER = NumericProperty.integerType("age");
        final com.holonplatform.core.property.Property<FilterInput.Range<Integer>> AGE_RANGE =
                (com.holonplatform.core.property.Property) com.holonplatform.core.property.PathProperty
                        .create("ageRange", FilterInput.Range.class);
        final com.holonplatform.core.property.Property<Boolean> ACTIVE_FILTER =
                com.holonplatform.core.property.PathProperty.create("active", Boolean.class);
        final com.holonplatform.core.property.Property<LocalDate> BIRTH_FILTER =
                com.holonplatform.core.property.PathProperty.create("birth", LocalDate.class);

        final BasicDataSource dataSource = BasicDataSource.builder()
                .url("jdbc:h2:mem:test_filter_types_bean;DB_CLOSE_DELAY=-1")
                .username("sa")
                .driverClassName(DatabasePlatform.H2.getDriverClassName())
                .build();

        try (java.sql.Connection connection = dataSource.getConnection();
                java.sql.Statement statement = connection.createStatement()) {
            statement.execute("drop table if exists test_filter_types");
            statement.execute("create table test_filter_types (id bigint primary key, name varchar(50), age integer, active boolean, birth date)");
            statement.execute("insert into test_filter_types (id, name, age, active, birth) values (1, 'Alice', 30, true, DATE '1990-01-01')");
            statement.execute("insert into test_filter_types (id, name, age, active, birth) values (2, 'Bob', 40, false, DATE '1985-05-05')");
            statement.execute("insert into test_filter_types (id, name, age, active, birth) values (3, 'Carol', 25, true, DATE '2000-06-15')");
        }

        final Datastore datastore = JdbcDatastore.builder()
                .dataSource(dataSource)
                .traceEnabled(true)
                .build();

        final FilterInputForm<com.vaadin.flow.component.formlayout.FormLayout> filters = FilterInputForm.formLayout()
                .withFilter(NAME_FILTER, FilterInput.string(NAME_FILTER))
                .withFilter(AGE_FILTER, FilterInput.number(AGE_FILTER, Integer.class))
                .withFilter(AGE_RANGE, FilterInput.numberRange(AGE_FILTER, Integer.class))
                .withFilter(ACTIVE_FILTER, FilterInput.bool(ACTIVE_FILTER))
                .withFilter(BIRTH_FILTER, FilterInput.localDate(BIRTH_FILTER))
                .build();

        final BeanListing<FilterTypesBean> listing = BeanListing.builder(FilterTypesBean.class).build();
        listing.setItems(filters, (query, filter) -> {
            var datastoreQuery = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
            if (filter != null) {
                datastoreQuery.filter(filter);
            }
            return datastoreQuery.stream(BeanProjection.of(FilterTypesBean.class));
        });
        com.holonplatform.core.Registration registration = listing.refreshOnFilterChange(filters);

        List<FilterTypesBean> unfiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(3, unfiltered.size());

        filters.getFilterInput(NAME_FILTER).orElseThrow(() -> new AssertionError("Name filter not found"))
                .getInput().setValue("Alice");
        List<FilterTypesBean> nameFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(1, nameFiltered.size());
        assertEquals(1L, nameFiltered.get(0).getId());

        filters.getFilterInput(NAME_FILTER).orElseThrow(() -> new AssertionError("Name filter not found")).reset();
        filters.getFilterInput(AGE_FILTER).orElseThrow(() -> new AssertionError("Age filter not found"))
                .getInput().setValue(40);
        List<FilterTypesBean> numberFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(1, numberFiltered.size());
        assertEquals(2L, numberFiltered.get(0).getId());

        filters.getFilterInput(AGE_FILTER).orElseThrow(() -> new AssertionError("Age filter not found")).reset();
        filters.getFilterInput(AGE_RANGE).orElseThrow(() -> new AssertionError("Age range filter not found"))
                .getInput().setValue(new FilterInput.Range<>(25, 30));
        List<FilterTypesBean> rangeFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, rangeFiltered.size());

        filters.getFilterInput(AGE_RANGE).orElseThrow(() -> new AssertionError("Age range filter not found")).reset();
        filters.getFilterInput(ACTIVE_FILTER).orElseThrow(() -> new AssertionError("Active filter not found"))
                .getInput().setValue(Boolean.TRUE);
        List<FilterTypesBean> boolFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(2, boolFiltered.size());

        filters.getFilterInput(ACTIVE_FILTER).orElseThrow(() -> new AssertionError("Active filter not found")).reset();
        filters.getFilterInput(BIRTH_FILTER).orElseThrow(() -> new AssertionError("Birth filter not found"))
                .getInput().setValue(LocalDate.of(1990, 1, 1));
        List<FilterTypesBean> dateFiltered = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
        assertEquals(1, dateFiltered.size());
        assertEquals(1L, dateFiltered.get(0).getId());

        registration.remove();
    }

}
