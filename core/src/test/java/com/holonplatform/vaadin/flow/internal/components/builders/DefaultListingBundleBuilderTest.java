package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultListingBundleBuilderTest {

	/**
	 * When autoCreateColumns=false, columns() is a query-projection hint only.
	 * No grid columns are created even if explicit column names are provided.
	 */
	@Test
	void autoCreateColumnsFalse_withExplicitColumns_gridHasNoColumns() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(false)
				.columns("lastName", "firstName")
				.build();

		assertThat(bundle.listing().getVisibleColumns()).isEmpty();
		assertThat(bundle.grid().getColumns()).isEmpty();
	}

	/**
	 * When autoCreateColumns=false and no explicit columns provided,
	 * the grid has no columns.
	 */
	@Test
	void autoCreateColumnsFalse_withoutExplicitColumnsCreatesNoGridColumns() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(false)
				.build();

		assertThat(bundle.listing().getVisibleColumns()).isEmpty();
		assertThat(bundle.grid().getColumns()).isEmpty();
	}

	/**
	 * When autoCreateColumns=true and explicit columns are provided,
	 * the grid shows only those columns in the specified order.
	 */
	@Test
	void autoCreateColumnsTrue_withExplicitColumns_gridShowsOnlyThoseColumns() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(true)
				.columns("firstName")
				.build();

		assertThat(bundle.listing().getVisibleColumns()).extracting(Object::toString).containsExactly("firstName");
		assertThat(bundle.grid().getColumns())
				.extracting(Grid.Column::getKey)
				.containsExactly("firstName");
	}

	/**
	 * When autoCreateColumns=true and multiple explicit columns are provided,
	 * the grid shows exactly those columns in the specified order.
	 */
	@Test
	void autoCreateColumnsTrue_withMultipleExplicitColumns_gridShowsColumnsInOrder() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(true)
				.columns("firstName", "lastName")
				.build();

		List<String> gridKeys = bundle.grid().getColumns().stream()
				.map(Grid.Column::getKey)
				.toList();

		assertThat(gridKeys).containsExactly("firstName", "lastName");
		assertThat(bundle.listing().getVisibleColumns()).extracting(Object::toString).containsExactly("firstName", "lastName");
	}

	/**
	 * When autoCreateColumns=true, explicit columns combined with hidden()
	 * result in all columns present in the grid, with hidden ones excluded from
	 * visibleColumns and included in hiddenColumns.
	 */
	@Test
	void autoCreateColumnsTrue_withHiddenColumn_visibleColumnsExcludesHidden() {
		ListingBundle<Person> bundle = Components.listing(Person.class)
				.autoCreateColumns(true)
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

