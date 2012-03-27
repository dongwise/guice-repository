/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 19.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.repository;

import org.springframework.stereotype.Repository;
import ru.befree.common.jpa.AbstractJPARepository;

@Repository
public class JPACustomerRepository extends AbstractJPARepository<Customer, Long> implements CustomerRepository {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
/*===========================================[ CONSTRUCTORS ]===============*/

    @Override
    public void getSome() {
        System.out.println("SOME");
    }

    /*===========================================[ CLASS METHODS ]==============*/

}
