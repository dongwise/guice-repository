/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 18.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.common.jpa;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

public abstract class JPAPersistenceModule extends AbstractModule {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(JPAPersistenceModule.class);
    public static final String P_PERSISTENCE_UNIT_NAME = "persistence-unit-name";

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private String jpaUnitName;

    /*===========================================[ CONSTRUCTORS ]===============*/

    protected JPAPersistenceModule(String... jpaUnitName) {
        if (jpaUnitName.length > 0) {
            this.jpaUnitName = jpaUnitName[0];
        } else {
            this.jpaUnitName = System.getProperty(P_PERSISTENCE_UNIT_NAME);
            if (this.jpaUnitName == null) {
                throw new IllegalStateException("Unable to instantiate JPAPersistenceModule: no jpaUnitName specified");
            }
        }
    }

    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    protected void configure() {
        logger.info(String.format("Configuring persistence with JPA unit name: [%s]", jpaUnitName));

        Properties props = new Properties();
        String propFileName = jpaUnitName + ".properties";

        InputStream fin = getClass().getClassLoader().getResourceAsStream(propFileName);
        if (fin != null) {
            try {
                props.load(fin);
            } catch (Exception e) {
                throw new RuntimeException("Error reading properties file:" + propFileName);
            }
        } else {
            throw new RuntimeException("Properties file not found:" + propFileName);
        }

        JpaPersistModule module = new JpaPersistModule(jpaUnitName);
        // Передаем параметры инициализации Persistence-контекста
        module.properties(props);
        install(module);

        bind(JPAInitializer.class).asEagerSingleton();


//        MethodInterceptor interceptor = extractInterceptor(module);
//        bindInterceptor(Matchers.annotatedWith(Transactional.class), Matchers.any(), interceptor);
//        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), interceptor);
/*
        try {
            bindInterceptor(Matchers.annotatedWith(Repository.class),
                    Matchers.not(Matchers.identicalTo(JPARepositoryProxy2.class.getMethod("invoke", Object.class, Method.class, Object[].class))),
                    new RepoMagicInterceptor(getProvider(EntityManager.class)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
*/
//TODO: RepositoryFactorySupport.QueryExecutorMethodInterceptor

        bind(GenericJPARepository.class);
        bind(JPARepositoryProxy.class);
        bind(JPARepositoryProxy2.class);
        configureRepositories();
        logger.info("Persistence configured");
    }

    private MethodInterceptor extractInterceptor(JpaPersistModule module) {
        try {
            Class moduleClass = module.getClass();
            Method method = moduleClass.getDeclaredMethod("getTransactionInterceptor");
            method.setAccessible(true);
            return (MethodInterceptor) method.invoke(module);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to get transaction interceptor for JPAPersistenceModule instantiation", e);
        }
    }

    protected abstract void configureRepositories();
}
