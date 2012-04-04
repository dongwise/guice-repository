/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 04.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository.test;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/

    public void someCustomMethod(User user) {
        System.out.println("user = " + user);
    }
}
