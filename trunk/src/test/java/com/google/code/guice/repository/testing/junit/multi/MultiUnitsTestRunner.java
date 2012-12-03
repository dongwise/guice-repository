/*
* Copyright (c) 2012, i-Free. All Rights Reserved.
*/

package com.google.code.guice.repository.testing.junit.multi;

import com.google.code.guice.repository.JpaRepositoryProvider;
import com.google.code.guice.repository.configuration.JpaRepositoryModule;
import com.google.code.guice.repository.testing.common.GuiceTestRunner;
import com.google.code.guice.repository.testing.repo.*;
import org.junit.runners.model.InitializationError;

/**
 * MultiUnitsTestRunner -
 *
 * @author Alexey Krylov (AleX)
 * @since 03.12.12
 */
public class MultiUnitsTestRunner extends GuiceTestRunner {
    /*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/
    public MultiUnitsTestRunner(Class<?> classToRun) throws InitializationError {
        super(classToRun, new JpaRepositoryModule("test-h2") {
                    @Override
                    protected void configureRepositories() {
                        bind(UserRepository.class).toProvider(new JpaRepositoryProvider<UserRepository>());
                        bind(AccountRepository.class).toProvider(new JpaRepositoryProvider<AccountRepository>());
                    }
                }, new JpaRepositoryModule("test-h2-secondary") {
                    @Override
                    protected void configureRepositories() {
                        bind(UserDataRepository.class).toProvider(new JpaRepositoryProvider<UserDataRepository>());
                    }
                }
        );
    }

}
