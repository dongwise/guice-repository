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

package com.google.code.guice.repository.configuration;

import com.google.common.base.Predicate;
import net.jcip.annotations.NotThreadSafe;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Builds instance of {@link RepositoriesGroup}.
 * Examples:
 * <pre>
 *     RepositoriesGroupBuilder.forPackage("com.mycorp.repo").
 *             withExclusionPattern(".*" + UserDataRepository.class.getSimpleName() + ".*").
 *             attachedTo("persistence-unit1").
 *             build();
 *
 *   RepositoriesGroupBuilder.forPackage("com.mycorp.repo").
 *         withInclusionFilterPredicate(new Predicate&lt;Class&gt;() {
 *             {@literal @}Override
 *             public boolean apply(@Nullable Class input) {
 *                 return UserDataRepository.class.isAssignableFrom(input);
 *             }
 *         }).
 *         attachedTo("persistence-unit2").
 *         build();
 * </pre>
 *
 * @author Alexey Krylov
 * @since 08.12.12
 */
@NotThreadSafe
public class RepositoriesGroupBuilder {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    private String persistenceUnitName;
    private Collection<String> repositoriesPackages;
    private String inclusionPattern;
    private String exclusionPattern;
    private Predicate<Class> inclusionPredicate;
    private Predicate<Class> exclusionPredicate;

    /*===========================================[ CONSTRUCTORS ]=================*/

    protected RepositoriesGroupBuilder(Collection<String> repositoriesPackages) {
        this.repositoriesPackages = new ArrayList<String>(repositoriesPackages);
    }

    /*===========================================[ CLASS METHODS ]================*/

    public static RepositoriesGroupBuilder forPackage(String packageName) {
        Assert.notNull(packageName);
        return forPackages(Arrays.asList(packageName));
    }

    public static RepositoriesGroupBuilder forPackages(Collection<String> repositoriesPackages) {
        Assert.notEmpty(repositoriesPackages);
        Assert.noNullElements(repositoriesPackages.toArray(new String[repositoriesPackages.size()]));
        return new RepositoriesGroupBuilder(repositoriesPackages);
    }

    public RepositoriesGroupBuilder withPackage(String packageName) {
        Assert.notNull(packageName);
        repositoriesPackages.add(packageName);
        return this;
    }

    public RepositoriesGroupBuilder withPackages(Collection<String> repositoriesPackages) {
        Assert.noNullElements(repositoriesPackages.toArray(new String[repositoriesPackages.size()]));
        this.repositoriesPackages.addAll(repositoriesPackages);
        return this;
    }

    public RepositoriesGroupBuilder attachedTo(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        return this;
    }

    public RepositoriesGroupBuilder withInclusionPattern(String inclusionPattern) {
        this.inclusionPattern = inclusionPattern;
        return this;
    }

    public RepositoriesGroupBuilder withExclusionPattern(String exclusionPattern) {
        this.exclusionPattern = exclusionPattern;
        return this;
    }

    public RepositoriesGroupBuilder withInclusionFilterPredicate(Predicate<Class> inclusionPredicate) {
        this.inclusionPredicate = inclusionPredicate;
        return this;
    }

    public RepositoriesGroupBuilder withExclusionFilterPredicate(Predicate<Class> exclusionPredicate) {
        this.exclusionPredicate = exclusionPredicate;
        return this;
    }

    public RepositoriesGroup build() {
        RepositoriesGroup repositoriesGroup = new RepositoriesGroup(repositoriesPackages, persistenceUnitName);
        repositoriesGroup.setIncusionPattern(inclusionPattern);
        repositoriesGroup.setExclusionPattern(exclusionPattern);
        repositoriesGroup.setInclusionFilterPredicate(inclusionPredicate);
        repositoriesGroup.setExclusionFilterPredicate(exclusionPredicate);
        return repositoriesGroup;
    }
}