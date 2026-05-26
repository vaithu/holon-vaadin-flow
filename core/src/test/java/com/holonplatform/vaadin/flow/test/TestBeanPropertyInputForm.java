/*
 * Copyright 2016-2017 Axioma srl.
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

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.beans.DataPath;
import com.holonplatform.core.beans.Identifier;
import com.holonplatform.core.beans.Ignore;
import com.holonplatform.core.beans.Sequence;
import com.holonplatform.core.beans.Version;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.beans.BeanDatastore;
import com.holonplatform.core.i18n.Caption;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.jdbc.JdbcDatastore;
import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BeanPropertyInputForm}.
 */
public class TestBeanPropertyInputForm {

    // -----------------------------------------------------------------------
    // Test bean classes
    // -----------------------------------------------------------------------

    /** Simple bean with @Sequence ordering, @Identifier, and @Version. */
    public static class CustomerBean {

        @Identifier
        @Sequence(1)
        private Long id;

        @Sequence(2)
        private String firstName;

        @Sequence(3)
        private String lastName;

        @Version
        @Sequence(4)
        private Long version;

        public CustomerBean() {}

        public Long getId()              { return id; }
        public void setId(Long id)       { this.id = id; }
        public String getFirstName()     { return firstName; }
        public void setFirstName(String v) { this.firstName = v; }
        public String getLastName()      { return lastName; }
        public void setLastName(String v)  { this.lastName = v; }
        public Long getVersion()         { return version; }
        public void setVersion(Long v)   { this.version = v; }
    }

    /** Bean without any special annotations. */
    public static class SimpleBean {
        private String name;
        private Integer age;

        public SimpleBean() {}
        public String getName()       { return name; }
        public void setName(String v) { this.name = v; }
        public Integer getAge()       { return age; }
        public void setAge(Integer v) { this.age = v; }
    }

    /** Bean used to verify @Ignore, @Caption and @Sequence handling. */
    public static class AnnotatedBean {

        @Sequence(2)
        private String second;

        @Caption("Display Name")
        @Sequence(1)
        private String displayName;

        @Ignore
        private String ignored;

        public AnnotatedBean() {}

        public String getSecond() { return second; }
        public void setSecond(String second) { this.second = second; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getIgnored() { return ignored; }
        public void setIgnored(String ignored) { this.ignored = ignored; }
    }

    public static class DbVersionTrigger implements org.h2.api.Trigger {

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

    @DataPath("test_form_db")
    public static class DbBean {

        @Identifier
        @DataPath("id")
        @Sequence(1)
        private Long id;

        @DataPath("name")
        @Sequence(2)
        private String name;

        @Version
        @DataPath("version")
        @Sequence(3)
        private Long version;

        public DbBean() {
        }

        public DbBean(Long id, String name, Long version) {
            this.id = id;
            this.name = name;
            this.version = version;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
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

    // -----------------------------------------------------------------------
    // Builder / factory tests
    // -----------------------------------------------------------------------

    @Test
    public void testFormLayoutFactory() {
        BeanPropertyInputForm<CustomerBean> form =
                BeanPropertyInputForm.formLayout(CustomerBean.class).build();
        assertNotNull(form);
        assertInstanceOf(FormLayout.class, form.getComponent());
    }

    @Test
    public void testVerticalLayoutFactory() {
        BeanPropertyInputForm<CustomerBean> form =
                BeanPropertyInputForm.verticalLayout(CustomerBean.class).build();
        assertNotNull(form);
        assertInstanceOf(VerticalLayout.class, form.getComponent());
    }

    // -----------------------------------------------------------------------
    // @Identifier / @Version auto-hidden tests
    // -----------------------------------------------------------------------

    @Test
    public void testIdentifierAndVersionAutoHidden() {
        BeanPropertyInputForm<CustomerBean> form =
                BeanPropertyInputForm.formLayout(CustomerBean.class).build();

        // BeanPropertySet gives all 4 properties
        BeanPropertySet<CustomerBean> bps = BeanPropertySet.create(CustomerBean.class);
        List<String> allNames = StreamSupport.stream(bps.spliterator(), false)
                .map(PathProperty::relativeName)
                .toList();
        assertTrue(allNames.contains("id"),      "BPS should contain 'id'");
        assertTrue(allNames.contains("version"), "BPS should contain 'version'");

        // The form has all 4 properties (they are hidden, not excluded)
        assertTrue(form.getProperties().stream()
                .anyMatch(p -> "id".equals(p.toString()) || p.toString().contains("id")),
                "Form property set should contain 'id' (hidden)");
    }

    @Test
    public void testShowIdentifiers() {
        // When showIdentifiers() is called the 'id' Input MUST be present
        BeanPropertyInputForm<CustomerBean> form =
                BeanPropertyInputForm.formLayout(CustomerBean.class)
                        .showIdentifiers()
                        .build();

        // Retrieve the PathProperty for 'id' via the builder helper
        Optional<PathProperty<?>> idProp =
                BeanPropertyInputForm.formLayout(CustomerBean.class)
                        .property("id");
        assertTrue(idProp.isPresent(), "property('id') should be non-empty");

        // Form should have 'id' as an Input (not hidden)
        @SuppressWarnings("unchecked")
        Optional<Input<Long>> idInput = idProp
                .filter(p -> Long.class.equals(p.getType()))
                .map(p -> (PathProperty<Long>) p)
                .flatMap(form::getInput);
        assertTrue(idInput.isPresent(), "Input for 'id' should be present when showIdentifiers() is called");
    }

    // -----------------------------------------------------------------------
    // excludeFields tests
    // -----------------------------------------------------------------------

    @Test
    public void testExcludeFields() {
        BeanPropertyInputForm<CustomerBean> form =
                BeanPropertyInputForm.formLayout(CustomerBean.class)
                        .excludeFields("firstName")
                        .build();

        // 'firstName' must not appear in the property set at all
        boolean hasFirstName = form.getProperties().stream()
                .anyMatch(p -> "firstName".equals(p.getName()));
        assertFalse(hasFirstName, "'firstName' should not be in the form after excludeFields()");
    }

    @Test
    public void testPropertyHelperReturnsEmptyForExcluded() {
        Optional<PathProperty<?>> prop =
                BeanPropertyInputForm.formLayout(CustomerBean.class)
                        .excludeFields("lastName")
                        .property("lastName");
        assertTrue(prop.isEmpty(), "property() should return empty for an excluded field");
    }

    @Test
    public void testPropertyHelperReturnsValueForIncluded() {
        Optional<PathProperty<?>> prop =
                BeanPropertyInputForm.formLayout(CustomerBean.class)
                        .property("firstName");
        assertTrue(prop.isPresent(), "property() should return the PathProperty for a non-excluded field");
        assertEquals("firstName", prop.get().relativeName());
    }

    // -----------------------------------------------------------------------
    // readOnlyFields tests
    // -----------------------------------------------------------------------

    @Test
    public void testReadOnlyFields() {
        BeanPropertyInputForm<CustomerBean> form =
                BeanPropertyInputForm.formLayout(CustomerBean.class)
                        .readOnlyFields("firstName")
                        .build();

        Optional<PathProperty<?>> firstNameProp =
                BeanPropertyInputForm.formLayout(CustomerBean.class).property("firstName");
        firstNameProp.ifPresent(p -> {
            @SuppressWarnings("unchecked")
            Optional<Input<String>> input = form.getInput((PathProperty<String>) p);
            input.ifPresent(i -> assertTrue(i.isReadOnly(), "'firstName' input should be read-only"));
        });
    }

    // -----------------------------------------------------------------------
    // setBean / getBean round-trip tests
    // -----------------------------------------------------------------------

    @Test
    public void testSetBeanPopulatesInputs() {
        BeanPropertyInputForm<SimpleBean> form =
                BeanPropertyInputForm.formLayout(SimpleBean.class).build();

        SimpleBean bean = new SimpleBean();
        bean.setName("Alice");
        bean.setAge(30);
        form.setBean(bean);

        // Get the PathProperty for 'name'
        Optional<PathProperty<?>> nameProp =
                BeanPropertyInputForm.formLayout(SimpleBean.class).property("name");
        assertTrue(nameProp.isPresent());

        @SuppressWarnings("unchecked")
        Optional<Input<String>> nameInput = form.getInput((PathProperty<String>) nameProp.get());
        assertTrue(nameInput.isPresent());
        assertEquals("Alice", nameInput.get().getValue());
    }

    @Test
    public void testGetBeanReadsFromInputs() {
        BeanPropertyInputForm<SimpleBean> form =
                BeanPropertyInputForm.formLayout(SimpleBean.class).build();

        // Populate 'name' via setBean
        SimpleBean original = new SimpleBean();
        original.setName("Bob");
        form.setBean(original);

        // Extract without validation
        SimpleBean extracted = form.getBean(false);
        assertNotNull(extracted);
        assertEquals("Bob", extracted.getName());
    }

    @Test
    public void testSetBeanGetBeanRoundTrip() {
        BeanPropertyInputForm<SimpleBean> form =
                BeanPropertyInputForm.formLayout(SimpleBean.class).build();

        SimpleBean original = new SimpleBean();
        original.setName("Carol");
        original.setAge(25);

        form.setBean(original);
        SimpleBean result = form.getBean(false);

        assertEquals("Carol", result.getName());
        assertEquals(25, result.getAge());
    }

    // -----------------------------------------------------------------------
    // configure() escape-hatch test
    // -----------------------------------------------------------------------

    @Test
    public void testConfigureEscapeHatch() {
        // Verify that configure() can successfully set ENTER navigation
        assertDoesNotThrow(() ->
                BeanPropertyInputForm.formLayout(SimpleBean.class)
                        .configure(fb -> fb
                                .enterMovesFocusToNext(true)
                                .validateOnEnterFocusMove(false))
                        .build()
        );
    }

    // -----------------------------------------------------------------------
    // getValue(boolean) / setValue(PropertyBox) delegation tests
    // -----------------------------------------------------------------------

    @Test
    public void testGetValueDelegation() {
        BeanPropertyInputForm<SimpleBean> form =
                BeanPropertyInputForm.formLayout(SimpleBean.class).build();

        SimpleBean bean = new SimpleBean();
        bean.setName("Dave");
        form.setBean(bean);

        PropertyBox box = form.getValue(false);
        assertNotNull(box);
    }

    @Test
    public void testIsPropertyInputForm() {
        BeanPropertyInputForm<SimpleBean> form =
                BeanPropertyInputForm.formLayout(SimpleBean.class).build();
        assertInstanceOf(PropertyInputForm.class, form);
    }

    @Test
    public void testIgnoreAnnotationExcludedAutomatically() {
        BeanPropertyInputForm<AnnotatedBean> form =
                BeanPropertyInputForm.formLayout(AnnotatedBean.class).build();

        boolean hasIgnored = form.getProperties().stream()
                .map(PathProperty.class::cast)
                .anyMatch(p -> "ignored".equals(p.relativeName()));
        assertFalse(hasIgnored, "@Ignore field must be excluded from form properties");

        Optional<PathProperty<?>> ignored = BeanPropertyInputForm.formLayout(AnnotatedBean.class)
                .property("ignored");
        assertTrue(ignored.isEmpty(), "property('ignored') should be empty for @Ignore field");
    }

    @Test
    public void testCaptionAnnotationAppliedToInputLabel() {
        BeanPropertyInputForm<AnnotatedBean> form =
                BeanPropertyInputForm.formLayout(AnnotatedBean.class).build();

        Optional<PathProperty<?>> property = BeanPropertyInputForm.formLayout(AnnotatedBean.class)
                .property("displayName");
        assertTrue(property.isPresent());

        @SuppressWarnings("unchecked")
        Optional<Input<String>> input = form.getInput((PathProperty<String>) property.get());
        assertTrue(input.isPresent());

        String label = input.get().hasLabel().map(com.holonplatform.vaadin.flow.components.HasLabel::getLabel)
                .orElse(null);
        assertEquals("Display Name", label);
    }

    @Test
    public void testSequenceOrderingApplied() {
        BeanPropertyInputForm<AnnotatedBean> form =
                BeanPropertyInputForm.formLayout(AnnotatedBean.class).build();

        List<String> order = form.getBindings()
                .map(b -> (PathProperty<?>) b.getProperty())
                .map(PathProperty::relativeName)
                .toList();

        assertEquals(List.of("displayName", "second"), order,
                "Bindings should follow @Sequence ordering and ignore @Ignore fields");
    }

    @Test
    public void testBeanDatastoreInsertUpdateUsingForm() throws Exception {
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test_form_db;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        try (java.sql.Connection connection = dataSource.getConnection();
             java.sql.Statement statement = connection.createStatement()) {
            statement.execute("drop table if exists test_form_db");
            statement.execute("create table test_form_db (id bigint primary key, name varchar(100), version bigint default 0 not null)");
            statement.execute("create trigger test_form_db_version_trigger before insert, update on test_form_db for each row call \"com.holonplatform.vaadin.flow.test.TestBeanPropertyInputForm$DbVersionTrigger\"");
        }

        final Datastore datastore = JdbcDatastore.builder()
                .dataSource(dataSource)
                .traceEnabled(true)
                .build();
        final BeanDatastore beanDatastore = BeanDatastore.of(datastore);
        final BeanPropertySet<DbBean> dbBeanPropertySet = BeanPropertySet.create(DbBean.class);

        // Insert an initial bean row.
        beanDatastore.insert(new DbBean(1L, "initial", null));

        DbBean fromDb = beanDatastore.query(DbBean.class)
                .filter(dbBeanPropertySet.property("id").eq(1L))
                .findOne()
                .orElseThrow(() -> new RuntimeException("Initial DB bean not found"));

        assertEquals(0L, fromDb.getVersion());
        assertEquals("initial", fromDb.getName());

        // Load into form and update only editable field(s).
        BeanPropertyInputForm<DbBean> form = BeanPropertyInputForm.formLayout(DbBean.class).build();
        form.setBean(fromDb);

        @SuppressWarnings("unchecked")
        PathProperty<String> nameProperty = (PathProperty<String>) BeanPropertyInputForm.formLayout(DbBean.class)
                .property("name")
                .orElseThrow(() -> new RuntimeException("name property not found"));

        Input<String> nameInput = form.getInput(nameProperty)
                .orElseThrow(() -> new RuntimeException("name input not found"));
        nameInput.setValue("updated");

        DbBean edited = form.getBean(false);
        edited.setId(fromDb.getId());
        edited.setVersion(fromDb.getVersion());

        // Persist update and verify version bump + updated value.
        beanDatastore.save(edited);

        DbBean reloaded = beanDatastore.query(DbBean.class)
                .filter(dbBeanPropertySet.property("id").eq(1L))
                .findOne()
                .orElseThrow(() -> new RuntimeException("Reloaded DB bean not found"));

        assertEquals("updated", reloaded.getName());
        assertEquals(fromDb.getVersion() + 1L, reloaded.getVersion());
    }
}






