/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 04.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository.test;

import org.guice.repository.test.model.User;
import org.junit.Assert;

public class UserRepositoryCustomImpl implements  UserRepositoryCustom {

    /*===========================================[ CLASS METHODS ]==============*/

    public void someCustomMethod(User user) {
        Assert.assertNotNull(user);
        System.out.println("user = " + user);
    }
}