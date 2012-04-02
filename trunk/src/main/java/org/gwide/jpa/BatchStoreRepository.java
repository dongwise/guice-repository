/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 21.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/

package org.gwide.jpa;

import org.springframework.data.repository.Repository;

import java.io.Serializable;

public interface BatchStoreRepository<T,ID extends Serializable> extends Repository<T,ID>{

    /*===========================================[ INTERFACE METHODS ]==============*/

    /**
     * Массовая вставка в базу (batch). Этот метод удаляет сохраненные сущности из PersistentContext'а (L1 cache)
     * для экономии памяти на массовых сохранениях.
     *
     * @param entities сохраняемые сущности.
     */
    void storeInBatch(Iterable<T> entities);
}
