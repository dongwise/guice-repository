/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 21.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.common.jpa;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

class JPARepositoryProxy implements InvocationHandler {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private GenericJPARepository delegate;

    /*===========================================[ CONSTRUCTORS ]===============*/

    @Inject
    public void init(GenericJPARepository delegate) {
        this.delegate = delegate;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    void configure(Class repositoryClass, Class domainClass) {
        delegate.configure(repositoryClass, domainClass);
        //TODO: Наполнить некий Map известными методами: method -> parameterclasses
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            //TODO: отслеживать отстуствие методов у делегата и делать магию: QueryExecutorMethodInterceptor обслуживает хитрые запросы
//            if (delegate.getClass().getMethod(method.getName(), getParameterClasses(args)) == null) {
//                System.out.println(String.format("No method found for: [%s]", method));
//            }
            return method.invoke(delegate, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }catch (Throwable e) {
            throw e.getCause();
        }
    }

    private Class[] getParameterClasses(Object[] args) {
        if (args != null && args.length > 0) {
            return Collections2.transform(Arrays.asList(args), new Function<Object, Class>() {
                @Override
                public Class apply(Object input) {
                    return input.getClass();
                }
            }).toArray(new Class[args.length]);
        }
        return new Class[0];
    }
}