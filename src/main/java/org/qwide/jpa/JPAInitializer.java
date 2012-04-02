/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 18.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.qwide.jpa;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JPAInitializer {

    /*===========================================[ STATIC VARIABLES ]=============*/

    private static final Logger logger = LoggerFactory.getLogger(JPAInitializer.class);

    /*===========================================[ CONSTRUCTORS ]===============*/

    @Inject
    public void init(PersistService service) {
        try {
            service.start();
            logger.debug("PersistService started");
        } catch (Exception e) {
            // Ошибка может возникнуть в случае некорректно указанных параметров
            logger.error("Error", e);
            throw new RuntimeException(e);
        }
    }
}
