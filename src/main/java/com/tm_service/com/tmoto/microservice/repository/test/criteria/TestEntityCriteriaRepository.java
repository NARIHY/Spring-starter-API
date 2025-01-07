package com.tm_service.com.tmoto.microservice.repository.test.criteria;

import com.tm_service.com.tmoto.base.repository.criteria.BaseCriteriaRepository;
import com.tm_service.com.tmoto.microservice.model.TestEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public class TestEntityCriteriaRepository extends BaseCriteriaRepository<TestEntity> {

    public TestEntityCriteriaRepository(EntityManager entityManager) {
        super(TestEntity.class, entityManager);
    }

    @Override
    public Page<TestEntity> findByCriteria(Pageable pageInfo, MultiValueMap<String, String> criteria) {
        // Personnaliser la logique ici si nécessaire
        return super.findByCriteria(pageInfo, criteria);
    }
}
