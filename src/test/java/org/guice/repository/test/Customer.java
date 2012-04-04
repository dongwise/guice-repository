/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 18.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.guice.repository.test;

import javax.persistence.*;

@Entity
public class Customer {
/*===========================================[ STATIC VARIABLES ]=============*/
/*===========================================[ INSTANCE VARIABLES ]=========*/
/*===========================================[ CONSTRUCTORS ]===============*/
/*===========================================[ CLASS METHODS ]==============*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;
    @Column
    private String surname;

    public Customer(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Customer() {
    }
}
