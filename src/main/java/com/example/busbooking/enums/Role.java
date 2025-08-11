
package com.example.busbooking.enums;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement

// Enum to denote Roles of all users.
public enum Role {
    ADMIN(1),
    DEVELOPER(2),
    USER(3);

    private final int id;

    Role(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
