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
package com.holonplatform.vaadin.flow.examples;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValuePresenterRegistry;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;

@SuppressWarnings("unused")
public class ExampleListing {

	// tag::propertydef[]
	static final NumericProperty<Long> ID = NumericProperty.longType("id");
	static final StringProperty NAME = StringProperty.create("name");
	static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME);
	// end::propertydef[]

	// tag::target[]
	static final DataTarget<?> TARGET = DataTarget.named("subjects");
	// end::target[]

	public void listing1() {
		// tag::listing1[]
		PropertyListing listing = PropertyListing.builder(SUBJECT).build(); // <1>

		listing = Components.listing.properties(ID, NAME).build(); // <2>

		new VerticalLayout().add(listing.getComponent()); // <3>
		// end::listing1[]
	}

	// tag::beandef[]
	class MyBean {

		private Long id;
		private String name;

		public MyBean() {
			super();
		}

		public MyBean(Long id, String name) {
			super();
			this.id = id;
			this.name = name;
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

	}
	// end::beandef[]

	public void listing2() {
		// tag::listing2[]
		BeanListing<MyBean> listing = BeanListing.builder(MyBean.class).build(); // <1>

		listing = Components.listing.items(MyBean.class).build(); // <2>

		new VerticalLayout().add(listing.getComponent()); // <3>
		// end::listing2[]
	}

	public void listing3() {
		// tag::listing3[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) // <1>
				.items(PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build()).build();

		listing = PropertyListing.builder(SUBJECT) // <2>
				.addItem(PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build())
				.addItem(PropertyBox.builder(SUBJECT).set(ID, 2L).set(NAME, "Two").build()).build();
		// end::listing3[]
	}

	public void listing4() {
		// tag::listing4[]
		BeanListing<MyBean> listing = BeanListing.builder(MyBean.class) // <1>
				.items(new MyBean(1L, "One"), new MyBean(2L, "Two")).build();

		listing = BeanListing.builder(MyBean.class) // <2>
				.addItem(new MyBean(1L, "One")) //
				.addItem(new MyBean(2L, "Two")).build();
		// end::listing4[]
	}

	public void listing5() {
		// tag::listing5[]
		DataProvider<PropertyBox, ?> dataProvider = getPropertyBoxDataProvider(); // <1>

		PropertyListing listing = PropertyListing.builder(SUBJECT).dataSource(dataProvider) // <2>
				.build();
		// end::listing5[]
	}

	public void listing6() {
		// tag::listing6[]
		DataProvider<MyBean, ?> dataProvider = getBeanDataProvider(); // <1>

		BeanListing<MyBean> listing = BeanListing.builder(MyBean.class).dataSource(dataProvider) // <2>
				.build();
		// end::listing6[]
	}

	public void listing7() {
		PropertyBox itemToRefresh = null;
		// tag::listing7[]
		PropertyListing listing = PropertyListing.builder(SUBJECT).dataSource(getDataProvider()).build(); // <1>

		listing.refresh(); // <2>
		listing.refreshItem(itemToRefresh); // <3>
		// end::listing7[]
	}

	public void listing8() {
		// tag::listing8[]
		Datastore datastore = getDatastore();

		PropertyListing listing = PropertyListing.builder(SUBJECT) // <1>
				.dataSource(DatastoreDataProvider.create(datastore, TARGET, SUBJECT)) // <2>
				.build();
		// end::listing8[]
	}

	public void listing9() {
		// tag::listing9[]
		Datastore datastore = getDatastore();

		PropertyListing listing = PropertyListing.builder(SUBJECT) // <1>
				.dataSource(datastore, TARGET) // <2>
				.build();
		// end::listing9[]
	}

	public void listing10() {
		// tag::listing10[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.dataSource(getDatastore(), TARGET) // <1>
				.withQueryFilter(NAME.isNotNull()) // <2>
				.withQuerySort(ID.asc()) // <3>
				.build();
		// end::listing10[]
	}

	public void listing11() {
		// tag::listing11[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.pageSize(50) // <1>
				.columnReorderingAllowed(true) // <2>
				.resizable(true) // <3>
				.frozenColumns(1) // <4>
				.heightByRows(true) // <5>
				.verticalScrollingEnabled(true) // <6>
				.build();
		// end::listing11[]
	}

	public void listing12() {
		// tag::listing12[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.visibleColumns(NAME, ID) // <1>
				.displayAsFirst(NAME) // <2>
				.displayAsLast(ID) // <3>
				.displayBefore(NAME, ID) // <4>
				.displayAfter(ID, NAME) // <5>
				.hidden(ID) // <6>
				.build();

		BeanListing<MyBean> listing2 = BeanListing.builder(MyBean.class) // <7>
				.visibleColumns("name", "id").displayAsFirst("name").hidden("id").build();
		// end::listing12[]
	}

	public void listing13() {
		// tag::listing13[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.header(NAME, "The name") // <1>
				.header(NAME, "The name", "name.message.code") // <2>
				.width(NAME, "100px") // <3>
				.flexGrow(NAME, 1) // <4>
				.alignment(NAME, ColumnAlignment.CENTER) // <5>
				.footer(NAME, "Footer text") // <6>
				.resizable(NAME, true) // <7>
				.sortable(NAME, true) // <8>
				.sortUsing(NAME, ID) // <9>
				.sortProvider(NAME, direction ->
				/* sort logic omitted */
				null) // <10>
				.valueProvider(NAME, item -> item.getValue(NAME)) // <11>
				.renderer(NAME, new TextRenderer<>()) // <12>
				.frozen(NAME, true) // <13>
				.build();
		// end::listing13[]
	}

	public void listing14() {
		// tag::listing14[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.withColumn(item -> "Virtual: " + item.getValue(ID)) // <1>
				.header("Virtual") // <2>
				.displayBefore(NAME) // <3>
				.add() // <4>
				.build();
		// end::listing14[]
	}

	public void listing15() {
		// tag::listing15[]
		BeanListing<MyBean> listing2 = BeanListing.builder(MyBean.class) //
				.withColumn(item -> "Virtual: " + item.getId()) // <1>
				.header("Virtual") // <2>
				.displayAsFirst() // <3>
				.add() // <4>
				.build();
		// end::listing15[]
	}

	public void listing16() {
		// tag::listing16[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.withComponentColumn(item -> new Button(item.getValue(NAME))) // <1>
				.header("Component") // <2>
				.sortUsing(NAME) // <3>
				.add() // <4>
				.build();
		// end::listing16[]
	}

	public void listing17() {
		// tag::listing17a[]
		PropertyValuePresenterRegistry.getDefault() // <1>
				.forProperty(ID, (property, value) -> "#" + value); // <2>
		// end::listing17a[]

		// tag::listing17b[]
		PropertyListing listing = PropertyListing.builder(SUBJECT).build(); // <1>

		String value = ID.present(1L); // <2>
		// end::listing17b[]
	}

	private static DataProvider<PropertyBox, ?> getDataProvider() {
		return null;
	}

	private static DataProvider<PropertyBox, ?> getPropertyBoxDataProvider() {
		return null;
	}

	private static DataProvider<MyBean, ?> getBeanDataProvider() {
		return null;
	}

	private static Datastore getDatastore() {
		return null;
	}

}
