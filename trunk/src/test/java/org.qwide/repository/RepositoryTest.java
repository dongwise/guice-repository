/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 19.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.qwide.repository;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

@RunWith(RepoTestRunner.class)
public class RepositoryTest {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/

//    @Inject
//    private CustomerRepository customerRepository;

//    @Inject
//    private AccountRepository accountRepository;

    @Inject
    private Provider<UserRepository> userRepositoryProvider;

    @Test
    public void testRepo() throws Exception {
/*
        long count = customerRepository.count();
        customerRepository.save(new Customer("first", "second"));
        assertEquals(1, customerRepository.count());

        Page<Customer> all = customerRepository.findAll(new PageRequest(0, 100));
        assertEquals(1, all.getNumberOfElements());
*/

        UserRepository userRepository = userRepositoryProvider.get();
//        assertEquals(0, accountRepository.count());
        userRepository.deleteAll();
        assertEquals(0, userRepository.count());

        userRepository.save(new User("john", "smith", 42));
        userRepository.save(new User("alex", "johns", 33));
        userRepository.save(new User("sam", "brown", 22));
        assertEquals(3, userRepositoryProvider.get().count());

        Page<User> users = userRepository.findAll(new PageRequest(0, 100));
        assertEquals(3, users.getNumberOfElements());



        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.invokeAll(Arrays.asList(Executors.callable(new Runnable() {
            public void run() {
                try {
                    System.out.println("Start concurrent thread");
                    UserRepository anotherRepo = userRepositoryProvider.get();
                    System.out.println("count");
                    assertEquals(3, anotherRepo.count());
                    System.out.println("save");
                    anotherRepo.save(new User(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 10));
                    assertEquals(4, anotherRepo.count());
                    System.out.println("Stored 4");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })));

        System.out.println("After");
        assertEquals(4, userRepository.count());
        userRepository.deleteAll();
        assertEquals(0, userRepository.count());

//        accountRepository.save(new Account(UUID.randomUUID().toString()));
//        assertEquals(1, accountRepository.count());

//        accountRepository.deleteAll();
//        assertEquals(0, accountRepository.count());

        Collection<Account> accounts = new LinkedList<Account>();
        for (int i = 0; i < 10; i++) {
            accounts.add(new Account(String.valueOf(i)));
        }
//        accountRepository.storeInBatch(accounts);
//        assertEquals(10, accountRepository.count());

//        accountRepository.findAccountByUuid(UUID.randomUUID());
    }
}
