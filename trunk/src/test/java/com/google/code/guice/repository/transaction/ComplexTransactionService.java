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
import com.google.inject.Provider;
import junit.framework.Assert;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class ComplexTransactionService {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Inject
    private UserRepository userRepository;

    @Inject
    private Provider<EntityManager> entityManager;

    /*===========================================[ CLASS METHODS ]==============*/

    @Transactional(rollbackFor = Exception.class)
    public void performFirstComplexTransaction() throws Exception {
        try {
            System.out.println("EM for performComplexTransaction: " + entityManager.get().hashCode());
            System.out.println("Delete");

            //TODO теряем транзакцию
            userRepository.deleteAll();
            System.out.println("Save1");
            userRepository.save(new User("John", "Smith", 42));
            System.out.println("Save2");
            userRepository.save(new User("Alex", "Johns", 22));
            System.out.println("Count1");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Assert.assertEquals("Invalid repository size", 2, userRepository.count());

        throw new Exception("Flow breaker1");
    }

    @Transactional(rollbackFor = Exception.class)
    public void performSecondComplexTransaction() throws Exception {
        System.out.println("EM for performSecondComplexTransaction: " + entityManager.get().hashCode());
        System.out.println("Save3");
        userRepository.save(new User("1", "1", 1));
        System.out.println("Count2");
        Assert.assertEquals("Invalid repository size", 1, userRepository.count());
        throw new Exception("Flow breaker2");
    }

}
