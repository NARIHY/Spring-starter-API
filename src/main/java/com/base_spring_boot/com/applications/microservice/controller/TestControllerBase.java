package com.base_spring_boot.com.applications.microservice.controller;

import com.base_spring_boot.com.applications.base.controller.ControllerBase;
import com.base_spring_boot.com.applications.base.service.Service;
import com.base_spring_boot.com.applications.base.utils.filter.JsonModelFilter;
import com.base_spring_boot.com.applications.microservice.persistence.model.TestEntity;
import com.base_spring_boot.com.applications.microservice.service.TestServiceBase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Pour test uniquement, Tester les base de l'applicaiton springboot
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestControllerBase extends ControllerBase<TestEntity> {
    public TestControllerBase(TestServiceBase serviceBase, JsonModelFilter jsonModelFilter) {
        super(serviceBase, jsonModelFilter);
    }

    @Override
    protected Service<TestEntity> getService() {
        return serviceBase;
    }

    @Override
    protected Set<String> getDefaultFilter() {
        return Set.of(
                "id",
                "name",
                "price",
                "creationDate",
                "lastModifiedDate"
        );
    }
}
