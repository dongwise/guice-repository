/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.support;

import com.google.code.guice.repository.configuration.PersistenceUnitsConfigurationManager;
import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * CompositeTransactionInterceptor - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 07.12.12
 */
public class CompositeTransactionInterceptor implements MethodInterceptor {
    /*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]===========*/
    @Inject
    private PersistenceUnitsConfigurationManager configurationManager;
/*===========================================[ CONSTRUCTORS ]=================*/
/*===========================================[ CLASS METHODS ]================*/

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //TODO: unitName
        return invocation.proceed();
    }
}
