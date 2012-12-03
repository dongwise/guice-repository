/*
* Copyright (c) 2012, i-Free. All Rights Reserved.
*/

package com.google.code.guice.repository.testing.junit.multi;

import com.google.code.guice.repository.testing.model.Account;
import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.model.UserData;
import com.google.code.guice.repository.testing.repo.AccountRepository;
import com.google.code.guice.repository.testing.repo.UserDataRepository;
import com.google.code.guice.repository.testing.repo.UserRepository;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

/**
 * MultiDatasourceTest -
 *
 * @author Alexey Krylov (AleX)
 * @since 02.12.12
 */
@RunWith(MultiUnitsTestRunner.class)
public class MultiUnitsTest {
    /*===========================================[ STATIC VARIABLES ]=============*/

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private UserRepository userRepository;

    @Inject
    private AccountRepository accountRepository;


    @Inject
    private UserDataRepository userDataRepository;


/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/


    @Test
    public void complexTest() {
        //TODO
        userRepository.save(new User("user", "surname", 1));
        accountRepository.save(new Account(UUID.randomUUID().toString(), "surname"));
        userDataRepository.save(new UserData(UUID.randomUUID().toString()));
    }
}
