/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 19.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/

package ru.befree.common.jpa;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Repository
public abstract class AbstractJPARepository<T, ID extends Serializable> implements JpaRepository<T, ID>,
        JpaSpecificationExecutor<T>, BatchStoreRepository<T, ID> {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    private Logger logger;

    //TODO: locks
    private EntityManager entityManager;
    private SimpleJpaRepository<T, ID> delegate;

    /*===========================================[ CLASS METHODS ]==============*/

    @Inject
    public void init(EntityManager entityManager) {
        configure(getClass(), DomainClassExtractor.extact(getClass()), entityManager);
    }

    final void configure(Class repositoryClass, Class domainClass, EntityManager entityManager) {
        logger = LoggerFactory.getLogger(repositoryClass);
        delegate = new SimpleJpaRepository<T, ID>(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public void storeInBatch(Iterable<T> entities) {
        List<T> saved = save(entities);
        for (T entity : saved) {
            entityManager.detach(entity);
        }
    }

    @Transactional
    public void delete(ID id) {
        delegate.delete(id);
    }

    @Transactional
    public void delete(T entity) {
        delegate.delete(entity);
    }

    @Transactional
    public void delete(Iterable<? extends T> entities) {
        delegate.delete(entities);
    }

    @Transactional
    public void deleteInBatch(Iterable<T> entities) {
        delegate.deleteInBatch(entities);
    }

    @Override
    @Transactional
    public void deleteAll() {
        delegate.deleteAll();
    }

    public T findOne(ID id) {
        return delegate.findOne(id);
    }

    public boolean exists(ID id) {
        return delegate.exists(id);
    }

    @Override
    public List<T> findAll() {
        return delegate.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return delegate.findAll(sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return delegate.findAll(pageable);
    }

    public T findOne(Specification<T> spec) {
        return delegate.findOne(spec);
    }

    public List<T> findAll(Specification<T> spec) {
        return delegate.findAll(spec);
    }

    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        return delegate.findAll(spec, pageable);
    }

    public List<T> findAll(Specification<T> spec, Sort sort) {
        return delegate.findAll(spec, sort);
    }

    @Override
    public long count() {
        return delegate.count();
    }

    public long count(Specification<T> spec) {
        return delegate.count(spec);
    }

    @Transactional
    public T save(T entity) {
        return delegate.save(entity);
    }

    @Transactional
    public T saveAndFlush(T entity) {
        return delegate.saveAndFlush(entity);
    }

    @Transactional
    public List<T> save(Iterable<? extends T> entities) {
        return delegate.save(entities);
    }

    @Override
    @Transactional
    public void flush() {
        delegate.flush();
    }

    protected Logger getLogger() {
        return logger;
    }
}