package com.base_spring_boot.com.applications.microservice.persistence.model.status;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "status_entity")
public class StatusEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status_name")
    private String statusName;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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
        StatusEntity that = (StatusEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(statusName, that.statusName) && Objects.equals(creationDate, that.creationDate) && Objects.equals(lastModifiedDate, that.lastModifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statusName, creationDate, lastModifiedDate);
    }
}
