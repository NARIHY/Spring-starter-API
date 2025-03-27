package com.base_spring_boot.com.applications.microservice.persistence.repository.test.criteria;

import com.base_spring_boot.com.applications.base.repository.criteria.CriteriaRepository;
import com.base_spring_boot.com.applications.microservice.persistence.model.TestEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public class TestEntityCriteriaRepository extends CriteriaRepository<TestEntity> {

    public TestEntityCriteriaRepository(EntityManager entityManager) {
        super(TestEntity.class, entityManager);
    }

    @Override
    public Page<TestEntity> findByCriteria(Pageable pageInfo, MultiValueMap<String, String> criteria) {
        // Personnaliser la logique ici si nécessaire
        return super.findByCriteria(pageInfo, criteria);
    }
}
