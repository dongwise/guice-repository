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

package com.google.code.guice.repository.testing.junit.multi;

import com.google.code.guice.repository.testing.model.User;
import com.google.code.guice.repository.testing.model.UserData;
import com.google.code.guice.repository.testing.repo.UserDataRepository;
import com.google.code.guice.repository.testing.repo.UserRepository;
import com.google.inject.Inject;
import org.springframework.transaction.annotation.Transactional;

/**
 * MultiUnitsService - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 04.12.12
 */
public class MultiPersistenceUnitsService {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserDataRepository userDataRepository;

    /*===========================================[ CLASS METHODS ]================*/

    @Transactional
    public void generateUsers(int count) {
        for (int i = 0; i < count; i++) {
            userRepository.save(new User("name" + i, "surname" + i, i));
        }
    }

    @Transactional("test-h2-secondary")
    public void generateUserData(int count) {
        for (int i = 0; i < count; i++) {
            userDataRepository.save(new UserData());
        }
    }
}
