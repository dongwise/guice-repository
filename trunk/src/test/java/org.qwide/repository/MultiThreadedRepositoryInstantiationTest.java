/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 03.04.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.qwide.repository;

import com.google.inject.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(RepoTestRunner.class)
public class MultiThreadedRepositoryInstantiationTest {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final int MAX_CONCURRENT_THREADS = 1000;

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Injector injector;
    private AtomicLong instanceCounter;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        instanceCounter = new AtomicLong();
        Collection<Callable<Object>> callables = new ArrayList<Callable<Object>>();

        Runnable repoCreator = new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()){
                    UserRepository instance = injector.getInstance(UserRepository.class);
                    instanceCounter.incrementAndGet();
                }
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS);
        for (int i = 0; i < MAX_CONCURRENT_THREADS; i++) {
            executorService.execute(repoCreator);
        }

        System.out.println("Awaiting for generation");

        int testTimeSeconds = 10;
        TimeUnit.SECONDS.sleep(testTimeSeconds);
        executorService.shutdown();
        System.out.println(String.format("Speed: [%d] instances/sec", instanceCounter.get()/testTimeSeconds));
    }
}
