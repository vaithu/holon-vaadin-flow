package com.holonplatform.vaadin.flow.test;

import org.junit.jupiter.api.Test;

class AbstractFormViewTest {

    class Person {
        private String firstName;
        private String lastName;

        public Person() {
        }

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // Getters and setters
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

    /*private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private final BeanValidationBinder<Person> binder = new BeanValidationBinder<>(Person.class);
    private AbstractFormView<Person> abstractFormView;

    class DefaultFormView extends AbstractFormView<Person> {

        public DefaultFormView(BeanValidationBinder<Person> binder, Person value) {
            super(binder, value);
        }

        @Override
        public Person createBlankForm() {
            return new Person();
        }
    }

    @Test
    void setValue() {
        Person person = new Person("John", "Doe");
        abstractFormView = new DefaultFormView(binder, person);


        try {
            abstractFormView.setFormValue(person);
            Person person1 = abstractFormView.getFormValue().orElseThrow();
            Assertions.assertEquals(person1.getFirstName(), person.getFirstName());

        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void getValue() throws ValidationException {
        setValue();
        Assertions.assertNotNull(abstractFormView.getFormValue());
    }

    @Test
    void readBean() {
        Person person = new Person("John", "Doe");
        binder.readBean(person);
    }

    @Test
    void writeBean() {
        Person person = new Person();
        try {
            binder.writeBean(person);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isValid() {
        setValue();
        assert abstractFormView.isValid();

    }*/

    @Test
    void hasChanges() {
    }
}