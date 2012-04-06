/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 04.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository.test;

import org.guice.repository.SimpleBatchStoreJpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.EntityManager;

public class UserRepositoryCustomImpl extends SimpleBatchStoreJpaRepository<User, Long> implements UserRepositoryCustom{
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
/*===========================================[ CONSTRUCTORS ]===============*/

    public UserRepositoryCustomImpl(Class<User> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }

    public UserRepositoryCustomImpl(JpaEntityInformation<User, Long> entityMetadata, EntityManager entityManager) {
        super(entityMetadata, entityManager);
    }
    /*===========================================[ CLASS METHODS ]==============*/

    public void someCustomMethod(User user) {
        System.out.println("user = " + user);
    }
}
