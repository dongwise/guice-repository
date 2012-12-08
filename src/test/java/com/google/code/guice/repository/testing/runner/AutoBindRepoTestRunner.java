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

import com.google.code.guice.repository.configuration.RepositoriesGroupBuilder;
import com.google.code.guice.repository.configuration.ScanningJpaRepositoryModule;
import com.google.code.guice.repository.testing.common.GuiceTestRunner;
import com.google.code.guice.repository.testing.repo.UserDataRepository;
import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import org.junit.runners.model.InitializationError;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AutoBindRepoTestRunner extends GuiceTestRunner {

    /*===========================================[ CLASS METHODS ]==============*/

    public AutoBindRepoTestRunner(Class<?> classToRun) throws InitializationError {
        super(classToRun, new ScanningJpaRepositoryModule(
                Arrays.asList(
                      RepositoriesGroupBuilder.forPackage("com.google.code.guice.repository.testing.repo").
                                withExclusionPattern(".*" + UserDataRepository.class.getSimpleName() + ".*").
                                attachedTo("test-h2").
                                build(),

                        RepositoriesGroupBuilder.forPackage("com.google.code.guice.repository.testing.repo").
                                withInclusionFilterPredicate(new Predicate<Class>() {
                                    @Override
                                    public boolean apply(@Nullable Class input) {
                                        return UserDataRepository.class.isAssignableFrom(input);
                                    }
                                }).
                                attachedTo("test-h2-secondary").
                                build()
                )){
            @Override
            protected Map<String, Object> getAdditionalEMFProperties(String persistenceUnitName) {
                Map<String, Object> properties = new HashMap<String, Object>();
                if ("test-h2".equals(persistenceUnitName)){
                    properties.put("jpaDialect", new HibernateJpaDialect());
                    return properties;

                }
                return properties;
            }
        }, new AbstractModule() {
                    @Override
                    protected void configure() {
                        bindInterceptor(Matchers.any(), Matchers.any(), new TestInterceptor());
                    }
                });
    }
}