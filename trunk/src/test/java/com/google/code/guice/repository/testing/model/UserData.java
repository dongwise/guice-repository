/*
* Copyright (c) 2012, i-Free. All Rights Reserved.
*/

package com.google.code.guice.repository.testing.model;

import javax.persistence.*;

/**
 * UserInfo -
 *
 * @author Alexey Krylov (AleX)
 * @since 03.12.12
 */
@Entity
public class UserData {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String data;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public UserData(String data) {
        this.data = data;
    }

    public UserData() {
    }

    /*===========================================[ CLASS METHODS ]==============*/

    public String getData() {
        return data;
    }
}