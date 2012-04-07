/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 07.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository.test;

import org.guice.repository.test.model.Account;
import org.guice.repository.test.model.Customer;
import org.guice.repository.test.model.User;
import org.guice.repository.test.runner.AutoBindRepoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AutoBindRepoTestRunner.class)
public class AutoBindRepositoryTest {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private UserRepository userRepository;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private CustomerRepository customerRepository;

    /*===========================================[ CLASS METHODS ]==============*/

    @Before
    public void cleanup(){
        userRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    public void testUserRepository(){
        userRepository.save(new User("name", "surname", 42));
        assertEquals(1, userRepository.count());
        userRepository.deleteOtherUsers();
        assertEquals(0, userRepository.count());
    }

    @Test
    public void testAccountRepository(){
        accountRepository.deleteAll();
        String uuid = UUID.randomUUID().toString();
        accountRepository.save(new Account(uuid, "lexx"));
        assertEquals(1, accountRepository.count());
        assertNotNull(accountRepository.findAccountByName("lexx"));
        assertNotNull(accountRepository.findAccountByUuid(uuid));
        accountRepository.deleteAll();
        assertEquals(0, accountRepository.count());
    }

    @Test
    public void testCustomerRepository(){
        customerRepository.save(new Customer("name", "surname"));
        customerRepository.sharedCustomMethod(new Long(42));
        assertEquals(1, customerRepository.count());
        customerRepository.deleteAll();
        assertEquals(0, customerRepository.count());
    }
}
