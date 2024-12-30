package com.tm_service.com.tmoto.microservice.controller;

import com.tm_service.com.tmoto.base.controller.BaseController;
import com.tm_service.com.tmoto.microservice.model.TestEntity;
import com.tm_service.com.tmoto.microservice.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController extends BaseController<TestEntity> {
    @Autowired
    public TestController(TestService testService) {
        super(testService);
    }

}
