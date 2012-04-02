/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 02.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.common.jpa;

import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

public class GuiceLocalEntityManagerFactoryBean extends LocalEntityManagerFactoryBean {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final long serialVersionUID = -3873649039969409357L;
    private static final Logger logger = LoggerFactory.getLogger(GuiceLocalEntityManagerFactoryBean.class);

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Provider<EntityManagerFactory> entityManagerFactoryProvider;
    private Provider<EntityManager> entityManagerProvider;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public GuiceLocalEntityManagerFactoryBean(Provider<EntityManagerFactory> entityManagerFactoryProvider, Provider<EntityManager> entityManagerProvider) {
        this.entityManagerFactoryProvider = entityManagerFactoryProvider;
        this.entityManagerProvider = entityManagerProvider;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
        EntityManagerFactory emf = entityManagerFactoryProvider.get();
        EntityManager entityManager = entityManagerProvider.get();
        logger.info(String.format("Accessing: factory=[%d], em=[%d]", emf.hashCode(), entityManager.hashCode()));

//        return entityManagerFactoryProvider.get();
        return new EntityManaderFactoryProxy(entityManagerFactoryProvider.get(), entityManagerProvider);
    }

    @Override
    protected EntityManagerFactory createEntityManagerFactoryProxy(EntityManagerFactory emf) {
        EntityManagerFactory entityManagerFactoryProxy = super.createEntityManagerFactoryProxy(emf);
        TransactionSynchronizationManager.bindResource(entityManagerFactoryProxy, new EntityManagerHolder(entityManagerProvider.get()));
        return entityManagerFactoryProxy;
    }

    private static class EntityManaderFactoryProxy implements EntityManagerFactory {
        private EntityManagerFactory entityManagerFactory;
        private Provider<EntityManager> entityManager;

        private EntityManaderFactoryProxy(EntityManagerFactory entityManagerFactory, Provider<EntityManager> entityManager) {
            this.entityManagerFactory = entityManagerFactory;
            this.entityManager = entityManager;
        }

        public EntityManager createEntityManager() {
            EntityManager em = entityManager.get();
            logger.info("Create EM: "+em.hashCode());
            return em;
        }

        public EntityManager createEntityManager(Map map) {
            return entityManagerFactory.createEntityManager(map);
        }

        public CriteriaBuilder getCriteriaBuilder() {
            return entityManagerFactory.getCriteriaBuilder();
        }

        public Metamodel getMetamodel() {
            return entityManagerFactory.getMetamodel();
        }

        public boolean isOpen() {
            return entityManagerFactory.isOpen();
        }

        public void close() {
            entityManagerFactory.close();
        }

        public Map<String, Object> getProperties() {
            return entityManagerFactory.getProperties();
        }

        public Cache getCache() {
            return entityManagerFactory.getCache();
        }

        public PersistenceUnitUtil getPersistenceUnitUtil() {
            return entityManagerFactory.getPersistenceUnitUtil();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        logger.info("Destroy called");
    }

}
