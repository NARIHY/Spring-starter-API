package com.base_spring_boot.com.tmoto.base.controller;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.base_spring_boot.com.tmoto.base.model.BaseEntity;
import com.base_spring_boot.com.tmoto.base.service.BaseService;
import com.base_spring_boot.com.tmoto.base.utils.SortUtils;
import com.base_spring_boot.com.tmoto.base.utils.UriParser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.Optional;
import java.util.Set;

@MappedSuperclass
public abstract class BaseController<T extends BaseEntity> {

    protected final BaseService<T> baseService;

    @Autowired
    public BaseController(BaseService<T> baseService) {
        this.baseService = baseService;
    }

    // Méthode abstraite pour que les classes enfant définissent les champs à inclure
    protected abstract List<String> getFieldsForResponse();

    // Applique le filtre JSON pour un seul entity
    private MappingJacksonValue applyJsonFilter(T entity) {
        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("baseFilter", SimpleBeanPropertyFilter.filterOutAllExcept(getFieldsForResponse().toArray(new String[0])));
        MappingJacksonValue wrapper = new MappingJacksonValue(entity);
        wrapper.setFilters(filters);
        return wrapper;
    }

    private MappingJacksonValue applyJsonFilterOnList(List<T> content, Set<String> fields) {
        // Si des champs sont spécifiés dans `fields`, on applique un filtre
        if (fields != null && !fields.isEmpty()) {
            // Création du filtre avec les champs spécifiés
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(fields.toArray(new String[0]));

            // Application du filtre via SimpleFilterProvider
            FilterProvider filters = new SimpleFilterProvider().addFilter("baseFilter", filter);

            // Application des filtres à la liste d'entités
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(content);
            mappingJacksonValue.setFilters(filters);

            return mappingJacksonValue;
        }

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("baseFilter", SimpleBeanPropertyFilter.filterOutAllExcept(getFieldsForResponse().toArray(new String[0])));

        // Retourner la liste sans filtrage si `fields` est vide ou null
        MappingJacksonValue wrapper = new MappingJacksonValue(content);

        wrapper.setFilters(filters);
        return wrapper;

    }




    // CREATE
    @PostMapping
    public ResponseEntity<MappingJacksonValue> create(@RequestBody T entity) {
        T createdEntity = baseService.save(entity);
        MappingJacksonValue filteredEntity = applyJsonFilter(createdEntity);
        return new ResponseEntity<>(filteredEntity, HttpStatus.CREATED);
    }

    // READ - Get all entities
    @GetMapping( produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MappingJacksonValue> getAll(@Schema(implementation = Object.class) @RequestParam MultiValueMap<String, String> queryParameters,
                                                      @RequestParam(name = "fields", required = false) Set<String> fields,
                                                      @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
                                                      @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
                                                      @RequestParam(name = "sort", required = false) String sort) {

        // Extraction des critères de filtre à partir des paramètres de la requête
        MultiValueMap<String, String> criteria = UriParser.extractCriteria(queryParameters);

        // Gestion de la pagination
        if (limit < 0) {
            limit = Integer.MAX_VALUE;  // Si limit est négatif, on considère une limite maximale
        }
        if (offset < 0) {
            offset = 0;  // Si offset est négatif, on commence à partir de zéro
        }

        int page = offset / limit;  // Calcul du numéro de page

        // Création de l'objet Pageable en fonction des paramètres de tri et de pagination
        Pageable pageable = PageRequest.of(page, limit, SortUtils.convertSortParameter(sort));

        // Récupération des données filtrées et paginées
        Page<T> resultPage = baseService.find(pageable, criteria);

        // Construction des en-têtes HTTP pour inclure le nombre total d'éléments et autres informations
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("x-total-count", String.valueOf(resultPage.getTotalElements()));
        responseHeaders.set("x-result-count", String.valueOf(resultPage.getNumberOfElements()));
        responseHeaders.set("x-user-allowed-methods", baseService.getUserAllowedMethodHeaders(null));

        // Application du filtre JSON à toutes les entités récupérées
        MappingJacksonValue filteredEntities =  applyJsonFilterOnList(resultPage.getContent(), fields);


        // Retourner la réponse avec les en-têtes et les entités filtrées
        // Retourner la réponse avec les en-têtes et les entités filtrées
        return new ResponseEntity<>(filteredEntities, responseHeaders, HttpStatus.OK);

    }



    // READ - Get one entity by ID
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MappingJacksonValue> getById(@PathVariable("id") Long id) {
        Optional<T> entity = baseService.findById(id);
        if (entity.isPresent()) {
            MappingJacksonValue filteredEntity = applyJsonFilter(entity.get());
            return ResponseEntity.ok(filteredEntity);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MappingJacksonValue> update(@PathVariable("id") Long id, @RequestBody T entity) {
        Optional<T> existingEntity = baseService.findById(id);
        if (existingEntity.isPresent()) {
            entity.setId(id);
            T updatedEntity = baseService.update(entity, id);
            MappingJacksonValue filteredEntity = applyJsonFilter(updatedEntity);
            return new ResponseEntity<>(filteredEntity, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<T> delete(@PathVariable Long id) {
        baseService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
