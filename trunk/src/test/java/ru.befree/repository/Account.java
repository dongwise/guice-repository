/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 18.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.gwide.repository;

import javax.persistence.*;

@Entity
public class Account {
    /*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String uuid;

    public Account(String uuid) {
        this.uuid = uuid;
    }

    public Account() {
    }
}
