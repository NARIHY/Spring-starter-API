package com.base_spring_boot.com.applications.base.service;

import com.base_spring_boot.com.security.service.OpenIdService;
import com.base_spring_boot.com.applications.base.repository.criteria.CriteriaRepository;
import com.base_spring_boot.com.applications.microservice.persistence.model.status.StatusEntity;
import com.base_spring_boot.com.applications.microservice.persistence.repository.status.StatusEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
import java.util.function.Consumer;

public abstract class ServiceBase<T> implements com.base_spring_boot.com.applications.base.service.Service<T> {
    protected abstract JpaRepository<T, Integer> getRepository();

    protected abstract CriteriaRepository<T> getCriteriaRepository();

    protected abstract T processCreate(T entity);

    protected abstract T processUpdate(T entity, Integer id);

    protected abstract void processRemove(T entity);

    protected final StatusEntityRepository statusEntityRepository;

    protected final OpenIdService openIdService;

    protected String getStatusName() {
        return null;
    }

    protected String getStatusValid() {
        return null;
    }

    private void getCriteriaStatusFilter(MultiValueMap<String, String> criteriaMap) {
        if (getStatusName() != null) {
            // Toujours garantir qu'un statut valide existe
            String statusValid = getStatusValid(); // Assurez-vous de toujours obtenir un statut valide
            if (!criteriaMap.containsKey(getStatusName())) {
                criteriaMap.add(getStatusName(), statusValid);
            } else if (criteriaMap.containsKey(getStatusName()) && criteriaMap.get(getStatusName()).contains("-1")) {
                criteriaMap.remove(getStatusName());
            }
        }
    }

    @Autowired
    public ServiceBase(OpenIdService openIdService,  StatusEntityRepository statusEntityRepository) {
        this.openIdService = openIdService;
        this.statusEntityRepository = statusEntityRepository;
    }

    @Override
    @PreAuthorize("this.canQuery()")
    public Page<T> find(Pageable page, MultiValueMap<String, String> criteria) {
        getCriteriaStatusFilter(criteria);
        if (!criteria.isEmpty()) {
            return getCriteriaRepository().findByCriteria(page, criteria);
        } else {
            return getRepository().findAll(page);
        }
    }

    @PreAuthorize("this.canQueryOne(#id)")
    public T getById(Integer id) {
        return getRepository().findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found : " + id));
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
    public T create(T entity) {
        entity = processCreate(entity);
        return getRepository().save(entity);
    }

    /**
     * Met à jour une entité existante en vérifiant son existence et son ID.
     */
    @Transactional
    @PreAuthorize("this.canUpdate(#id)")
    public T update(T entity, Integer id) {
        return getRepository().save(processUpdate(entity, id));
    }

    /**
     * Trouver une entité par son ID.
     */
    @PreAuthorize("this.canQueryOne(#id)")
    public T findById(Integer id) {
        return getRepository().findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found : " + id));
    }

    /**
     * Supprimer une entité par son ID.
     */
    @Transactional
    @PreAuthorize("this.canRemove(#id)")
    public void delete(Integer id) {
        processRemove(getById(id));
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

    public StatusEntity setStatusValid()
    {
        Optional<StatusEntity> opStatus =this.statusEntityRepository.findById(1);
        return opStatus.get();
    }

    public StatusEntity setStatusInvalid()
    {
        Optional<StatusEntity> opStatus =this.statusEntityRepository.findById(2);
        return opStatus.get();
    }

    public StatusEntity setStatusPending()
    {
        Optional<StatusEntity> opStatus = this.statusEntityRepository.findById(3);
        return opStatus.get();
    }

    public StatusEntity setStatusCancel()
    {
        Optional<StatusEntity> opStatus = this.statusEntityRepository.findById(4);
        return opStatus.get();
    }

    public StatusEntity setStatusFinish()
    {
        Optional<StatusEntity> opStatus = this.statusEntityRepository.findById(5);
        return opStatus.get();
    }

    // Méthode utilitaire pour mettre à jour un champ seulement si la valeur n'est pas nulle
    public <T> void updateFieldIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }
}
