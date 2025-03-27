package com.base_spring_boot.com.applications.microservice.persistence.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "test_entity")
public class TestEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private double price;

    /**
     * Date et heure de création de l'instance d'unité d'enseignement.
     * Ce champ est obligatoire.
     */
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;



    /**
     * Date et heure de la dernière modification de l'instance d'unité d'enseignement.
     * Ce champ est optionnel.
     */
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }


    public void setPrice(double price) {
        this.price = price;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestEntity that = (TestEntity) o;
        return Double.compare(price, that.price) == 0 && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(creationDate, that.creationDate) && Objects.equals(lastModifiedDate, that.lastModifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, creationDate, lastModifiedDate);
    }
}
