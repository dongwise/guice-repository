/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 19.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository.test;

import org.guice.common.GuiceTestRunner;
import org.guice.repository.JpaPersistenceModule;
import org.guice.repository.JpaRepositoryProvider;
import org.junit.runners.model.InitializationError;


public class RepoTestRunner extends GuiceTestRunner {

    /*===========================================[ CLASS METHODS ]==============*/

    public RepoTestRunner(Class<?> classToRun) throws InitializationError {
        super(classToRun, new JpaPersistenceModule() {
            @Override
            protected void configureRepositories() {
                bind(UserRepository.class).toProvider(new JpaRepositoryProvider<UserRepository>(UserRepositoryCustomImpl.class));
            }
        });
//        super(classToRun, new ScanningJpaPersistenceModule("org.guice.repository.test"));
//        super(classToRun, new ScanningJpaPersistenceModule("org.guice.repository.test"));
    }
}
