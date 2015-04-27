## Repository and Concurrency aspects ##
All Repositories is thread-safe except case, when you use [PersistFilter](http://code.google.com/p/guice-repository/source/browse/trunk/src/main/java/com/google/code/guice/repository/filter/PersistFilter.java) and ignores prohibition of threads manual management related to Java EE. In this case you need to synchronize Repository access code.

Some theory regarding to this JPA aspect can be found here:
  * http://docs.oracle.com/javaee/6/tutorial/doc/bnbqw.html
  * http://javanotepad.blogspot.com/2007/08/managing-jpa-entitymanager-lifecycle.html
  * http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html_single/#transactions-basics-issues (see information about Session, because it is an underlying EntityManager implementation for Hibernate ORM)