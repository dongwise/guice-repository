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

package com.googlecode.guicerepository;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PersistService starter, which is needed for Guice-Persist module.
 *
 * @author Alexey Krylov AKA lexx
 */
class JpaInitializer {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(JpaInitializer.class);

    /*===========================================[ CONSTRUCTORS ]===============*/

    @Inject
    public void init(PersistService service) {
        try {
            service.start();
            logger.debug("PersistService started");
        } catch (Exception e) {
            logger.error("Error", e);
            throw new RuntimeException(e);
        }
    }
}
