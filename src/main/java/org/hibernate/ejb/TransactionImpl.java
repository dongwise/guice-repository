/**
 * Copyright (C) 2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

//$Id$
package org.hibernate.ejb;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

/**
 * @author Gavin King
 * @author Emmanuel Bernard
 */
public class TransactionImpl implements EntityTransaction {
    private static boolean dump = false;
    private Logger logger = LoggerFactory.getLogger(TransactionImpl.class);
    private HibernateEntityManagerImplementor entityManager;
    private Transaction tx;
    private boolean rollbackOnly;

    public TransactionImpl(AbstractEntityManagerImpl entityManager) {
        this.entityManager = entityManager;
    }

    private Session getSession() {
        return entityManager.getSession();
    }

    public void begin() {
        if (dump) {
            logger.error("begin", new Exception());
        } else {
            logger.info("begin");
        }
        try {
            rollbackOnly = false;
            if (tx != null && tx.isActive()) {
                throw new IllegalStateException("Transaction already active");
            }
            //entityManager.adjustFlushMode();
            tx = getSession().beginTransaction();
        } catch (HibernateException he) {
            entityManager.throwPersistenceException(he);
        }finally {
            logger.info("begin done");
        }
    }

    public void commit() {
        if (dump) {
            logger.error("commit", new Exception());
        }else{
            logger.info("commit");
        }


        if (tx == null || !tx.isActive()) {
            throw new IllegalStateException("Transaction not active");
        }
        if (rollbackOnly) {
            tx.rollback();
            throw new RollbackException("Transaction marked as rollbackOnly");
        }
        try {
            tx.commit();
        } catch (Exception e) {
            Exception wrappedException;
            if (e instanceof HibernateException) {
                wrappedException = entityManager.convert((HibernateException) e);
            } else {
                wrappedException = e;
            }
            try {
                //as per the spec we should rollback if commit fails
                tx.rollback();
            } catch (Exception re) {
                //swallow
            }
            throw new RollbackException("Error while committing the transaction", wrappedException);
        } finally {
            rollbackOnly = false;
            logger.info("commit done");
        }
        //if closed and we commit, the mode should have been adjusted already
        //if ( entityManager.isOpen() ) entityManager.adjustFlushMode();
    }

    public void rollback() {
        if (dump) {
            logger.error("rollback", new Exception());
        }else {
            logger.info("rollback");
        }

        if (tx == null || !tx.isActive()) {
            throw new IllegalStateException("Transaction not active");
        }
        try {
            tx.rollback();
        } catch (Exception e) {
            throw new PersistenceException("unexpected error when rollbacking", e);
        } finally {
            try {
                if (entityManager != null) {
                    Session session = getSession();
                    if (session != null && session.isOpen()) {
                        session.clear();
                    }
                }
            } catch (Throwable t) {
                //we don't really care here since it's only for safety purpose
            }
            rollbackOnly = false;
            logger.info("rollback done");
        }
    }

    public void setRollbackOnly() {
        if (!isActive()) {
            throw new IllegalStateException("Transaction not active");
        }
        this.rollbackOnly = true;
    }

    public boolean getRollbackOnly() {
        if (!isActive()) {
            throw new IllegalStateException("Transaction not active");
        }
        return rollbackOnly;
    }

    public boolean isActive() {
        try {
            return tx != null && tx.isActive();
        } catch (RuntimeException e) {
            throw new PersistenceException("unexpected error when checking transaction status", e);
        }
    }

}
