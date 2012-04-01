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

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

public class GuiceLocalEntityManagerFactoryBean extends LocalEntityManagerFactoryBean {
    private static final long serialVersionUID = -3873649039969409357L;
    /*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
    private Provider<EntityManagerFactory> entityManagerFactory;

/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/

    public GuiceLocalEntityManagerFactoryBean(Provider<EntityManagerFactory> entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
        EntityManagerFactory emf = entityManagerFactory.get();
        System.out.println(String.format("Accessing for emf: [%s], [%d], [%d]", Thread.currentThread().getName(), emf.hashCode(), emf.createEntityManager().hashCode()));
        new Exception("BLABLAOUT").printStackTrace();
        return emf;
    }
}
