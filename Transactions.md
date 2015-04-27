Since 2.0.0 you can use [@Transactional](http://static.springsource.org/spring/docs/3.0.x/api/org/springframework/transaction/annotation/Transactional.html) annotation for all your Guice-instantiated components.
**guice-repository** contains specific interceptor which is bound to all @Transactional methods:
```
CompositeTransactionInterceptor transactionInterceptor = new CompositeTransactionInterceptor();
requestInjection(transactionInterceptor);
bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionInterceptor);
```

CompositeTransactionInterceptor is a bridge between _guice-repository_ environment and Spring's default [TransactionInterceptor](http://static.springsource.org/spring/docs/3.0.x/api/org/springframework/transaction/interceptor/TransactionInterceptor.html).

Some related examples can be found here:
http://code.google.com/p/guice-repository/source/browse/trunk/src/test/java/com/google/code/guice/repository/testing/junit/transaction/

Code example:
```
    @Transactional(timeout = 1, rollbackFor = Exception.class)
    public void testTimeoutedTransaction() throws Exception {
        TimeUnit.SECONDS.sleep(3);
        userRepository.findAll();
    }
```