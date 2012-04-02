/****************************************************************************\
 __AUTHOR........: Alexey Krylov
 __CREATED.......: 20.03.12
 __COPYRIGHT.....: Copyright (c) 2011 Jobdone 
 __VERSION.......: 1.0
 __DESCRIPTION...:
 ****************************************************************************/


package org.gwide.repository;

import javax.persistence.*;

@Entity
public class User {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private int age;

    @Column
    private String name;

    @Column
    private String surname;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public User() {
    }

    public User(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    /*===========================================[ CLASS METHODS ]==============*/

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}