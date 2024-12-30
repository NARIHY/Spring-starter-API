package com.tm_service.com.tmoto.base.controller;


import com.tm_service.com.tmoto.base.model.BaseEntity;
import com.tm_service.com.tmoto.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

public abstract class BaseController<T extends BaseEntity> {

    protected final BaseService<T> baseService;

    @Autowired
    public BaseController(BaseService<T> baseService) {
        this.baseService = baseService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<T> create(@RequestBody T entity) {
        T createdEntity = baseService.save(entity);
        return new ResponseEntity<>(createdEntity, HttpStatus.CREATED);
    }

    // READ - Get all entities
    @GetMapping
    public ResponseEntity<List<T>> getAll() {
        List<T> entities = baseService.findAll();
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    // READ - Get one entity by ID
    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable("id") Long id) {
        Optional<T> entity = baseService.findById(id);
        return entity.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable("id") Long id, @RequestBody T entity) {
        Optional<T> existingEntity = baseService.findById(id);
        if (existingEntity.isPresent()) {
            entity.setId(id); // Assurez-vous que l'entité a l'ID mis à jour
            T updatedEntity = baseService.save(entity);
            return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Optional<T> entity = baseService.findById(id);
        if (entity.isPresent()) {
            baseService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}