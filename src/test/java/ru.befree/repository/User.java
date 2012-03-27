/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 20.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package ru.befree.repository;

import javax.persistence.*;

@Entity
public class User {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;

    @Column
    private String uuid;

    public User(String uuid) {
        this.uuid = uuid;
    }

    public User() {
    }
}
