/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.testing.runner;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Arrays;


/**
 * TestInterceptor - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 09.12.12
 */
public class TestInterceptor implements MethodInterceptor {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]===========*/
/*===========================================[ CONSTRUCTORS ]=================*/
/*===========================================[ CLASS METHODS ]================*/

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        System.out.println(String.format("method: [%s], args: ([%s]), this: [%s]",
                invocation.getMethod().getName(),
                arguments !=null? Arrays.asList(arguments):"void",
                invocation.getThis()));
        return invocation.proceed();
    }
}
