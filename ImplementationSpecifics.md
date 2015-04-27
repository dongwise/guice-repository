## ORM Implementation Specifics ##

### Persistence-unit name ###

As you can see from documentation earlier there is an constructor parameter for JpaRepositoryModule named "my-persistence-unit", for example:
```
install(new JpaRepositoryModule("my-persistence-unit") {
            @Override
            protected void bindRepositories(RepositoryBinder binder) {
                binder.bind(AccountRepository.class).to("my-persistence-unit");
            }
        });
```

Constructor parameter is a persistence unit name which will be used for _EntityManagerFactory_ creation. This parameter is required by underlying _Spring Data-JPA_ subsystem and it is mandatory to pass them to _JpaPersistModule_.

_guice-repository_ allows you three options to specify persistence-unit name:
  1. Specify it directly during module instantiation
  1. Create your own persistence units resolving method, just override _getPersistenceUnitNames_ of JpaRepositoryModule:
```
   install(new JpaRepositoryModule() {
            @Override
            protected void bindRepositories(RepositoryBinder binder) {
                binder.bind(AccountRepository.class).to("my-persistence-unit");
            }

            protected Collection<String> getPersistenceUnitsNames() {
                // use your own detection mechanisms, but remember - Guice doesn't allow injection in modules during construction
                return Arrays.asList("my-persistence-unit");
            }
        });
```
  1. Specify system property named "persistence-units" splitted by symbol ',' or ';':
```
    -Dpersistence-units=unit1,unit2,unit3
```

For all options you should have **META-INF/persistence.xml** in your classpath. This configuration file should contain all persistence units used by your application.

### ORM Implemenation Specifics Separation ###

_guice-repository_ allows you to separate ORM-specific properties from **persistence.xml** configuration file.

This is very useful approach in situations where you don't know your application consumer ORM-specifics and/or physical database type and/or location. In this case you provide two artifacts to your customer:
  * application artifact with generic _persistence.xml_ which contains mappings and persistence unit definitions only
  * artifact with possible database configurations and configuration manual

**How to begin?**

First of all you should define your generic _persistence.xml_, here is an example:
```
<persistence version="2.0" xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence       http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">

    <persistence-unit name="myapp.oracle" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.ejb.HibernatePersistence</provider>
        <mapping-file>META-INF/mappings/sequence/User.orm.xml</mapping-file>
        <mapping-file>META-INF/mappings/sequence/Entity.orm.xml</mapping-file>
        <exclude-unlisted-classes>true</exclude-unlisted-classes>
        <shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>
    </persistence-unit>

    <persistence-unit name="myapp.h2" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.ejb.HibernatePersistence</provider>
        <mapping-file>META-INF/mappings/identity/User.orm.xml</mapping-file>
        <mapping-file>META-INF/mappings/identity/Entity.orm.xml</mapping-file>
        <exclude-unlisted-classes>true</exclude-unlisted-classes>
        <shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>
    </persistence-unit>

    <persistence-unit name="myapp.sql-server" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.ejb.HibernatePersistence</provider>
        <mapping-file>META-INF/mappings/identity/User.orm.xml</mapping-file>
        <mapping-file>META-INF/mappings/identity/Entity.orm.xml</mapping-file>
        <exclude-unlisted-classes>true</exclude-unlisted-classes>
        <shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>
    </persistence-unit>
</persistence>
```

As you can see there is three persistence units defined with no DB specifics - only mappings. It's important thing - if you need better portability of your mappings you should not choose annotation-based mappings. For example - identity sequences
is only choice for Oracle, but it's not supported in Microsoft SQL Server.

Second thing - you need to create set of ORM-specifics configuration files.
By-default _guice-repository_ perform a classpath search of files named **${persistence-unit-name}.properties**.

**Here is some examples of this files:**

_myapp.h2.properties_:
```
hibernate.connection.driver_class=org.h2.Driver

hibernate.dialect=org.hibernate.dialect.H2Dialect
hibernate.connection.url=jdbc:h2:./target/test-db;MVCC=TRUE

hibernate.show_sql=false
hibernate.format_sql=false
hibernate.hbm2ddl.auto=create
```

_myapp.oracle.properties_:
```
hibernate.connection.driver_class=oracle.jdbc.OracleDriver

hibernate.dialect=org.hibernate.dialect.Oracle10gDialect
hibernate.connection.url=jdbc:oracle:thin:@//localhost:1521/xe
hibernate.connection.username=dev
hibernate.connection.password=dev
hibernate.connection.autocommit=true
hibernate.show_sql=false
hibernate.format_sql=false
hibernate.hbm2ddl.auto=update

hibernate.id.new_generator_mappings=true
```

_myapp.sql-server.properties_:
```
hibernate.connection.driver_class=net.sourceforge.jtds.jdbc.Driver

hibernate.dialect=org.hibernate.dialect.SQLServerDialect
hibernate.connection.url=jdbc:jtds:sqlserver://SRV-TEST-DB:1433;databaseName=test_db;integratedSecurity=true;

hibernate.show_sql=false
hibernate.format_sql=false
hibernate.hbm2ddl.auto=update
hibernate.jdbc.batch_size=500
```

Also you can define your own ORM-specifics separation by overriding _getPersistenceUnitProperties_ method in _JparepositoryModule_:
```
   install(new JpaRepositoryModule("my-persistence-unit") {
            @Override
            protected Properties getPersistenceUnitProperties(String persistenceUnitName) {
              // define your own Properties for JPA persistence provider
            }
   }

```