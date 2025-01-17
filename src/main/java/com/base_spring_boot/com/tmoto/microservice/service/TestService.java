package com.base_spring_boot.com.tmoto.microservice.service;

import com.base_spring_boot.com.tmoto.base.controller.exception.FunctionnalException;
import com.base_spring_boot.com.tmoto.base.service.BaseService;
import com.base_spring_boot.com.tmoto.microservice.model.TestEntity;
import com.base_spring_boot.com.tmoto.microservice.repository.test.TestEntityRepository;
import com.base_spring_boot.com.tmoto.microservice.repository.test.criteria.TestEntityCriteriaRepository;
import org.springframework.stereotype.Service;

@Service
public class TestService extends BaseService<TestEntity>  {

    // Constructor that correctly passes the specific repository to the base service
    public TestService(TestEntityRepository testEntityRepository, TestEntityCriteriaRepository testEntityCriteriaRepository) {
        super(testEntityRepository, testEntityCriteriaRepository);
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
        return entity;  // Retourner l'entité modifiée
    }

    /**
     * Implémentation spécifique à la mise à jour d'une entité TestEntity.
     */
    @Override
    protected TestEntity processUpdate(TestEntity entity, Long id) {
        // Validation que l'entité existe ou que l'ID est valide
        TestEntity existingEntity = baseRepository.findById(id)
                .orElseThrow(() -> new FunctionnalException("Entité non trouvée pour la mise à jour."));

        // Mise à jour du champ 'name' si la nouvelle valeur est non vide
        if (entity.getName() != null && !entity.getName().isEmpty()) {
            existingEntity.setName(entity.getName());
        }

        // Mise à jour du champ 'price' si la nouvelle valeur est non nulle
        if (!String.valueOf(entity.getPrice()).isEmpty()) {
            existingEntity.setPrice(entity.getPrice());
        }

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
        baseRepository.delete(entity);
    }

    @Override
    public boolean canCreate() {
        return true;
    }

    @Override
    public boolean canUpdate(Long id) {
        return true;
    }

    @Override
    public boolean canQuery() {
        return true;
    }

    @Override
    public boolean canQueryOne(Long id) {
        return true;
    }

    @Override
    public boolean canRemove(Long id) {
        return true;
    }
}
