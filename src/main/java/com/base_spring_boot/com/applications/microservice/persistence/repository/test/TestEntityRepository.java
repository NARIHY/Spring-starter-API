package com.base_spring_boot.com.applications.microservice.persistence.repository.test;

import com.base_spring_boot.com.applications.microservice.persistence.model.TestEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntityRepository extends org.springframework.data.jpa.repository.JpaRepository<TestEntity, Integer> {
    // Vous pouvez ajouter des méthodes personnalisées pour TestEntity ici si nécessaire
}
