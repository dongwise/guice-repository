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

package com.google.code.guice.repository.transaction;

import com.google.code.guice.repository.model.User;
import com.google.code.guice.repository.repo.UserRepository;
import junit.framework.Assert;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class ComplexTransactionService {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private UserRepository userRepository;

    @Inject
    private EntityManager entityManager;

    /*===========================================[ CLASS METHODS ]==============*/

    @Transactional(rollbackFor = Exception.class)
    public void performComplexTransaction() throws Exception {
        userRepository.deleteAll();
        userRepository.save(new User("John", "Smith", 42));
        userRepository.save(new User("Alex", "Johns", 22));
        long count = userRepository.count();
        Assert.assertEquals("Invalid repository size", 2, count);
        throw new Exception("Flow breaker");
    }
}
