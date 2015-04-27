## Developer Guide ##
Welcome to the developer documentation of guice-repository project - a Google Guice adapted version of [Spring Data-JPA](http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/#preface) project with some additional features.

Current project version is: **2.1.0**

## What are Spring Data-JPA? ##
Here is description from Spring Data-JPA authors:

"_Spring JPA is part of the umbrella Spring Data project that makes it easy to easily implement JPA based repositories._"

For my opinion it is very useful project that helps developer to work hard on business logic and avoid writing boilerplate code around persistence layer.

## What are guice-repository? ##
Guice-repository is an adapter for _[Spring Data-JPA](http://www.springsource.org/spring-data/jpa)_ project which brings all known functionality of _[Guice-Persist](http://code.google.com/p/google-guice/wiki/GuicePersist)_ with many additional features:
  * Support for [batch store](http://www.objectdb.com/java/jpa/persistence/store#Batch_Store_) (see [this](BatchStore.md) page);
  * Allow access to EntityManager from Repository (see [EntityManagerProvider](http://code.google.com/p/guice-repository/source/browse/trunk/src/main/java/com/google/code/guice/repository/EntityManagerProvider.java));
  * Full support for @Transactional methods for all Guice-instantiated entities (see [this](Transactions.md) page);
  * Support for [multiple](MultiplePersistenceUnits.md) persistence units;
  * Support for [direct injection](PersistenceInjections.md) of EntityManager/EntityManagerFactory with @PersistenceContext/@PersistenceUnit/@Inject&@Named annotations;
  * Support for injections in repositories/custom implementations;
  * [PersistFilter](WebPersistFilter.md) as implementation of "Open EntityManager in View"/"session-in-view"/"session-per-http-request" pattern;
  * Offers natural way of Repository binding process;
  * Repository auto-bind possibilities with exclusion/inclusion filters - see [AutoBind](AutoBind.md);
  * Ability to bind interceptor to catch Repository methods with @Transactional.

**NOTE:** _guice-repository_ is not compatible with _guice-persist_ extension and can't coexist in one _pom.xml_.

## How did this work? ##

There is a three main steps:

1. Define a [Repository](http://static.springsource.org/spring-data/data-jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html) interface

```
public interface AccountRepository extends JpaRepository<Account, Long>,
        EntityManagerProvider {

    Account findAccountByUuid(String uuid);

    @Query("select a from Account a where a.name = :name")
    Account findAccountByName(@Param("name") String name);
}
```

This process is well described in the [original manual](http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/#repositories.definition).
All features described in 1.1-1.4 documentation parts is supported by _guice-repository_.

2. Install a Guice-module

You can select between manual Repository binding in JpaRepositoryModule and auto-binding with ScanningJpaRepositoryModule.

Example for JpaRepositoryModule:
```
install(new JpaRepositoryModule("my-persistence-unit") {
            @Override
            protected void bindRepositories(RepositoryBinder binder) {
                binder.bind(AccountRepository.class).to("my-persistence-unit");
            }
        });
```


Example for [ScanningJpaRepositoryModule](AutoBind.md):
```
install(new ScanningJpaRepositoryModule("com.mycorp.repo", "my-persistence-unit"));
```

In this case "com.mycorp.repo" is a package where your Repository interfaces is located.

3. @Inject & use

Inject and use Repository in your services/business-logic modules:

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

## Why not just use pure Guice-Persist? ##

My own best-reasons-list for this is:
  * Reduce boilerplate code around persistence layer
  * Simplify understanding of Domain model and its lifecycle aspects between team members
  * Isolation from ORM-configuration vendor specifics (see [this](ImplementationSpecifics.md) page) in EntityManager configuration
  * OOM-protected Batch-insert feature

## Where i can get it? ##

Project is mavenized and published in Central Maven repository.

```
<dependency>
    <groupId>com.google.code.guice-repository</groupId>
    <artifactId>guice-repository</artifactId>
    <version>${version.guice-repository}</version>
</dependency>
```

This project is depends on many other 3rd party libraries which is required to work. So, _without_ Maven you should manually add actual versions of this 3rd party libraries. Actual list of them can be viewed in _dependencies_ section of [pom.xml](http://code.google.com/p/guice-repository/source/browse/trunk/pom.xml).

Actual project version will be always on top of this page.