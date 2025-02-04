package com.base_spring_boot.com.tmoto.base.service;

import com.base_spring_boot.com.tmoto.base.model.BaseEntity;
import com.base_spring_boot.com.tmoto.base.repository.BaseRepository;
import com.base_spring_boot.com.tmoto.base.repository.criteria.BaseCriteriaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.MappedSuperclass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;


//Imp

import jakarta.persistence.criteria.*;

//
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@MappedSuperclass
public abstract class BaseService<T extends BaseEntity> implements Service<T> {

    protected final BaseRepository<T> baseRepository;
    protected final BaseCriteriaRepository<T> baseCriteriaRepository;


    protected abstract T processCreate(T entity);

    protected abstract T processUpdate(T entity, Integer id);

    protected abstract void processRemove(T entity);

    @Autowired
    public BaseService(BaseRepository<T> baseRepository, BaseCriteriaRepository<T> baseCriteriaRepository) {
        this.baseRepository = baseRepository;
        this.baseCriteriaRepository = baseCriteriaRepository;
    }

    @Override
    @PreAuthorize("this.canQuery()")
    public Page<T> find(Pageable pageable, MultiValueMap<String, String> criteria) {
        // Vérification des critères pour ajuster la recherche
        if (criteria != null && !criteria.isEmpty()) {
            return baseCriteriaRepository.findByCriteria(pageable, criteria);
        } else {
            return baseRepository.findAll(pageable);
        }
    }

    @PreAuthorize("this.canQueryOne(#id)")
    public T getById(Integer id) {
        return baseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found : " + id));
    }

    /**
     * Enregistre une entité dans la base de données après avoir initialisé ses dates de création et de dernière modification.
     * <p>
     * Avant de sauvegarder l'entité, cette méthode définit la date de création et la date de dernière modification
     * en utilisant la méthode {@code generateDate()}.
     * Ensuite, l'entité est envoyée à la méthode { baseRepository#save(Object) } pour persister l'entité dans la base de données.
     * </p>
     *
     * @param entity L'entité à enregistrer. Cette entité doit être une instance d'un type qui étend ou implémente un objet
     *               persistant. La méthode modifie directement cette entité pour ajouter les informations de dates.
     * @return L'entité enregistrée, après sa sauvegarde dans la base de données. Cela peut inclure des modifications apportées
     *         par la méthode de persistance (par exemple, l'ID généré automatiquement).
     * @throws IllegalArgumentException si l'entité est null.
     */
    @Transactional
    @PreAuthorize("this.canCreate()")
    public T save(T entity) {
        entity.setCreationDate(generateDate());
        entity.setLastModifiedDate(generateDate());
        this.processCreate(entity);
        return baseRepository.save(entity);
    }

    /**
     * Met à jour une entité existante en vérifiant son existence et son ID.
     */
    @Transactional
    @PreAuthorize("this.canUpdate(#id)")
    public T update(T entity, Integer id) {
        entity.setLastModifiedDate(generateDate());
        return baseRepository.save(processUpdate(entity, id));
    }




    /**
     * Trouver une entité par son ID.
     */
    @PreAuthorize("this.canQueryOne(#id)")
    public Optional<T> findById(Integer id) {
        return baseRepository.findById(id);
    }

    /**
     * Supprimer une entité par son ID.
     */
    @Transactional
    @PreAuthorize("this.canRemove(#id)")
    public void delete(Integer id) {
        if (!baseRepository.existsById(id)) {
            throw new EntityNotFoundException("Entity with ID " + id + " not found for deletion");
        }
        this.processRemove(getById(id));
    }

    /**
     * Trouver les entités par critères et pagination.
     */
    public Page<T> findByCriteria(Pageable pageInfo, MultiValueMap<String, String> criteria) {
        // Si la taille de la page est nulle ou négative, la définir à 10
        if (pageInfo.getPageSize() <= 0) {
            pageInfo = Pageable.ofSize(10).withPage(pageInfo.getPageNumber());
        }

        // Recherche avec critères
        return baseCriteriaRepository.findByCriteria(pageInfo, criteria);
    }

    /**
     * Générer la date actuelle.
     */
    protected LocalDateTime generateDate() {
        return LocalDateTime.now(); // Générer la date et l'heure actuelles
    }

    /**
     * Convertir la valeur en fonction du type de donnée.
     */
    protected Object getValue(Path<?> pathParam, String value) {
        if (pathParam.getJavaType().equals(Boolean.class)) {
            return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1") || value.equalsIgnoreCase("yes");
        }
        if (pathParam.getJavaType().equals(Date.class)) {
            try {
                // Utilisation de DateTimeFormatter pour analyser la date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
                return java.sql.Timestamp.valueOf(dateTime); // Convertir en Timestamp
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format: " + value);
            }
        }
        return value;
    }
    @Transactional(readOnly = true)
    public boolean canCreate() {
        return false;
    }


    @Transactional(readOnly = true)
    public boolean canUpdate(Integer id) {
        return false;
    }



    @Transactional(readOnly = true)
    public boolean canQuery() {
        return false;
    }



    @Transactional(readOnly = true)
    public boolean canQueryOne(Integer id) {
        return false;
    }



    @Transactional(readOnly = true)
    public boolean canRemove(Integer id) {
        return false;
    }

    @Transactional(readOnly = true)
    public String getUserAllowedMethodHeaders(Integer id) {
        var allowedMethods = new StringBuilder();
        try {
            var canQuery = canQuery();
            if (canQuery) {
                allowedMethods.append("GET,");
            }
        } catch (Exception ignored) {
        }
        try {
            if (id != null) {
                var canQueryOne = canQueryOne(id);
                if (canQueryOne) {
                    allowedMethods.append("GET_ONE,");
                }
            }
        } catch (Exception ignored) {
        }
        try {
            if (id != null) {
                var canUpdate = canUpdate(id);
                if (canUpdate) {
                    allowedMethods.append("UPDATE,");
                }
            }
        } catch (Exception ignored) {
        }
        try {
            if (id != null) {
                var canRemove = canRemove(id);
                if (canRemove) {
                    allowedMethods.append("DELETE,");
                }
            }
        } catch (Exception ignored) {
        }
        try {
            var canCreate = canCreate();
            if (canCreate) {
                allowedMethods.append("POST,");
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return allowedMethods.toString();
    }
}
