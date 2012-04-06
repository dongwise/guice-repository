/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 18.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/

package org.guice.repository.test;

import org.guice.repository.BatchStoreRepository;
import org.guice.repository.EntityManagerProvider;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User>,
        BatchStoreRepository<User>, EntityManagerProvider, UserRepositoryCustom{

    @Modifying
    @Transactional
    @Query("delete from User u where u.age >= 200")
    void deleteInactiveUsers();

    @Modifying
    @Transactional
    @Query("delete from User u where u.age >= 1")
    void deleteOtherUsers();
}
