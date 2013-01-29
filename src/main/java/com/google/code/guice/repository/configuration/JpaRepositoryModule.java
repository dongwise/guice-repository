/*
 * Copyright (C) 2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.guice.repository.configuration;

import com.google.code.guice.repository.SimpleQueryDslJpaRepository;
import com.google.code.guice.repository.filter.PersistFilter;
import com.google.code.guice.repository.spi.*;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.querydsl.QueryDslUtils;
import org.springframework.data.repository.Repository;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * Guice module with Repository support. Repository bindings should be made in {@link
 * #bindRepositories(RepositoryBinder)}
 * method. Module requires array of persistence units as an constructor parameter.
 * There is three options to specify persistence units names:
 * <ol>
 * <li>constructor parameter. For example:
 * <pre>new JpaRepositoryModule("persistence-unit1", "persistence-unit2")</pre>
 * </li>
 * <p/>
 * <li>override {@link #getPersistenceUnitsNames} method. For example:
 * <pre>
 *          new JpaRepositoryModule(){
 *                 protected Collection<String> getPersistenceUnitsNames() {
 *                     return Arrays.asList("persistence-unit1", "persistence-unit2");
 *               }
 *          }
 *       </pre>
 * </li>
 * <li>system property 'persistence-units' splitted by symbol ',' or ';' .
 * For example: -Dpersistence-units=persistence-unit1,persistence-unit2</li>
 * </ol>
 * <p>
 * It is very useful to separate ORM specifics from persistence.xml. This technique gives you possibilities to pack
 * your persistence.xml with mappings/specification-driven aspects to artifact. Module will look for this specifics in
 * ${persistence-unit-name}.properties file placed in the classpath. Also you can define all ORM specifics in
 * persistence.xml.
 * </p>
 * <p>
 * NOTE: first persistence unit name will be treated as <b>default</b>. In the example above persistence unit with name
 * "persistence-unit1" will be registered as default persistence unit.
 * </p>
 *
 * @author Alexey Krylov
 * @since 10.04.2012
 */
public abstract class JpaRepositoryModule extends AbstractModule {

    /*===========================================[ STATIC VARIABLES ]=============*/

    /**
     * System property with attached persistence units.
     * <pre>
     *     -Dpersistence-units=unit1,unit2,unit3
     * </pre>
     */
    public static final String P_PERSISTENCE_UNITS = "persistence-units";

    /**
     * Persistence units split regex - can be used for outside property value construction
     */
    public static final String PERSISTENCE_UNITS_SPLIT_REGEX = ",|;";

	/*===========================================[ INSTANCE VARIABLES ]===========*/

    protected String[] persistenceUnitsNames;

    private Logger logger;

    /*===========================================[ CONSTRUCTORS ]=================*/

    protected JpaRepositoryModule(String... persistenceUnitsNames) {
        logger = LoggerFactory.getLogger(getClass());
        String[] foundPersistenceUnitsNames = null;
        if (persistenceUnitsNames.length > 0) {
            foundPersistenceUnitsNames = Arrays.copyOf(persistenceUnitsNames, persistenceUnitsNames.length);
        } else {
            Collection<String> cPersistenceUnits = getPersistenceUnitsNames();
            if (cPersistenceUnits == null) {
                String pPersistenceUnits = System.getProperty(P_PERSISTENCE_UNITS);
                if (pPersistenceUnits == null) {
                    throw new IllegalStateException("Unable to instantiate JpaRepositoryModule: no persistence units specified");
                } else {
                    String[] splittedPersistenceUnits = pPersistenceUnits.split(PERSISTENCE_UNITS_SPLIT_REGEX);
                    if (splittedPersistenceUnits.length > 0) {
                        foundPersistenceUnitsNames = splittedPersistenceUnits;
                    }
                }
            } else {
                foundPersistenceUnitsNames = cPersistenceUnits.toArray(new String[cPersistenceUnits.size()]);
            }
        }

        this.persistenceUnitsNames = foundPersistenceUnitsNames;
    }

    /*===========================================[ CLASS METHODS ]================*/

    /**
     * A way to provide persistence-unit name.
     *
     * @return persistence-unit name.
     */
    protected Collection<String> getPersistenceUnitsNames() {
        return null;
    }

	/*===========================================[ INTERFACE METHODS ]============*/

    @Override
    protected void configure() {
        String simpleName = getClass().getSimpleName();
        String moduleName = simpleName.isEmpty() ? getClass().getName() : simpleName;
        logger.info(String.format("Configuring [%s] for persistence units: %s", moduleName, Arrays.asList(persistenceUnitsNames)));

        bind(CustomRepositoryImplementationResolver.class).in(Scopes.SINGLETON);

        PersistenceUnitsConfigurationManager configurationManager = createPersistenceUnitsConfigurationManager(persistenceUnitsNames);
        bind(PersistenceUnitsConfigurationManager.class).toInstance(configurationManager);

        // Only Spring @Transactional annotation is supported
        TransactionAttributeSource tas = createTransactionAttributeSource();
        bind(TransactionAttributeSource.class).toInstance(tas);

        // Initializing Spring Context
        ApplicationContext applicationContext = createApplicationContext(configurationManager);
        bind(ApplicationContext.class).toInstance(applicationContext);

        // Initializing interceptor for components created by Guice and marked with @Transactional
        CompositeTransactionInterceptor transactionInterceptor = new CompositeTransactionInterceptor();
        requestInjection(transactionInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionInterceptor);

        install(new FactoryModuleBuilder().build(SimpleBatchStoreJpaRepositoryFactory.class));
        install(new FactoryModuleBuilder().build(CustomJpaRepositoryFactoryCreator.class));
        enableQueryDSLSupport();

        // Support for EntityManager and EntityManagerFactory injections
        bindPersistence(configurationManager);

        RepositoryBinder repositoryBinder = createRepositoryBinder();
        logger.info("Binding repositories...");
        bindRepositories(repositoryBinder);
        Iterable<RepositoryBinding> bindings = repositoryBinder.getBindings();
        for (RepositoryBinding binding : bindings) {
            Class<? extends Repository> repositoryClass = binding.getRepositoryClass();
            Class customRepositoryClass = binding.getCustomRepositoryClass();
            String persistenceUnitName = binding.getPersistenceUnitName();

            // Locate persistenceUnitName from annotation when no direct binding defined
            if (persistenceUnitName == null || persistenceUnitName.isEmpty()) {
                String specificPersistenceUnitName = extractAnnotationsPersistenceUnitName(repositoryClass);
                if (specificPersistenceUnitName != null && !specificPersistenceUnitName.isEmpty()) {
                    persistenceUnitName = specificPersistenceUnitName;
                }
            }

            if (configurationManager.getConfiguration(persistenceUnitName) == null) {
                throw new IllegalStateException(String.format("Unable to register repository [%s] - persistence unit [%s] is not registered",
                        repositoryClass.getName(), persistenceUnitName));
            }

            bind(repositoryClass).toProvider(new JpaRepositoryProvider(binding, persistenceUnitName));

            logger.info(String.format("[%s] %s attached to [%s] and available for injection",
                    repositoryClass.getName(),
                    customRepositoryClass != null ? "(" + customRepositoryClass.getName() + ")" : "",
                    persistenceUnitName));
        }

        logger.info(String.format("[%s] configured", moduleName));
    }

    /**
     * Creates configuration manager which contains all required information about specified persistence units and
     * their
     * properties.
     *
     * @param persistenceUnits specified persistence units names
     *
     * @return initialized configuration manger instance
     */
    protected PersistenceUnitsConfigurationManager createPersistenceUnitsConfigurationManager(String... persistenceUnits) {
        PersistenceUnitsConfigurationManager manager = new PersistenceUnitsConfigurationManager();
        for (int i = 0; i < persistenceUnits.length; i++) {
            String persistenceUnitName = persistenceUnits[i];
            Properties persistenceUnitProperties = getPersistenceUnitProperties(persistenceUnitName);
            // First persistence unit will be "default"
            boolean isDefaultConfiguration = i == 0;
            manager.registerConfiguration(
                    new PersistenceUnitConfiguration(persistenceUnitName, persistenceUnitProperties), isDefaultConfiguration);
        }
        return manager;
    }

    /**
     * Custom persistence-unit properties - for example it can consist Hibernate/EclipseLink specific parameters.
     * By-default this properties loaded from file named ${persistence-unit-name}.properties located in the classpath.
     *
     * @return initialized java.util.Properties or null.
     */
    protected Properties getPersistenceUnitProperties(String persistenceUnitName) {
        Properties props = null;
        String propFileName = persistenceUnitName + ".properties";

        InputStream pStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        try {
            props = new Properties();
            if (pStream != null) {
                props.load(pStream);
            }
        } catch (Exception e) {
            logger.error(String.format("Unable to load properties for persistence-unit: [%s]", persistenceUnitName), e);
        } finally {
            if (pStream != null) {
                try {
                    pStream.close();
                } catch (IOException e) {
                    logger.error("Error", e);
                }
            }
        }
        return props;
    }

    /**
     * Creates {@link Transactional} annotation source with annotation parser.
     *
     * @return parser instance.
     *
     * @see AnnotationTransactionAttributeSource
     * @see SpringTransactionAnnotationParser
     */
    protected TransactionAttributeSource createTransactionAttributeSource() {
        return new AnnotationTransactionAttributeSource(new SpringTransactionAnnotationParser());
    }

    /**
     * Creates Spring application context based on found persistence units. Context will be initialized with multiple
     * persistence units support.
     *
     * @param configurationManager configuration manager with persistence units configurations.
     *
     * @return initialized Spring context
     *
     * @see <a href="https://github.com/SpringSource/spring-data-jpa/blob/master/src/test/resources/multiple-entity-manager-integration-context.xml"/>
     */
    protected ApplicationContext createApplicationContext(PersistenceUnitsConfigurationManager configurationManager) {
        GenericApplicationContext context = new GenericApplicationContext();

        String abstractEMFBeanName = "abstractEntityManagerFactory";
        context.registerBeanDefinition(abstractEMFBeanName, BeanDefinitionBuilder.
                genericBeanDefinition(LocalContainerEntityManagerFactoryBean.class).
                setAbstract(true).getBeanDefinition());

        Iterable<PersistenceUnitConfiguration> configurations = configurationManager.getConfigurations();
        for (PersistenceUnitConfiguration configuration : configurations) {
            String persistenceUnitName = configuration.getPersistenceUnitName();
            logger.info(String.format("Processing persistence unit: [%s]", persistenceUnitName));
            Map props = configuration.getProperties();

            String entityManagerFactoryName = "entityManagerFactory#" + persistenceUnitName;

            BeanDefinitionBuilder emfFactoryDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition().setParentName(abstractEMFBeanName).
                    addPropertyValue("persistenceUnitName", persistenceUnitName).
                    addPropertyValue("jpaProperties", props);

            // Additional EntityManagerFactory properties for Spring EMFBean initialization - s.a. jpaDialect, jpaVendorAdapter
            Map<String, Object> properties = getAdditionalEMFProperties(persistenceUnitName);
            if (properties != null) {
                for (String propertyName : properties.keySet()) {
                    Object value = properties.get(propertyName);
                    if (value != null) {
                        logger.info(String.format("Found additional EMF property: [%s] -> [%s]", propertyName, value));
                        emfFactoryDefinitionBuilder.addPropertyValue(propertyName, value);
                    }
                }
            }

            context.registerBeanDefinition(entityManagerFactoryName, emfFactoryDefinitionBuilder.getBeanDefinition());

            // Naming is important - it's needed for later TransactionManager resolution based on @Transactional value with persistenceUnitName
            context.registerBeanDefinition(persistenceUnitName,
                    BeanDefinitionBuilder.genericBeanDefinition(JpaTransactionManager.class).
                            addPropertyReference("entityManagerFactory", entityManagerFactoryName).getBeanDefinition());

            PlatformTransactionManager transactionManager = context.getBean(persistenceUnitName, PlatformTransactionManager.class);
            TransactionInterceptor transactionInterceptor = new TransactionInterceptor(transactionManager, createTransactionAttributeSource());
            transactionInterceptor.setBeanFactory(context);

            // Wiring components
            EntityManagerFactory emf = context.getBean(entityManagerFactoryName, EntityManagerFactory.class);
            EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(emf, props);
            configuration.setEntityManager(entityManager);
            configuration.setEntityManagerFactory(emf);
            configuration.setTransactionManager(transactionManager);
            configuration.setTransactionManagerName(persistenceUnitName);
            configuration.setTransactionInterceptor(transactionInterceptor);

            // Default bindings for EMF & Transaction Manager - they needed for repositories with @Transactional without value
            if (configuration.isDefault()) {
                context.registerAlias(entityManagerFactoryName, "entityManagerFactory");
                context.registerAlias(persistenceUnitName, "transactionManager");
            }
        }

        return context;
    }

    /**
     * Provide additional EntityManagerFactory properties for related {@link LocalContainerEntityManagerFactoryBean}
     * initialization.
     * Some of this properties: jpaDialect, jpaVendorAdapter, dataSource, etc.
     * <p/>
     * NOTE: Overriding of this method requires detailed knowledge of Spring Data specifics.
     * Example:
     * <pre>
     *     protected Map<String, Object> getAdditionalEMFProperties(String persistenceUnitName) {
     *         Map<String, Object> properties = new HashMap<String, Object>();
     *         if ("persistence-unit1".equals(persistenceUnitName)) {
     *             properties.put("jpaDialect", new MySpecificHibernateJpaDialect());
     *             return properties;
     *         }
     *         return properties;
     *     }
     * </pre>
     *
     * @param persistenceUnitName current configurable persistence unit
     *
     * @return additional configuration properties map
     */
    protected Map<String, Object> getAdditionalEMFProperties(String persistenceUnitName) {
        return null;
    }

    /**
     * Enables <a href="http://www.querydsl.com/"/> support if required dependencies is in classpath.
     */
    protected void enableQueryDSLSupport() {
        if (QueryDslUtils.QUERY_DSL_PRESENT) {
            install(new FactoryModuleBuilder().build(SimpleQueryDslJpaRepositoryFactory.class));
        } else {
            logger.info("QueryDSL is disabled");
            bind(SimpleQueryDslJpaRepositoryFactory.class).toInstance(new SimpleQueryDslJpaRepositoryFactory() {
                @Override
                public SimpleQueryDslJpaRepository create(JpaEntityInformation jpaEntityInformation, EntityManager entityManager) {
                    throw new IllegalStateException("QueryDSL is disabled, but used in your project! Please add QueryDSL support libraries!");
                }
            });
        }
    }

    /**
     * Binds EntityManagers/EntityManagerFactories to current injector context.
     * Required for later direct EntityManager/EntityManagerFactory injections.
     *
     * @param configurationManager configuration manager instance
     */
    protected void bindPersistence(PersistenceUnitsConfigurationManager configurationManager) {
        // Support for EntityManager with @PersistenceContext injections
        TypeListener persistenceContextTypeListener = new PersistenceContextTypeListener();
        requestInjection(persistenceContextTypeListener);
        bindListener(Matchers.any(), persistenceContextTypeListener);

        // Support for EntityManagerFactory with @PersistenceUnit injections
        TypeListener persistenceUnitTypeListener = new PersistenceUnitTypeListener();
        requestInjection(persistenceUnitTypeListener);
        bindListener(Matchers.any(), persistenceUnitTypeListener);

        Iterable<PersistenceUnitConfiguration> configurations = configurationManager.getConfigurations();

        for (final PersistenceUnitConfiguration configuration : configurations) {
            /**
             * Provider bindings for EM needed for Web-mode - EM can be recreated
             * (& re-registered in PersistenceUnitConfiguration)
             * in {@link PersistFilter}
             * */
            bind(EntityManager.class).annotatedWith(Names.named(configuration.getPersistenceUnitName())).toProvider(new Provider<EntityManager>() {
                @Override
                public EntityManager get() {
                    return configuration.getEntityManager();
                }
            });
            bind(EntityManagerFactory.class).annotatedWith(Names.named(configuration.getPersistenceUnitName())).toInstance(configuration.getEntityManagerFactory());
        }

        // binding default EntityManager & EntityManagerFactory
        final PersistenceUnitConfiguration defaultConfiguration = configurationManager.getDefaultConfiguration();
        /**
         * Provider bindings for EM needed for Web-mode - EM can be recreated
         * (& re-registered in PersistenceUnitConfiguration)
         * in {@link PersistFilter}
         * */
        bind(EntityManager.class).toProvider(new Provider<EntityManager>() {
            @Override
            public EntityManager get() {
                return defaultConfiguration.getEntityManager();
            }
        });
        bind(EntityManagerFactory.class).toInstance(defaultConfiguration.getEntityManagerFactory());
    }

    /**
     * Creates repository binder instance. This binder will be passed into {@link #bindRepositories(RepositoryBinder)}.
     *
     * @return repository binder instance
     */
    protected RepositoryBinder createRepositoryBinder() {
        return new DefaultRepositoryBinder();
    }

    /**
     * Bind your repositories here.
     * Example:
     * <pre>
     *    protected void bindRepositories(RepositoryBinder binder) {
     *        binder.bind(UserRepository.class).to("test-h2");
     *        binder.bind(AccountRepository.class).withSelfDefinition();
     *        binder.bind(CustomerRepository.class).withCustomImplementation(CustomerRepositoryImpl.class).withSelfDefinition();
     *    }
     * </pre>
     *
     * @param binder repository binder instance
     */
    protected abstract void bindRepositories(RepositoryBinder binder);

    /**
     * Extracts {@code persistenceUnitName} from Repository annotations if no direct binding has been specified.
     *
     * @param repositoryClass repository class
     *
     * @return located persistenceUnitName or <i>null</i> if nothing has been found
     */
    protected String extractAnnotationsPersistenceUnitName(Class<? extends Repository> repositoryClass) {
        String persistenceUnitName = null;
        PersistenceContext persistenceContext = repositoryClass.getAnnotation(PersistenceContext.class);
        if (persistenceContext != null) {
            persistenceUnitName = persistenceContext.unitName();
        }

        boolean persistenceUnitNameFound = false;
        boolean persistenceContextBased = false;
        if (persistenceUnitName == null || persistenceUnitName.isEmpty()) {
            Transactional annotation = repositoryClass.getAnnotation(Transactional.class);
            if (annotation != null) {
                persistenceUnitName = annotation.value();
                if (!persistenceUnitName.isEmpty()) {
                    persistenceUnitNameFound = true;
                }
            }
        } else {
            persistenceUnitNameFound = true;
            persistenceContextBased = true;
        }

        if (persistenceUnitNameFound) {
            logger.info(String.format("[%s] contains specified persistenceUnitName [%s] in @%s annotation",
                    repositoryClass.getName(),
                    persistenceUnitName,
                    persistenceContextBased ? PersistenceContext.class.getName() : Transactional.class.getName()));
        }

        return persistenceUnitName;
    }

	/*===========================================[ GETTER/SETTER ]================*/

    protected Logger getLogger() {
        return logger;
    }
}