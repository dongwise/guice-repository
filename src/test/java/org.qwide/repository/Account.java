/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 18.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.qwide.repository;

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

    @Column
    private String name;

    public Account(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Account() {
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
