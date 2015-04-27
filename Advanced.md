## Bind interceptor for all @Transactional methods ##

To bind interceptor for all @Transactional methods (including such methods in Repositories) you need to do something like this:
```
   bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), new MyTransactionalInterceptor());
```

## Using custom JPA dialect ##

You can specify custom properties for [LocalContainerEntityManagerFactoryBean](http://static.springsource.org/spring/docs/3.0.x/api/org/springframework/orm/jpa/LocalContainerEntityManagerFactoryBean.html)  bean creation by overriding _getAdditionalEMFProperties in JpaRepositoryModule:
```
install(new JpaRepositoryModule() {
            @Override
            protected Map<String, Object> getAdditionalEMFProperties(String persistenceUnitName) {
                Map<String, Object> properties = new HashMap<String, Object>();
                if ("persistence-unit1".equals(persistenceUnitName)){
                    properties.put("jpaDialect", new MyHibernateJpaDialect());
                    return properties;

                }
                return properties;
            }
       );
```_

Known properties: _jpaDialect, jpaVendorAdapter, dataSource (with theoretical multitanency support)._

## Configuring [JpaRepositoryFactoryBean](http://static.springsource.org/spring-data/data-jpa/docs/1.2.x/api/org/springframework/data/jpa/repository/support/JpaRepositoryFactoryBean.html) ##

See all available [RepositoryBindingBuilder](http://code.google.com/p/guice-repository/source/browse/trunk/src/main/java/com/google/code/guice/repository/configuration/RepositoryBindingBuilder.java) methods:
```
new JpaRepositoryModule() {
    @Override
    protected void bindRepositories(RepositoryBinder binder) {
        binder.bind(UserRepository.class).withNamedQueries(new PropertiesBasedNamedQueries(.)).withQueryLookupStrategyKey(QueryLookupStrategy.Key.CREATE).to("test-h2");
    }
}
```

## API design ##

Main principles that has been used for _guice-repository_ API design is simplicity and flexibility.

All JpaRepositoryModule methods has _protected_ modifier, so you can easily create your own variation of this module.