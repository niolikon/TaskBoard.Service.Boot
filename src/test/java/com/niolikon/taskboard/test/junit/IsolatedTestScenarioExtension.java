package com.niolikon.taskboard.test.junit;

import com.niolikon.taskboard.test.scenario.WithIsolatedTestScenario;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.extension.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IsolatedTestScenarioExtension implements BeforeEachCallback, AfterEachCallback {

    private TestEntityManager testEntityManager;
    private TransactionTemplate transactionTemplate;
    private final List<Object> persistedEntities = new ArrayList<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        EntityManagerFactory entityManagerFactory = SpringExtension.getApplicationContext(context).getBean(EntityManagerFactory.class);
        testEntityManager = new TestEntityManager(entityManagerFactory);
        JpaTransactionManager transactionManager = SpringExtension.getApplicationContext(context).getBean(JpaTransactionManager.class);
        transactionTemplate = new TransactionTemplate(transactionManager);

        context.getTestMethod().ifPresent(method -> {
            WithIsolatedTestScenario annotation = method.getAnnotation(WithIsolatedTestScenario.class);
            if (annotation != null) {
                transactionTemplate.execute(status -> {
                    try {
                        Class<?> dataClass = annotation.dataClass();
                        String methodName = annotation.methodName();

                        Method datasetMethod = dataClass.getDeclaredMethod(methodName);
                        List<?> entities = (List<?>) datasetMethod.invoke(null);

                        for (Object entity : entities) {
                            Object persisted = testEntityManager.persist(entity);
                            persistedEntities.add(persisted);
                        }
                        testEntityManager.flush();
                    } catch (Exception e) {
                        throw new RuntimeException("Error loading dataset for test: " + method.getName(), e);
                    }
                    return null;
                });
            }
        });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (testEntityManager == null || transactionTemplate == null) {
            throw new IllegalStateException("EntityManager or TransactionTemplate was not initialized!");
        }

        transactionTemplate.execute(status -> {
            Collections.reverse(persistedEntities);
            for (Object entity : persistedEntities) {
                Object mergedEntity = testEntityManager.merge(entity);
                testEntityManager.remove(mergedEntity);
            }
            testEntityManager.flush();
            persistedEntities.clear();
            return null;
        });
    }
}
