## Custom Repository Implementations ##

This part of documentation dedicated to [part 1.4 of official Spring Data-JPA reference manual](http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/#repositories.custom-implementations).

In case of manual Repository binding via JpaRepositoryModule and feature 1.4.2 (Custom behavior to all repositories) from reference manual you should use this construction:
```
install(new JpaRepositoryModule("persistence-unit-name") {
            @Override
            protected void configureRepositories() {
                bind(MyRepository.class).toProvider(new JpaRepositoryProvider<MyRepository>(MyRepositoryImpl.class));
            }
        });
```

in this example MyRepositoryImpl is your custom repository base class:
```
public class MyRepositoryImpl extends SimpleBatchStoreJpaRepository<MyEntity,Long> implements MyRepository {

    public CustomerRepositoryImpl(Class<MyEntity> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    public void sharedCustomMethod(Long entityId) {
    }
}

```

It is **mandatory** to create a constructor with **Class + EntityManager** parameters.