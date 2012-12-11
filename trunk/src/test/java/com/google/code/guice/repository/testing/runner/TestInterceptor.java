/*
 * Copyright (C) 2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.guice.repository.testing.runner;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;


/**
 * Sample interceptor bound to all calls fo Repository methods annotated with {@link Transactional}.
 *
 * @author Alexey Krylov
 * @since 09.12.12
 */
public class TestInterceptor implements MethodInterceptor {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(TestInterceptor.class);

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        logger.info(String.format("method: [%s], args: ([%s]), this: [%s]",
                invocation.getMethod().getName(),
                arguments != null ? Arrays.asList(arguments) : "void",
                invocation.getThis()));
        return invocation.proceed();
    }
}
