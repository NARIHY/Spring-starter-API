package com.base_spring_boot.com.applications.base.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;


public interface Service<T> {
    Page<T> find(Pageable page, MultiValueMap<String, String> criteria);
    T getById(Integer id);



    T create(T entity);

    T update(T entity, Integer id);

    void delete(Integer id);

    String getUserAllowedMethodHeaders(Integer id);
}
