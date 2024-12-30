package com.tm_service.com.tmoto.microservice.repository;

import com.tm_service.com.tmoto.base.repository.BaseRepository;
import com.tm_service.com.tmoto.microservice.model.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntityRepository extends BaseRepository<TestEntity> {
    // Vous pouvez ajouter des méthodes personnalisées pour TestEntity ici si nécessaire
}
