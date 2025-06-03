package com.example.auth.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement

public enum Role {
    ADMIN,
    DEVELOPER,
    USER
}
