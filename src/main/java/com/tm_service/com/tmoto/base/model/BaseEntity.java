package com.tm_service.com.tmoto.base.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Getters et setters pour l'ID et d'autres attributs communs

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

