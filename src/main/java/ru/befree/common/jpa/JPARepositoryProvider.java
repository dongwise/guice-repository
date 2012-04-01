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
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class JPARepositoryProvider<R extends Repository> implements Provider<R> {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Injector injector;

    private Class<? super R> repositoryClass;

    @Inject
    private Provider<EntityManager> entityManagerProvider;


    @Inject
    private Provider<EntityManagerFactory> entityManagerFactoryProvider;

    /*===========================================[ CLASS METHODS ]==============*/

    public R get() {
        /**
         * Injector Reverse lookup - find binding Key information for this Provider instance
         */
        if (repositoryClass == null) {
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
        }

        System.out.println("repoClass = " + repositoryClass);

//        JPARepositoryProxy2 proxy = injector.getInstance(JPARepositoryProxy2.class);
//        proxy.configure(repositoryClass, domainClass);
        //TODO: Caused by: java.lang.IllegalStateException: No persistence exception translators found in bean factory. Cannot perform exception translation.
        //http://stackoverflow.com/questions/8434712/no-persistence-exception-translators-found-in-bean-factory-cannot-perform-excep

        JpaRepositoryFactoryBean repoBean = new JpaRepositoryFactoryBean();
        repoBean.setEntityManager(entityManagerProvider.get());
        repoBean.setRepositoryInterface(repositoryClass);
        GenericApplicationContext beanFactory = new GenericApplicationContext();
        //TODO: for custom implementation
//        bean.setCustomImplementation();

        GenericBeanDefinition emBean = new GenericBeanDefinition();
        emBean.setBeanClass(GuiceLocalEntityManagerFactoryBean.class);
        emBean.setConstructorArgumentValues(new ConstructorArgumentValues() {
            {
                addGenericArgumentValue(entityManagerFactoryProvider);
            }
        });
        beanFactory.registerBeanDefinition("entityManagerFactory", emBean);

        GenericBeanDefinition tmBean = new GenericBeanDefinition();
        tmBean.setBeanClass(JpaTransactionManager.class);
        beanFactory.registerBeanDefinition("transactionManager", tmBean);

        repoBean.setBeanFactory(beanFactory);
        repoBean.afterPropertiesSet();

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
        return (R) repoBean.getObject();
//        return (R) Proxy.newProxyInstance(repositoryClass.getClassLoader(), new Class[]{repositoryClass}, proxy);
    }
}
