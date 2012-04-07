/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 19.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository.test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.guice.repository.test.model.User;
import org.guice.repository.test.runner.ManualBindRepoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

@RunWith(ManualBindRepoTestRunner.class)
public class UserRepositoryTest {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Provider<UserRepository> userRepositoryProvider;

    /*===========================================[ CLASS METHODS ]==============*/

    @Before
    public void cleanup() {
        userRepositoryProvider.get().deleteAll();
    }


    @Test
    public void testRepo() throws Exception {
        UserRepository userRepository = userRepositoryProvider.get();
        userRepository.someCustomMethod(new User("one", "two", 42));

        userRepository.deleteInactiveUsers();
        userRepository.deleteOtherUsers();

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

        userRepository.someCustomMethod(new User("john", "smith", 42));
    }
}
