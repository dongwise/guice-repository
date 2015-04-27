### 2.1.0 ###
  * Migration to Spring-Data JPA 1.3.0 and Spring 3.1.4.RELEASE

### 2.0.2 ###

  * PersistFilter related bugfixes - it's now possible to direct Repository injection instead of Provider of Repository in web-environments.

### 2.0.1 ###

  * PersistFilter related bugfixes.

### 2.0.0 ###

  * Full support for @Transactional methods for all Guice-instantiated entities;
  * Support for multiple persistence units;
  * Support for direct injection of EntityManager/EntityManagerFactory with @PersistenceContext/@PersistenceUnit/@Inject&@Named annotations;
  * Support for injections in repositories/custom implementations;
  * PersistFilter as implementation of "Open EntityManager in View"/"session-in-view"/"session-per-http-request" pattern;
  * More natural way of Repository binding process;
  * Highly tunable ScanningJpaRepositoryModule: RepositoriesGroup, RepositoriesGroupBuilder, RepositoriesGroupFilterPredicates, etc;
  * Ability to bind interceptor to catch Repository methods with @Transactional;
  * Significant performance improvements;
  * Support for all settable properties of JpaRepositoryFactoryBean: namedQueries & queryLookupStrategyKey (see RepositoryBindingBuilder).

### 1.0.0 ###

  * Initial release and documentation
  * Maven repo sync