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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(RepoTestRunner.class)
public class BatchStoreRepositoryTest {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private Provider<AccountRepository> accountRepositoryProvider;
    private Timer timer;

    /*===========================================[ CLASS METHODS ]==============*/

    @Before
    public void before() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EntityManager entityManager = accountRepositoryProvider.get().getEntityManager();
                System.out.println(String.format("Current count: [%d], free memory: [%d] mb", accountRepositoryProvider.get().count(),
                        Runtime.getRuntime().freeMemory() / (1024 * 1024)));
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));
    }

    @After
    public void after() {
        AccountRepository accountRepository = accountRepositoryProvider.get();
        accountRepository.deleteAll();
        assertEquals(0, accountRepository.count());
        timer.cancel();
    }

    @Test
    public void testDefaultBatchSave() throws Exception {

        int batchSize = 1000;
        int iterationsCount = 1000;

        AccountRepository accountRepository = accountRepositoryProvider.get();
        for (int i = 0; i < iterationsCount; i++) {
            List<Account> accounts = generateBatch(batchSize);
            accountRepository.saveInBatch(accounts);
        }

        assertEquals("Invalid stored entities count", iterationsCount * batchSize, accountRepository.count());
    }

    @Test
    public void testPartialBatchSave() throws Exception {
        AccountRepository accountRepository = accountRepositoryProvider.get();

        int totalSize = 10000;

        List<Account> accounts = generateBatch(totalSize);
        accountRepository.saveInBatch(accounts, 100);

        assertEquals("Invalid stored entities count", totalSize, accountRepository.count());
    }

    private List<Account> generateBatch(int batchSize) {
        List<Account> accounts = new LinkedList<Account>();
        for (int i = 0; i < batchSize; i++) {
            accounts.add(new Account(UUID.randomUUID().toString(), String.valueOf(i)));
        }
        return accounts;
    }
}