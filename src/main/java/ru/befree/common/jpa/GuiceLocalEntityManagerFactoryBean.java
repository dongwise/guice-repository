/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 02.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.common.jpa;

import com.google.inject.Provider;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

public class GuiceLocalEntityManagerFactoryBean extends LocalEntityManagerFactoryBean {
    private static final long serialVersionUID = -3873649039969409357L;
    /*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
    private Provider<EntityManagerFactory> entityManagerFactoryProvider;
    private Provider<EntityManager> entityManagerProvider;

/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/

    public GuiceLocalEntityManagerFactoryBean(Provider<EntityManagerFactory> entityManagerFactoryProvider, Provider<EntityManager> entityManagerProvider) {
        this.entityManagerFactoryProvider = entityManagerFactoryProvider;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
        EntityManagerFactory emf = entityManagerFactoryProvider.get();
        System.out.println(String.format("Accessing for emf: [%s], [%d], [%d], new [%d]", Thread.currentThread().getName(), emf.hashCode(), emf.createEntityManager().hashCode(), entityManagerProvider.get().hashCode()));
        return new EntityManaderFactoryProxy(entityManagerFactoryProvider.get(), entityManagerProvider.get());
    }


    private static class EntityManaderFactoryProxy implements EntityManagerFactory{
        private EntityManagerFactory entityManagerFactory;
        private EntityManager entityManager;

        private EntityManaderFactoryProxy(EntityManagerFactory entityManagerFactory, EntityManager entityManager) {
            this.entityManagerFactory = entityManagerFactory;
            this.entityManager = entityManager;
        }

        public EntityManager createEntityManager() {
            return entityManager;
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
}
