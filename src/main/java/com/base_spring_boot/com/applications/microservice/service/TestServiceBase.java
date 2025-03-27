package com.base_spring_boot.com.applications.microservice.service;

import com.base_spring_boot.com.security.service.OpenIdService;
import com.base_spring_boot.com.applications.base.controller.exception.FunctionnalException;
import com.base_spring_boot.com.applications.base.repository.criteria.CriteriaRepository;
import com.base_spring_boot.com.applications.base.service.ServiceBase;
import com.base_spring_boot.com.applications.microservice.persistence.model.TestEntity;
import com.base_spring_boot.com.applications.microservice.persistence.repository.status.StatusEntityRepository;
import com.base_spring_boot.com.applications.microservice.persistence.repository.test.TestEntityRepository;
import com.base_spring_boot.com.applications.microservice.persistence.repository.test.criteria.TestEntityCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class TestServiceBase extends ServiceBase<TestEntity> {

    private final TestEntityRepository testEntityRepository;
    private final TestEntityCriteriaRepository testEntityCriteriaRepository;
    // Constructor that correctly passes the specific repository to the base service
    public TestServiceBase(OpenIdService openIdService, TestEntityRepository testEntityRepository, TestEntityCriteriaRepository testEntityCriteriaRepository, StatusEntityRepository statusEntityRepository) {
        super(openIdService,statusEntityRepository);
        this.testEntityRepository = testEntityRepository;
        this.testEntityCriteriaRepository = testEntityCriteriaRepository;
    }

    @Override
    protected JpaRepository<TestEntity, Integer> getRepository() {
        return testEntityRepository;
    }

    @Override
    protected CriteriaRepository<TestEntity> getCriteriaRepository() {
        return testEntityCriteriaRepository;
    }


    /**
     * Implémentation spécifique à la création d'une entité TestEntity.
     * Exemple : vous pouvez valider ou ajuster certaines valeurs avant la création.
     */
    @Override
    protected TestEntity processCreate(TestEntity entity) {
        // Logique métier ou validation avant création
        if (entity.getName() == null || entity.getName().isEmpty()) {
            throw new FunctionnalException("Le nom de l'entité ne peut pas être vide.");
        }
        entity.setCreationDate(generateDate());
        entity.setLastModifiedDate(generateDate());
        return entity;  // Retourner l'entité modifiée
    }

    /**
     * Implémentation spécifique à la mise à jour d'une entité TestEntity.
     */
    @Override
    protected TestEntity processUpdate(TestEntity entity, Integer id) {
        // Validation que l'entité existe ou que l'ID est valide
        TestEntity existingEntity = getRepository().findById(id)
                .orElseThrow(() -> new FunctionnalException("Entité non trouvée pour la mise à jour."));

        // Mise à jour du champ 'name' si la nouvelle valeur est non vide
        if (entity.getName() != null && !entity.getName().isEmpty()) {
            existingEntity.setName(entity.getName());
        }

        // Mise à jour du champ 'price' si la nouvelle valeur est non nulle
        if (!String.valueOf(entity.getPrice()).isEmpty()) {
            existingEntity.setPrice(entity.getPrice());
        }
        existingEntity.setLastModifiedDate(generateDate());
        return existingEntity;  // Retourner l'entité mise à jour
    }

    /**
     * Implémentation spécifique à la suppression d'une entité TestEntity.
     */
    @Override
    protected void processRemove(TestEntity entity) {
        if (entity == null) {
            throw new FunctionnalException("Entity not found for deletion.");
        }
        // Sinon, procéder à la suppression réelle
        getRepository().delete(entity);
    }

    @Override
    public boolean canCreate() {
        return openIdService.isAdmin() || openIdService.isDriver() || openIdService.isClient();
    }

    @Override
    public boolean canUpdate(Integer id) {
        return  openIdService.isAdmin() || openIdService.isDriver() || openIdService.isClient();
    }

    @Override
    public boolean canQuery() {
        return  openIdService.isAdmin() || openIdService.isDriver() || openIdService.isClient();
    }

    @Override
    public boolean canQueryOne(Integer id) {
        return  openIdService.isAdmin() || openIdService.isDriver() || openIdService.isClient();
    }

    @Override
    public boolean canRemove(Integer id) {
        return  openIdService.isAdmin() || openIdService.isDriver() || openIdService.isClient();
    }
}
