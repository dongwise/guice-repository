/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 19.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.qwide.repository;

import org.junit.runners.model.InitializationError;
import org.qwide.common.GuiceTestRunner;


public class RepoTestRunner extends GuiceTestRunner {

    /*===========================================[ CLASS METHODS ]==============*/

    public RepoTestRunner(Class<?> classToRun) throws InitializationError {
        super(classToRun, new JPAPersistenceModule() {
            @Override
            protected void configureRepositories() {
                bind(UserRepository.class).toProvider(new JPARepositoryProvider<UserRepository>());
                bind(AccountRepository.class).toProvider(new JPARepositoryProvider<AccountRepository>());
            }
        });
    }
}
