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

package com.google.code.guice.repository.mapping;

import com.google.inject.Provider;
import org.springframework.context.ApplicationContext;

/**
 * Creates a Spring-context for spring-data-jpa.
 *
 * @author Alexey Krylov
 * @version 1.0.1
 * @since 07.11.2012
 */
public class ApplicationContextProvider implements Provider<ApplicationContext> {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private ApplicationContext context;

    /*===========================================[ CONSTRUCTORS ]===============*/

/*
    @Inject
    public void init(TransactionInterceptor transactionInterceptor,
                     @Named(JpaRepositoryModule.P_PERSISTENCE_UNITS) String persistenceUnitName,
                     @Named(JpaRepositoryModule.P_PERSISTENCE_UNIT_PROPERTIES) Properties props) {
        GenericApplicationContext context = new GenericApplicationContext();
        //http://blog.springsource.org/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/#comment-198835
        //TODO customization for dialect & etc
        context.registerBeanDefinition("entityManagerFactoryBean",
                BeanDefinitionBuilder.genericBeanDefinition(LocalEntityManagerFactoryBean.class).
                        addPropertyValue("persistenceUnitName", persistenceUnitName).
                        addPropertyValue("jpaProperties", props).getBeanDefinition());

        context.registerBeanDefinition("transactionManager",
                BeanDefinitionBuilder.genericBeanDefinition(JpaTransactionManager.class).getBeanDefinition());

        context.registerBeanDefinition("jpaRepositoryFactoryBean",
                BeanDefinitionBuilder.genericBeanDefinition(JpaRepositoryFactoryBean.class).getBeanDefinition());

        //TODO: use add advice
        JpaTransactionManager transactionManager = context.getBean(JpaTransactionManager.class);
        //transactionInterceptor.setTransactionManager(transactionManager);
        transactionInterceptor.setBeanFactory(context);
        //transactionInterceptor.afterPropertiesSet();
        this.context = context;
    }

*/
    /*===========================================[ CLASS METHODS ]==============*/

    @Override
    public ApplicationContext get() {
        return context;
    }
}