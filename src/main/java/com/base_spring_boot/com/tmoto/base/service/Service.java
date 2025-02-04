package com.base_spring_boot.com.tmoto.base.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;


public interface Service<T> {
    Page<T> find(Pageable page, MultiValueMap<String, String> criteria);

    String getUserAllowedMethodHeaders(Integer id);
}
