package com.base_spring_boot.com.tmoto.microservice.repository.test;

import com.base_spring_boot.com.tmoto.base.repository.BaseRepository;
import com.base_spring_boot.com.tmoto.microservice.model.TestEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntityRepository extends BaseRepository<TestEntity> {
    // Vous pouvez ajouter des méthodes personnalisées pour TestEntity ici si nécessaire
}
