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
import org.apache.commons.lang.Validate;
import org.springframework.data.repository.Repository;

import java.lang.reflect.Proxy;

@Singleton
public class JPARepositoryProvider<R extends Repository> implements Provider<R> {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Injector injector;

    private Class<? super R> repositoryClass;
    private Class domainClass;

    /*===========================================[ CLASS METHODS ]==============*/

    @Override
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
        JPARepositoryProxy proxy = injector.getInstance(JPARepositoryProxy.class);
        proxy.configure(repositoryClass, domainClass);
        return (R) Proxy.newProxyInstance(repositoryClass.getClassLoader(), new Class[]{repositoryClass}, proxy);
    }
}
