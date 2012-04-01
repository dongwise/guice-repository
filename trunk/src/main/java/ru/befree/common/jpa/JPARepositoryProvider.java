/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 20.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.common.jpa;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.inject.*;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class JPARepositoryProvider<R extends Repository> implements Provider<R> {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Class<? extends R> repositoryClass;
    private Provider<EntityManagerFactory> entityManagerFactoryProvider;
    private ApplicationContext context;
//    private RepositoryFactoryBeanSupport repoBean;
    private Provider<EntityManager> entityManagerProvider;


    /*===========================================[ CLASS METHODS ]==============*/

    @Inject
    public void init(Injector injector, Provider<EntityManagerFactory> entityManagerFactoryProvider, Provider<EntityManager> entityManagerProvider) {
        this.entityManagerFactoryProvider = entityManagerFactoryProvider;
        this.entityManagerProvider = entityManagerProvider;
        repositoryClass = extractRepositoryClass(injector);
        context = configureSpringContext();
    }

    private Class<? extends R> extractRepositoryClass(Injector injector) {
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

    private ApplicationContext configureSpringContext() {
//        JpaRepositoryFactoryBean repoBean = new JpaRepositoryFactoryBean();
//        repoBean.setEntityManager(entityManagerProvider.get());
//        repoBean.setRepositoryInterface(repositoryClass);

        GenericApplicationContext context = new GenericApplicationContext();
        //TODO: for custom implementation
//        bean.setCustomImplementation();

        context.registerBeanDefinition("entityManagerFactory",
                BeanDefinitionBuilder.genericBeanDefinition(GuiceLocalEntityManagerFactoryBean.class).
                        addConstructorArgValue(entityManagerFactoryProvider).
                        addConstructorArgValue(entityManagerProvider).getBeanDefinition());

        context.registerBeanDefinition("transactionManager",
                BeanDefinitionBuilder.genericBeanDefinition(JpaTransactionManager.class).getBeanDefinition());


        context.registerBeanDefinition("jpaRepositoryFactory",
                BeanDefinitionBuilder.genericBeanDefinition(JpaRepositoryFactoryBean.class).getBeanDefinition());

//        repoBean.setBeanFactory(beanFactory);
//        return repoBean;


        return context;
    }

    public R get() {
        EntityManager entityManager = entityManagerProvider.get();
        EntityManager entityManager1 = entityManagerProvider.get();
        System.out.println(String.format("em0: %d, em1: %d", entityManager.hashCode(), entityManager1.hashCode()));
        //TODO !!!DIFFERENT EMs - тут у нас разные EM - на этапе порождения ставим одного, а на этап для транзакции делается из GuiceLocalEM...Bean, там делается просто new а не инъекция
        System.out.println(String.format("Accessing for emf2: [%s], [%d], [%d], new [%d]", Thread.currentThread().getName(), entityManagerFactoryProvider.get().hashCode(), entityManager.hashCode(), entityManagerFactoryProvider.get().createEntityManager().hashCode()));
        JpaRepositoryFactoryBean factory = new JpaRepositoryFactoryBean();
        factory.setEntityManager(entityManager);
        factory.setRepositoryInterface(repositoryClass);
        factory.setBeanFactory(context);
        factory.afterPropertiesSet();

        //TODO: out accessing thread
        return (R) factory.getObject();

//        JPARepositoryProxy2 proxy = injector.getInstance(JPARepositoryProxy2.class);
//        proxy.configure(repositoryClass, domainClass);
        //TODO: Caused by: java.lang.IllegalStateException: No persistence exception translators found in bean factory. Cannot perform exception translation.
        //http://stackoverflow.com/questions/8434712/no-persistence-exception-translators-found-in-bean-factory-cannot-perform-excep


        //TODO: получать бин из контекста спринга
//from doc
//        Class<? extends R> repositoryClass = (Class<? extends R>) this.repositoryClass;
//        RepositoryFactorySupport rfc = new JpaRepositoryFactory(entityManagerProvider.get());
//        return rfc.getRepository(repositoryClass);

/*

        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManagerProvider.get());
        Class<? extends R> repositoryClass = (Class<? extends R>) this.repositoryClass;

        R repository = jpaRepositoryFactory.getRepository(repositoryClass);
//        return invocation.getMethod().invoke(repository, invocation.getArguments());

*/
        // TODO штука в том, что в RepoBean попадает отдельный, свой собственный, EntityManager
//        return (R) repoBean.getObject();
//        return (R) Proxy.newProxyInstance(repositoryClass.getClassLoader(), new Class[]{repositoryClass}, proxy);
    }
}
