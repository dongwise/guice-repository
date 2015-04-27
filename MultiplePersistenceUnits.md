Since 2.0.0 it's possible to work with multiple persistence units.

This process is quite simple:
  1. Define persistence units and entities in META-INF/persistence.xml;
  1. Define names of persistence units in JpaRepositoryModule/ScanningJpaRepositoryModule constructor;
  1. Define persistence unit name during repository binding process with RepositoryBinder or with annotating Repository interface with @Transactional(value="persistence-unit-name") or with @PersistenceContext(unitName="persistence-unit-name");
  1. Mark method, which operates with Repository, with @Transactional(value="persistence-unit-name").

Example for RepositoryBinder:
```
install(new JpaRepositoryModule() {
            @Override
            protected void bindRepositories(RepositoryBinder binder) {
                binder.bind(UserDataRepository.class).to("test-h2-secondary");
            }
       );
```

Example for Repository annotation:
```
@Transactional(value = "test-h2-secondary", readOnly = true)
public interface UserDataRepository extends BatchStoreJpaRepository<UserData, Long>,
        EntityManagerProvider {
}
```

This process based on Spring Data JPA default way for multiple persistence units resolution, you can see related docs/examples here:
  * http://blog.springsource.org/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/#comment-198835
  * https://github.com/SpringSource/spring-data-jpa/blob/master/src/test/resources/multiple-entity-manager-integration-context.xml

**NOTE:** all specified persistence units will be managed by [PersistenceUnitConfigurationsManager](http://code.google.com/p/guice-repository/source/browse/trunk/src/main/java/com/google/code/guice/repository/configuration/PersistenceUnitsConfigurationManager.java). It always has at least one configuration - default configuration. Default configuration is used for all cases when you have no direct specification to persistence unit name:
```
@Inject
private EntityManager entityManager;

@PersistenceContext
private EntityManager entityManager;

@PersistenceUnit
private EntityManagerFactory entityManagerFactory;

@Transactional
public defaultPersistenceUnitTransactionalMethod(){
}
...
bind(Repository.class).withSelfDefinition();
```