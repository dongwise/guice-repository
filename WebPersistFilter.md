PersistFilter of **guice-repository** is adapted version of [OpenEntityManagerInViewFilter](http://static.springsource.org/spring/docs/3.0.x/api/org/springframework/orm/jpa/support/OpenEntityManagerInViewFilter.html). This filter binds a JPA EntityManager to the thread for the entire processing of the HTTP request. Intended for the "Open EntityManager in View"/session-in-view/session-per-http-request pattern, i.e. to allow lazy loading in web views despite the original transactions already being completed.
To be able to use mentioned above pattern, register this filter once in your _Guice ServletModule_. It is important that you register this filter before any other filter.
Example configuration:
```
   public class MyModule extends ServletModule {
     public void configureServlets() {
       filter("/*").through(PersistFilter.class);
       serve("/index.html").with(MyHtmlServlet.class);
       // Etc.
     }
   }
```

For single persistence unit you can configure your filter with setPersistenceUnitName(String) (through web.xml/programmatically). Also you can just skip this parameter - in this case default persistence (PersistenceUnitsConfigurationManager.getDefaultConfiguration()) unit configuration will be used.

For multiple persistence units, you should register your custom filter before this filter. Your custom filter should set parameter "persistenceUnitName" into HttpServletRequest.
This filter requires the [Guice Servlet](http://code.google.com/p/google-guice/wiki/Servlets) extension.

Also it's recommended to read about [original PersistFilter](http://code.google.com/p/google-guice/wiki/JPA) from _guice-persist extension_.

**NOTE:** Since Hibernate 4.1.7 you can do lazy-load even if original EntityManager is closed.
Use property:
```
hibernate.enable_lazy_load_no_trans=true
```

More details here:
  * http://stackoverflow.com/questions/578433/how-to-solve-lazy-initialization-exception-using-jpa-and-hibernate-as-provider
  * https://hibernate.onjira.com/browse/HHH-7457