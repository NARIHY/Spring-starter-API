package com.base_spring_boot.com.applications.microservice.persistence.repository.status.criteria;

import com.base_spring_boot.com.applications.base.repository.criteria.BaseCriteriaRepository;
import com.base_spring_boot.com.applications.microservice.persistence.model.status.StatusEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class StatusEntityCriteriaRepository extends BaseCriteriaRepository<StatusEntity> {
    public StatusEntityCriteriaRepository(EntityManager entityManager) {
        super(StatusEntity.class,entityManager);
    }
}
