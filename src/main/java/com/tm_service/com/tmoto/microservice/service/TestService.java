package com.tm_service.com.tmoto.microservice.service;

import com.tm_service.com.tmoto.base.repository.BaseRepository;
import com.tm_service.com.tmoto.base.service.BaseService;
import com.tm_service.com.tmoto.microservice.model.TestEntity;
import com.tm_service.com.tmoto.microservice.repository.TestEntityRepository;
import org.springframework.stereotype.Service;

@Service
public class TestService extends BaseService<TestEntity> {
    public TestService(TestEntityRepository testEntityRepository) {
        super(testEntityRepository);
    }
}
