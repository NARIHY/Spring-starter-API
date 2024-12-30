package com.tm_service.com.tmoto.base.repository;

import com.tm_service.com.tmoto.base.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
    // Vous pouvez ajouter des méthodes personnalisées si nécessaire
}