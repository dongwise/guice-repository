/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 22.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.common.jpa;

import com.google.inject.Inject;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
class GenericJPARepository extends AbstractJPARepository {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private EntityManager entityManager;

    /*===========================================[ CONSTRUCTORS ]===============*/

    /**
     * Override Inject-by parent construction
     * @param entityManager
     */
    @Inject
    public void init(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    void configure(Class repositoryClass, Class domainClass) {
        configure(repositoryClass, domainClass, entityManager);
    }
}