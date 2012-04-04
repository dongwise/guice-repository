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

    @Inject
    private Provider<EntityManager> entityManagerProvider;

    /*===========================================[ CLASS METHODS ]==============*/

    @Test
    public void testRepo() throws Exception {

        int batchSize = 1000;
        int iterationsCount = 1000;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                AccountRepository accountRepository = accountRepositoryProvider.get();
                System.out.println(String.format("Current count: [%d], free memory: [%d] mb", accountRepository.count(),
                        Runtime.getRuntime().freeMemory() / (1024 * 1024)));
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));

        AccountRepository accountRepository = accountRepositoryProvider.get();
        EntityManager entityManager = entityManagerProvider.get();
        for (int i = 0; i < iterationsCount; i++) {
            List<Account> accounts = generateBatch(batchSize);
//            List<Account> saved = accountRepository.save(accounts);
            accountRepository.saveInBatch(accounts);
/*
            for (Account account : saved) {
                entityManager.detach(account);
            }
*/
//            accountRepository.flush();
        }

        assertEquals("Invalid stored entities count", iterationsCount * batchSize, accountRepository.count());
    }

    private List<Account> generateBatch(int batchSize) {
        List<Account> accounts = new LinkedList<Account>();
        for (int i = 0; i < batchSize; i++) {
            accounts.add(new Account(UUID.randomUUID().toString(), String.valueOf(i)));
        }
        return accounts;
    }
}