/*
 * Copyright (c) 2012, i-Free. All Rights Reserved.
 * Use is subject to license terms.
 */

package com.google.code.guice.repository.spi;

import com.google.code.guice.repository.configuration.RepositoryBinding;

/**
 * AccesibleRepositoryBinding - TODO: description
 *
 * @author Alexey Krylov (AleX)
 * @since 11.12.12
 */
public class AccesibleRepositoryBinding implements RepositoryBinding {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]===========*/

    private String persistenceUnitName;
    private Class repositoryClass;
    private Class customRepositoryClass;

/*===========================================[ CONSTRUCTORS ]=================*/

    public AccesibleRepositoryBinding(Class repositoryClass) {
        this.repositoryClass = repositoryClass;
    }
    /*===========================================[ CLASS METHODS ]================*/

    @Override
    public Class getRepositoryClass() {
        return repositoryClass;
    }

    @Override
    public Class getCustomRepositoryClass() {
        return customRepositoryClass;
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public void setCustomRepositoryClass(Class customRepositoryClass) {
        this.customRepositoryClass = customRepositoryClass;
    }

    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }
}
