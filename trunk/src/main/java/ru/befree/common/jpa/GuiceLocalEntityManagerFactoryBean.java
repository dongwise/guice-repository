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

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Provider<EntityManagerFactory> entityManagerFactoryProvider;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public GuiceLocalEntityManagerFactoryBean(Provider<EntityManagerFactory> entityManagerFactoryProvider) {
        this.entityManagerFactoryProvider = entityManagerFactoryProvider;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
        return entityManagerFactoryProvider.get();
    }
}