/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 03.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.qwide.repository;

import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@RunWith(RepoTestRunner.class)
public class MultiThreadedRepositoryTest {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final int MAX_CONCURRENT_THREADS = 100;

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Injector injector;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        Collection<Callable<Object>> callables = new ArrayList<Callable<Object>>();

        Runnable repoExploiter = new Runnable() {
            public void run() {
                UserRepository instance = injector.getInstance(UserRepository.class);
                instance.save(new User(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 1));
            }
        };
        for (int i = 0; i < MAX_CONCURRENT_THREADS; i++) {
            callables.add(Executors.callable(repoExploiter));
        }

        Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS).invokeAll(callables);

        Assert.assertEquals("Invalid entities count", MAX_CONCURRENT_THREADS, injector.getInstance(UserRepository.class).count());
    }
}
