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
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;

import javax.persistence.EntityManager;

public class JPARepositoryProvider2<R extends Repository> implements Provider<R> {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Injector injector;

    private Class<? super R> repositoryClass;
    private Class domainClass;

    @Inject
    private Provider<EntityManager> entityManagerProvider;

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
            domainClass = DomainClassExtractor.extact(repositoryClass);
        }

        System.out.println("repoClass = " + repositoryClass);
        //TODO: сделать перехватчик отслеживающий все вызовы @Repository - если метода нет, то вызов соответствующего шаманства по генерации JPQL
        //TODO:
        /** TODO
         * @Query
         @Param
         */
        //TODO: assisted inject
//        JPARepositoryProxy2 proxy = injector.getInstance(JPARepositoryProxy2.class);
//        proxy.configure(repositoryClass, domainClass);
        //TODO: Caused by: java.lang.IllegalStateException: No persistence exception translators found in bean factory. Cannot perform exception translation.
        JpaRepositoryFactoryBean bean = new JpaRepositoryFactoryBean();
        bean.setEntityManager(entityManagerProvider.get());
        bean.setRepositoryInterface(repositoryClass);
        bean.setBeanFactory(new GenericApplicationContext());
//        bean.setCustomImplementation();
        bean.afterPropertiesSet();

/*

        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManagerProvider.get());
        Class<? extends R> repositoryClass = (Class<? extends R>) this.repositoryClass;

        R repository = jpaRepositoryFactory.getRepository(repositoryClass);
//        return invocation.getMethod().invoke(repository, invocation.getArguments());

*/
        return (R) bean.getObject();
//        return (R) Proxy.newProxyInstance(repositoryClass.getClassLoader(), new Class[]{repositoryClass}, proxy);
    }
}
