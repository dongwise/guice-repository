/**
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

package com.google.code.guice.repository.junit;

import com.google.code.guice.repository.runner.ManualBindRepoTestRunner;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@RunWith(ManualBindRepoTestRunner.class)
public abstract class RepoTestBase {

    /*===========================================[ STATIC VARIABLES ]=============*/

    protected static final int MAX_CONCURRENT_THREADS = 10;

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    protected Logger logger;

    @Inject
    protected Injector injector;

    /*===========================================[ CLASS METHODS ]==============*/

    @Before
    public void beforeClass() {
        logger = LoggerFactory.getLogger(getClass());

    }
}