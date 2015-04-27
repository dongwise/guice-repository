## Batch Store ##

To use power of batches you should extend your Repository interface from _com.google.code.guice.repository.BatchStoreJpaRepository_.

For example:
```
public interface MyEntityRepository extends BatchStoreJpaRepository<MyEntity, Long> {
  //...
}
```

After that you will have an access to methods:
```
    /**
     * Saves all given entities in one batch. This method will detach saved enities from PersistentContext (L1 cache) to
     * prevent OutOfMemoryException.
     *
     * @param entities entities to save. Should be not null and not empty.
     */
    void saveInBatch(Iterable<T> entities);

    /**
     * Saves given entities in N batch iterations. Each batch contains <code>batchSize</code> elements. This method will
     * detach saved enities from PersistentContext (L1 cache) to prevent OutOfMemoryException.
     *
     * @param entities  entities to save. Should be not null and not empty.
     * @param batchSize count of entities to save per one batch. Should be positive number.
     */
    void saveInBatch(Iterable<T> entities, int batchSize);
```

Remember, that after batch store all stored entities became to detached state.