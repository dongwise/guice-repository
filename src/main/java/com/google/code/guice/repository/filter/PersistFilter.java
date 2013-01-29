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

package com.google.code.guice.repository.filter;

import com.google.code.guice.repository.configuration.PersistenceUnitConfiguration;
import com.google.code.guice.repository.configuration.PersistenceUnitsConfigurationManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.jcip.annotations.ThreadSafe;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * <b>guice-repository</b> adapted version of {@link OpenEntityManagerInViewFilter}.
 * This filter binds a JPA EntityManager to the thread for the entire processing of the request.
 * Intended for the "Open EntityManager in View"/<a href="http://code.google.com/p/google-guice/wiki/JPA">session-in-view/session-per-http-request</a>
 * pattern, i.e. to allow for lazy loading in web views despite the original transactions already being completed.
 * </p>
 * <p> To be able to use mentioned above pattern,
 * register this filter <b>once</b> in your Guice {@code ServletModule}. It is
 * important that you register this filter before any other filter.
 * <p/>
 * Example configuration:
 * <pre>{@code
 *  public class MyModule extends ServletModule {
 *    public void configureServlets() {
 *      filter("/*").through(PersistFilter.class);
 *      serve("/index.html").with(MyHtmlServlet.class);
 *      // Etc.
 *    }
 *  }
 * }</pre>
 * <p>
 * For single persistence unit you can configure your filter with {@link #setPersistenceUnitName(String)} (through
 * web.xml/programmatically).
 * Also you can just skip this parameter - in this case default persistence ({@link
 * PersistenceUnitsConfigurationManager#getDefaultConfiguration()} unit configuration will be used.
 * <p/>
 * <p>
 * For multiple persistence units, you should register your custom filter before this filter.
 * Your custom filter should set parameter {@link #P_PERSISTENCE_UNIT_NAME} into {@link HttpServletRequest}.
 * </p>
 * <p>
 * This filter requires the <a href="http://code.google.com/p/google-guice/wiki/Servlets">Guice Servlet</a> extension.
 * </p>
 *
 * @author Alexey Krylov
 * @see OpenEntityManagerInViewFilter
 * @see <a href="http://code.google.com/p/google-guice/wiki/ServletModule">ServletModule</a>
 * @since 08.12.12
 */
@Singleton
@ThreadSafe
public class PersistFilter extends OpenEntityManagerInViewFilter {

    /**
     * This HttpServletRequest parameter describes persistence unit to use.
     * Use means EntityManager opening/closing in doFilter try/finally.
     */
    public static final String P_PERSISTENCE_UNIT_NAME = "persistenceUnitName";

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    @Inject
    private PersistenceUnitsConfigurationManager configurationManager;

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    protected EntityManagerFactory lookupEntityManagerFactory(HttpServletRequest request) {
        // check for Filter configuration parameter presence
        String persistenceUnitName = getPersistenceUnitName();
        if (persistenceUnitName == null) {
            persistenceUnitName = request.getParameter(P_PERSISTENCE_UNIT_NAME);
        }
        // persistenceUnitName can still be null - in this case default configuration will be used
        return configurationManager.getConfiguration(persistenceUnitName).getEntityManagerFactory();
    }

    @Override
    protected EntityManager createEntityManager(EntityManagerFactory emf) {
        PersistenceUnitConfiguration configuration = configurationManager.getConfiguration(getPersistenceUnitName());
        EntityManager entityManager = emf.createEntityManager(configuration.getProperties());
        configurationManager.changeEntityManager(getPersistenceUnitName(), entityManager);
        return entityManager;
    }
}