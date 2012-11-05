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

package com.google.code.guice.repository.testing.model;

import javax.persistence.*;

@Entity
public class Account {

    /*===========================================[ INSTANCE VARIABLES ]=========*/

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String uuid;

    @Column
    private String name;

    /*===========================================[ CONSTRUCTORS ]===============*/

    public Account(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Account() {
    }

    /*===========================================[ CLASS METHODS ]==============*/

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
