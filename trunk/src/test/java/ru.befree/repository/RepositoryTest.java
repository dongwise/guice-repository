/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 19.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.repository;

import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(RepoTestRunner.class)
public class RepositoryTest {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/

    @Inject
    private CustomerRepository customerRepository;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private UserRepository userRepository;

    @Test
    public void testRepo() {
        long count = customerRepository.count();
        customerRepository.save(new Customer("first", "second"));
        assertEquals(1, customerRepository.count());

        Page<Customer> all = customerRepository.findAll(new PageRequest(0, 100));
        assertEquals(1, all.getNumberOfElements());

        assertEquals(0, accountRepository.count());
        assertEquals(0, userRepository.count());

        userRepository.save(new User(UUID.randomUUID().toString()));
        assertEquals(1, userRepository.count());

        accountRepository.save(new Account(UUID.randomUUID().toString()));
        assertEquals(1, accountRepository.count());

        accountRepository.deleteAll();
        assertEquals(0, accountRepository.count());

        Collection<Account> accounts = new LinkedList<Account>();
        for (int i = 0; i < 10; i++) {
            accounts.add(new Account(String.valueOf(i)));
        }
        accountRepository.storeInBatch(accounts);
        assertEquals(10, accountRepository.count());

        accountRepository.generateAccount(UUID.randomUUID().toString());

    }
}
