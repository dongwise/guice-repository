/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 20.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.qwide.jpa;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.inject.*;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class JPARepositoryProvider<R extends Repository> implements Provider<R> {

    private static final Logger logger = LoggerFactory.getLogger(JPARepositoryProvider.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Class<? extends R> repositoryClass;
    private Provider<EntityManagerFactory> entityManagerFactoryProvider;
    private ApplicationContext context;
    private Provider<EntityManager> entityManagerProvider;

    /*===========================================[ CLASS METHODS ]==============*/

    @Inject
    public void init(Injector injector, Provider<EntityManagerFactory> entityManagerFactoryProvider, Provider<EntityManager> entityManagerProvider) {
        this.entityManagerFactoryProvider = entityManagerFactoryProvider;
        this.entityManagerProvider = entityManagerProvider;
        repositoryClass = extractRepositoryClass(injector);
        context = createSpringContext();
    }

    protected Class<? extends R> extractRepositoryClass(Injector injector) {
        Class repositoryClass;
        Key<R> key = null;
        BiMap<Key<?>, Binding<?>> biMap = ImmutableBiMap.copyOf(injector.getBindings());
        String currentProvider = toString();
        for (Binding<?> value : biMap.values()) {
            if (value.getProvider().toString().equals(currentProvider)) {
                key = (Key<R>) biMap.inverse().get(value);
                break;
            }
        }

        Validate.notNull(key, String.format("Unable to find provider binding for [%s]", toString()));
        repositoryClass = key.getTypeLiteral().getRawType();
        return repositoryClass;
    }

    protected ApplicationContext createSpringContext() {
        GenericApplicationContext context = new GenericApplicationContext();

        context.registerBeanDefinition("entityManagerFactory",
                BeanDefinitionBuilder.genericBeanDefinition(GuiceLocalEntityManagerFactoryBean.class).
                        addConstructorArgValue(entityManagerFactoryProvider).getBeanDefinition());

        context.registerBeanDefinition("transactionManager",
                BeanDefinitionBuilder.genericBeanDefinition(JpaTransactionManager.class).getBeanDefinition());


        context.registerBeanDefinition("jpaRepositoryFactory",
                BeanDefinitionBuilder.genericBeanDefinition(JpaRepositoryFactoryBean.class).getBeanDefinition());

        return context;
    }

    public R get() {
        System.out.println("GET & BIND");
        EntityManager entityManager = entityManagerProvider.get();
        EntityManagerFactory entityManagerFactory = entityManagerFactoryProvider.get();

        // Setup new ThreadLocal entityManager instance
        GuiceLocalEntityManagerFactoryBean entityManagerFactoryBean = context.getBean(GuiceLocalEntityManagerFactoryBean.class);
        EntityManagerFactory proxiedEmf = entityManagerFactoryBean.getObject();
        if (TransactionSynchronizationManager.hasResource(proxiedEmf)) {
            TransactionSynchronizationManager.unbindResource(proxiedEmf);
        }

        TransactionSynchronizationManager.bindResource(proxiedEmf, new EntityManagerHolder(entityManagerProvider.get()));


        logger.info(String.format("Accessing.get: factory=[%d], em=[%d]", entityManagerFactory.hashCode(), entityManager.hashCode()));

        //TODO: for custom implementation
//        bean.setCustomImplementation();

        //TODO: EM закрывается по первой транзакции и не пересоздается....

        /**
         * Some instantiation example is here:
         * https://jira.springsource.org/browse/DATAJPA-69
         */
        JpaRepositoryFactoryBean factory = new JpaRepositoryFactoryBean();
        factory.setEntityManager(entityManager);
        factory.setRepositoryInterface(repositoryClass);
        factory.setBeanFactory(context);
        factory.afterPropertiesSet();

        return (R) factory.getObject();
    }
}