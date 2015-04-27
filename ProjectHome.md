# Overview #

Guice-repository is a **Guice-adapted** version of [Spring Data-JPA](http://www.springsource.org/spring-data/jpa) project with some additional features.

Example:
```
public class AccountService {
    @Inject
    private AccountRepository accountRepository;

    public void registerUser(String login, String password) throws RegistrationException{
     // ... some checks & etc
     accountRepository.save(new Account(login, password));
     // ... something else
    }

    public Account findAccount(String login) throws FinderException{
     return accountRepository.findAccountByLogin(login);
    }
}
```

## Repository pattern motivation ##
"**by Edward Hieatt and Rob Mee**

_Mediates between the domain and data mapping layers using a collection-like interface for accessing domain objects._"

[![](http://guice-repository.googlecode.com/svn/wiki/img/repository.png)](http://martinfowler.com/eaaCatalog/repository.html)


Theory can be found here:
  * http://martinfowler.com/eaaCatalog/repository.html
  * http://msdn.microsoft.com/en-us/library/ff649690.aspx
  * http://design-pattern.ru/patterns/repository.html

All reference documentation regarding to **Spring Data-JPA** is
[here](http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/).


---


## Project features ##
  * Full support of [1.1-1.4](http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/#repositories.core-concepts), [2.2-2.5](http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods) original Spring Data-JPA documentation parts;
  * Support of original 2.6, 2.7 parts in manual programmatic Spring wiring mode;
  * [Full support](Transactions.md) for @Transactional methods for all Guice-instantiated entities (**NEW** in 2.0);
  * [Support](MultiplePersistenceUnits.md) for multiple persistence units (**NEW** in 2.0);
  * [Support](PersistenceInjections.md) for direct injection of EntityManager/EntityManagerFactory with @PersistenceContext/@PersistenceUnit/@Inject&@Named annotations (**NEW** in 2.0);
  * Support for injections in repositories/custom implementations (**NEW** in 2.0);
  * [PersistFilter](WebPersistFilter.md) as implementation of "Open EntityManager in View"/"session-in-view"/"session-per-http-request" pattern (**NEW** in 2.0);
  * More natural way of Repository binding process (**NEW** in 2.0);
  * Repository Auto-bind possibilities - no need to do manual .bind() for each Repository (see [AutoBind](AutoBind.md));
  * Auto-binder possibilities with exclusion/inclusion filters - see [ScanningJpaRepositoryModule](AutoBind.md) (filters is **NEW** in 2.0);
  * Ability to bind interceptor to catch Repository methods with @Transactional (**NEW** in 2.0);
  * Significant performance improvements (**NEW** in 2.0);
  * Support for all settable properties of JpaRepositoryFactoryBean: namedQueries & queryLookupStrategyKey (see [RepositoryBindingBuilder](http://code.google.com/p/guice-repository/source/browse/trunk/src/main/java/com/google/code/guice/repository/configuration/RepositoryBindingBuilder.java)) (**NEW** in 2.0);
  * Support for [batch store](http://www.objectdb.com/java/jpa/persistence/store#Batch_Store_) (see [this](BatchStore.md) page);
  * Allow access to EntityManager from Repository (see [EntityManagerProvider](http://code.google.com/p/guice-repository/source/browse/trunk/src/main/java/com/google/code/guice/repository/EntityManagerProvider.java));
  * Significant [test coverage](http://code.google.com/p/guice-repository/source/browse/trunk/src/test/java/com/google/code/guice/repository/testing/junit).


---


## Quickstart ##

1. Define your [Repository](http://static.springsource.org/spring-data/data-jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html) interface

```
public interface AccountRepository extends JpaRepository<Account, Long>,
        EntityManagerProvider {

    Account findAccountByUuid(String uuid);

    @Query("select a from Account a where a.name = :name")
    Account findAccountByName(@Param("name") String name);
}
```


2. Install a Guice-module

```
install(new JpaRepositoryModule("my-persistence-unit") {
            @Override
            protected void bindRepositories(RepositoryBinder binder) {
                binder.bind(AccountRepository.class).to("my-persistence-unit");
            }
        });
```

3. @Inject & use

```
public class AccountService {
   
    @Inject
    private AccountRepository accountRepository;

    public void registerUser(String login, String password) throws RegistrationException{
     // ... some checks & etc
     accountRepository.save(new Account(login, password));
     // ... something else
    }

    public Account findAccount(String login) throws FinderException{
     return accountRepository.findAccountByLogin(login);
    }
}
```

---


## Mavenize ##

Artifact is available in the Central Maven repository:
```
<dependency>
    <groupId>com.google.code.guice-repository</groupId>
    <artifactId>guice-repository</artifactId>
    <version>2.1.0</version>
</dependency>
```