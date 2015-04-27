# Injections #
Since 2.0.0 it's possible to inject EntityManager/EntityManagerFactory in Guice-instantiated entities.

Injections for EntityManager/EntityManagerFactory related to different persistence unit is also supported - just specify "unitName" property.

Use @PersistenceContext/@Inject&@Named annotation to inject EnityManager:
```
// injection of EntityManager related to default persistence unit
@Inject
private EntityManager entityManager;

@Inject
@Named("test-h2-secondary")
private EntityManager entityManager;

@PersistenceContext
private EntityManager secondaryEntityManager;

@PersistenceContext(unitName = "test-h2-secondary")
private EntityManager secondaryEntityManager;
```

Use @PersistenceUnit/@Inject&@Named annotation to inject EnityManagerFactory:
```
// injection of EntityManagerFactory related to default persistence unit
@Inject
private EntityManagerFactory entityManagerFactory;

@Inject
@Named("test-h2-secondary")
private EntityManagerFactory entityManagerFactory;

@PersistenceUnit
private EntityManagerFactory secondaryEntityManagerFactory;

@PersistenceUnit(unitName = "test-h2-secondary")
private EntityManagerFactory secondaryEntityManagerFactory;
```

# Transactions #

To use injected EntityManager for entity modifications you should annotate your method with @Transactional. If EntityManager related to some persistence unit (except default) - you should specify persistence unit name in @Transactional annotation:

```
@Transactional
public void usePrimaryEntityManager() {
   entityManager.persist(new User());
}

@Transactional("test-h2-secondary")
public void testSecondaryEntityManager() {
   secondaryEntityManager.persist(new UserData());
   List resultList = secondaryEntityManager.createQuery("from UserData").getResultList();
}
```