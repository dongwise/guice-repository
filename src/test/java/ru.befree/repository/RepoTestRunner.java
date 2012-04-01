/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 19.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.repository;

import org.junit.runners.model.InitializationError;
import ru.befree.common.jpa.JPAPersistenceModule;
import ru.befree.common.jpa.JPARepositoryProvider;
import ru.befree.common.test.GuiceTestRunner;


public class RepoTestRunner extends GuiceTestRunner {

    /*===========================================[ CLASS METHODS ]==============*/

    public RepoTestRunner(Class<?> classToRun) throws InitializationError {
        super(classToRun, new JPAPersistenceModule() {
            @Override
            protected void configureRepositories() {
//                bind(CustomerRepository.class).to(JPACustomerRepository.class);
//                bind(CustomerRepository.class).to(JPACustomerRepository.class);
                //TODO: может быть на уровне Interceptor можно это отловить
                //TODO: в прокси надо отлавливать все вызовы на @Transactional и @Query, вешать на них то, что висело в стандартном Spring-DATA

                //TODO: можно выпилить бинд на провайдер и сделать перехватчик на инжекцию
/*
                bindInterceptor(Matchers.annotatedWith(Repository.class), Matchers.any(), new MethodInterceptor() {
                    public Object invoke(MethodInvocation invocation) throws Throwable {
                        System.out.println(String.format("[%s] [%s] [%s] [%s]", invocation.getClass(), invocation.getMethod().getName(), invocation.getArguments(), invocation.getStaticPart()));
                        return invocation.proceed();
                    }
                });
*/

                bind(UserRepository.class).toProvider(new JPARepositoryProvider<UserRepository>());
                bind(AccountRepository.class).toProvider(new JPARepositoryProvider<AccountRepository>());
/*
                bind(AccountRepository.class).toProvider(new Provider<AccountRepository>() {
                    @Inject

                    @Override
                    public AccountRepository get() {
                        //TODO: proxy - JPARepository должен быть сделан Guice-ом, т.к. только в этом случае будут перехваты
                        return (AccountRepository) Proxy.newProxyInstance(AccountRepository.class.getClassLoader(),
                                new Class[]{AccountRepository.class}, new JPARepository<Account, Long>());
                    }
                });

*/
//                bind(AccountRepository.class).toProvider(JPARepository.class);
            }
        });
    }
}
