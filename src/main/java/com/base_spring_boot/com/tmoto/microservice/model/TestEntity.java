package com.base_spring_boot.com.tmoto.microservice.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.base_spring_boot.com.tmoto.base.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_entity")
@JsonFilter("baseFilter")
public class TestEntity extends BaseEntity {
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

    private String name;
    private double price;
}
