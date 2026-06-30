package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultListingBundleBuilderTest {

	@Test
	void autoCreateColumnsFalse_withExplicitColumnsUsesProvidedColumnOrder() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(false)
				.columns("lastName", "firstName")
				.build();

		assertThat(bundle.listing().getVisibleColumns()).extracting(Object::toString).containsExactly("lastName", "firstName");
		assertThat(bundle.grid().getColumns())
				.extracting(Grid.Column::getKey)
				.containsExactly("lastName", "firstName");
	}

	@Test
	void autoCreateColumnsFalse_withoutExplicitColumnsCreatesNoGridColumns() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(false)
				.build();

		assertThat(bundle.listing().getVisibleColumns()).isEmpty();
		assertThat(bundle.grid().getColumns()).isEmpty();
	}

	@Test
	void autoCreateColumnsFalse_withSingleExplicitColumnUsesThatColumnOnly() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(false)
				.columns("firstName")
				.build();

		assertThat(bundle.listing().getVisibleColumns()).extracting(Object::toString).containsExactly("firstName");
		assertThat(bundle.grid().getColumns())
				.extracting(Grid.Column::getKey)
				.containsExactly("firstName");
	}

	@Test
	void explicitColumnsRemainStableWhenReadAsLists() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(false)
				.columns("firstName", "lastName")
				.build();

		List<String> gridKeys = bundle.grid().getColumns().stream()
				.map(Grid.Column::getKey)
				.toList();

		assertThat(gridKeys).containsExactly("firstName", "lastName");
		assertThat(bundle.listing().getVisibleColumns()).extracting(Object::toString).containsExactly("firstName", "lastName");
	}

	@Test
	void autoCreateColumnsFalse_withHiddenExplicitColumnKeepsOnlyVisibleExplicitColumns() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(false)
				.columns("firstName", "lastName")
				.hidden("lastName")
				.build();

		assertThat(bundle.grid().getColumns())
				.extracting(Grid.Column::getKey)
				.containsExactly("firstName", "lastName");
		assertThat(bundle.listing().getVisibleColumns()).extracting(Object::toString).containsExactly("firstName");
		assertThat(bundle.listing().getHiddenColumns()).extracting(Object::toString).containsExactly("lastName");
	}

	static class Person {
		private String firstName;
		private String lastName;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	}
}

