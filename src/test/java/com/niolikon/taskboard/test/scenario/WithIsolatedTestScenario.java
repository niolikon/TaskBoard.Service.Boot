package com.niolikon.taskboard.test.scenario;

import java.lang.annotation.*;

/**
 * Marks a test method with an isolated test scenario.
 * <p>
 * This annotation is used to specify a test dataset that will be loaded
 * into the database before the test runs. The dataset is defined within
 * the specified `dataClass`, which should contain a static method that
 * returns a collection of test entities.
 * </p>
 *
 * Example usage:
 * <pre>
 * {@code
 * @Test
 * @WithIsolatedTestScenario(dataClass = SingleTodoTestScenario.class)
 * void givenSingleTodo_whenReadAll_thenListWithSingleTodoIsReturned() {
 *     // Test logic here...
 * }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithIsolatedTestScenario {

    /**
     * Specifies the class that contains the dataset for this test.
     * <p>
     * This class must define a static method that returns a collection of
     * test entities to be persisted before the test execution.
     * </p>
     *
     * @return the class containing the dataset
     */
    Class<?> dataClass();

    /**
     * Specifies the name of the static method within `dataClass` that provides the dataset.
     * <p>
     * By default, this method is expected to be named {@code "getDataset"}.
     * If a different name is used, it must be explicitly specified.
     * </p>
     *
     * @return the method name returning the dataset (default: "getDataset")
     */
    String methodName() default "getDataset";
}
