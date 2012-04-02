/****************************************************************************\
 __FILE..........: GuiceTestRunner.java
 __AUTHOR........: Alexey Krylov
 __COPYRIGHT.....: Copyright (c) 2011 i-free
 _________________All rights reserved.
 __VERSION.......: 1.0
 __DESCRIPTION...:
 __HISTORY.......: DATE       COMMENT
 _____________________________________________________________________________
 ________________:31.01.12 Alexey Krylov AKA LexX: created.
 ****************************************************************************/

package ru.befree.common.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

/**
 * Uses Guice to inject JUnit4 tests.
 */
public class GuiceTestRunner extends BlockJUnit4ClassRunner {
    private final Injector injector;


    /**
     * Creates a new GuiceTestRunner.
     *
     * @param classToRun the test class to run
     * @param modules    the Guice modules
     *
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed
     */
    public GuiceTestRunner(Class<?> classToRun, Module... modules) throws InitializationError {
        super(classToRun);
        injector = Guice.createInjector(modules);
    }

    @Override
    public Object createTest() {
        return injector.getInstance(getTestClass().getJavaClass());
    }

    @Override
    protected void validateZeroArgConstructor(List<Throwable> errors) {
        // Guice can inject constructors with parameters so we don't want this method to trigger an error
    }

    /**
     * Returns the Guice injector.
     *
     * @return the Guice injector
     */
    protected Injector getInjector() {
        return injector;
    }
}