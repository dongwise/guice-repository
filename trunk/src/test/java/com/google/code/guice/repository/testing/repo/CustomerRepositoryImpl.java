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

package com.google.code.guice.repository.testing.repo;

import com.google.code.guice.repository.SimpleBatchStoreJpaRepository;
import com.google.code.guice.repository.testing.model.Customer;
import org.junit.Assert;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class CustomerRepositoryImpl extends SimpleBatchStoreJpaRepository<Customer,Long> implements CustomerRepository {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    @Inject
    private EntityManager entityManager;

    /*===========================================[ CONSTRUCTORS ]=================*/

    public CustomerRepositoryImpl(Class<Customer> domainClass, EntityManager em) {
        super(domainClass, em);
    }
    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public void sharedCustomMethod(Long customerID) {
        Assert.assertNotNull("CustomerID is null", customerID);
        Assert.assertNotNull("Injected entityManager is null", entityManager);
        System.out.println("customerID = " + customerID);
    }
}
