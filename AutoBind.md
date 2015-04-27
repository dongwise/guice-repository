## Repository Auto-bind ##

If you don't want to follow Google Guice binding style, or you have too many Repositories, or you just lazy - you can delegate this binding job to guice-repository.

All you need is to use _com.google.code.guice.repository.ScanningJpaRepositoryModule_ instead of _com.google.code.guice.repository.JpaRepositoryModule_.

[ScanningJpaRepositoryModule](http://code.google.com/p/guice-repository/source/browse/trunk/src/main/java/com/google/code/guice/repository/configuration/ScanningJpaRepositoryModule.java) will need a package(s) name(s) as a constructor parameters. This packages will be scanned and all found Repositories will be binded.

Single scan package example:
```
install(new ScanningJpaRepositoryModule("com.mycorp.repo", "my-persistence-unit"));
```

Multiple scan packages example:
```
install(new ScanningJpaRepositoryModule(
                        Arrays.asList(
                                RepositoriesGroupBuilder.forPackage("com.google.code.guice.repository.testing.repo").
                                        withExclusionPattern(".*" + UserDataRepository.class.getSimpleName() + ".*").
                                        attachedTo("test-h2").
                                        build(),

                                RepositoriesGroupBuilder.forPackage("com.google.code.guice.repository.testing.repo").
                                        withInclusionFilterPredicate(new Predicate<Class>() {
                                            @Override
                                            public boolean apply(@Nullable Class input) {
                                                return UserDataRepository.class.isAssignableFrom(input);
                                            }
                                        }).
                                        attachedTo("test-h2-secondary").
                                        build()
                        )) );
```

Patterns & [Predicates](http://code.google.com/p/guava-libraries/wiki/FunctionalExplained) can be used in combination. Predicates can be built with [Guava specialized utility](http://docs.guava-libraries.googlecode.com/git-history/release/javadoc/com/google/common/base/Predicates.html).

**NOTE:** you can combine auto-bind with manual binding - just override _bindRepositories_ method and call **super**:
```
@Override
protected void bindRepositories(RepositoryBinder binder) {
    super.bindRepositories(binder);
    // manual bind specific repositories
}
```