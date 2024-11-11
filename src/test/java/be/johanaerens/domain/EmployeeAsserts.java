package be.johanaerens.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEmployeeAllPropertiesEquals(Employee expected, Employee actual) {
        assertEmployeeAutoGeneratedPropertiesEquals(expected, actual);
        assertEmployeeAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEmployeeAllUpdatablePropertiesEquals(Employee expected, Employee actual) {
        assertEmployeeUpdatableFieldsEquals(expected, actual);
        assertEmployeeUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEmployeeAutoGeneratedPropertiesEquals(Employee expected, Employee actual) {
        assertThat(expected)
            .as("Verify Employee auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEmployeeUpdatableFieldsEquals(Employee expected, Employee actual) {
        assertThat(expected)
            .as("Verify Employee relevant properties")
            .satisfies(e -> assertThat(e.getFirstName()).as("check firstName").isEqualTo(actual.getFirstName()))
            .satisfies(e -> assertThat(e.getLastName()).as("check lastName").isEqualTo(actual.getLastName()))
            .satisfies(e -> assertThat(e.getEmail()).as("check email").isEqualTo(actual.getEmail()))
            .satisfies(e -> assertThat(e.getEmployeeNumber()).as("check employeeNumber").isEqualTo(actual.getEmployeeNumber()))
            .satisfies(e -> assertThat(e.getPhoneNumber()).as("check phoneNumber").isEqualTo(actual.getPhoneNumber()))
            .satisfies(e -> assertThat(e.getHireDate()).as("check hireDate").isEqualTo(actual.getHireDate()))
            .satisfies(e -> assertThat(e.getLanguage()).as("check language").isEqualTo(actual.getLanguage()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEmployeeUpdatableRelationshipsEquals(Employee expected, Employee actual) {
        // empty method
    }
}