package com.tm_service.com.tmoto.base.service;

import com.tm_service.com.tmoto.base.model.BaseEntity;
import com.tm_service.com.tmoto.base.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public abstract class BaseService<T extends BaseEntity> {

    protected final BaseRepository<T> baseRepository;

    @Autowired
    public BaseService(BaseRepository<T> baseRepository) {
        this.baseRepository = baseRepository;
    }

    public T save(T entity) {
        return baseRepository.save(entity);
    }

    public List<T> findAll() {
        return baseRepository.findAll();
    }

    public Optional<T> findById(Long id) {
        return baseRepository.findById(id);
    }

    public void delete(Long id) {
        baseRepository.deleteById(id);
    }
}