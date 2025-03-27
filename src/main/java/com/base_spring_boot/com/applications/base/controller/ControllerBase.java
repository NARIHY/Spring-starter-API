package com.base_spring_boot.com.applications.base.controller;

import com.base_spring_boot.com.applications.base.controller.exception.MaxRequestGivenException;
import com.base_spring_boot.com.applications.base.service.Service;
import com.base_spring_boot.com.applications.base.service.ServiceBase;
import com.base_spring_boot.com.applications.base.service.rateLimiter.RateLimiterService;
import com.base_spring_boot.com.applications.base.service.rateLimiter.RequestQueueService;
import com.base_spring_boot.com.applications.base.utils.SortUtils;
import com.base_spring_boot.com.applications.base.utils.UriParser;
import com.base_spring_boot.com.applications.base.utils.filter.JsonModelFilter;
import com.base_spring_boot.com.applications.base.utils.filter.JsonRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.springframework.core.env.Environment;

/**
 * Contrôleur de base pour la gestion des entités.
 * Il gère des fonctionnalités telles que la gestion des filtres JSON, la pagination,
 * la limitation de débit, et la validation des requêtes.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
public abstract class ControllerBase<T> {

    protected final ServiceBase<T> serviceBase;

    @Autowired
    protected RateLimiterService rateLimiterService;

    @Autowired
    protected RequestQueueService requestQueueService;

    // Filtrage par défaut
    protected static final List<String> DEFAULT_FILTERS = List.of("ALL");

    // Services abstraits à implémenter dans les classes concrètes
    protected abstract Service<T> getService();
    protected abstract Set<String> getDefaultFilter();

    protected final JsonModelFilter jsonModelFilter;

    @Autowired
    private Environment environment;

    /**
     * Constructeur injectant les services nécessaires pour le contrôleur.
     *
     * @param serviceBase Service de gestion des entités de type T
     * @param jsonModelFilter Service de filtrage des modèles JSON
     */
    @Autowired
    public ControllerBase(ServiceBase<T> serviceBase, JsonModelFilter jsonModelFilter) {
        this.serviceBase = serviceBase;
        this.jsonModelFilter = jsonModelFilter;
    }



    // ------------------ Méthodes CRUD ------------------

    /**
     * Crée une nouvelle entité et la sauvegarde dans la base de données.
     *
     * @param entity L'entité à créer
     * @return Une réponse contenant l'entité créée et un statut HTTP 201 (Créé)
     */
    @Operation(operationId = "create{entity}")
    @PostMapping
    public ResponseEntity<Object> create(HttpServletRequest request, @RequestBody T entity) {
        validateRequest(request);
        return new ResponseEntity<>(jsonModelFilter.getJsonModel(getService().create(entity),
                new JsonRepresentation(getRequestedFilter(null))), HttpStatus.CREATED);
    }

    /**
     * Récupère toutes les entités avec une possibilité de filtrage, de tri, et de pagination.
     *
     * @param queryParameters Paramètres de requête à analyser pour un filtrage
     * @param fields Ensemble de champs à inclure dans la réponse
     * @param limit Limite de résultats à renvoyer
     * @param offset Décalage des résultats
     * @param sort Paramètre de tri des résultats
     * @return Une réponse contenant la liste des entités et les informations de pagination
     */
    @Operation(operationId = "find{entity}")
    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<?>> find(@Schema(implementation = Object.class) @RequestParam MultiValueMap<String, String> queryParameters,
                                        @RequestParam(name = "fields", required = false) Set<String> fields,
                                        @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
                                        @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
                                        @RequestParam(name = "sort", required = false) String sort) {

        MultiValueMap<String, String> criteria = UriParser.extractCriteria(queryParameters, DEFAULT_FILTERS);
        limit = limit < 0 ? Integer.MAX_VALUE : limit;
        offset = offset < 0 ? 0 : offset;

        Pageable pageable = PageRequest.of(offset, limit, SortUtils.convertSortParameter(sort));

        Page<T> resultPage = getService().find(pageable, criteria);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("x-total-count", String.valueOf(resultPage.getTotalElements()));
        responseHeaders.set("x-result-count", String.valueOf(resultPage.getNumberOfElements()));
        responseHeaders.set("x-user-allowed-methods", getService().getUserAllowedMethodHeaders(null));

        return new ResponseEntity<>(jsonModelFilter.getJsonModels(resultPage.getContent(),
                new JsonRepresentation(getRequestedFilter(fields))), responseHeaders, HttpStatus.OK);
    }

    /**
     * Récupère une entité spécifique par son ID.
     *
     * @param id L'ID de l'entité à récupérer
     * @param fields Ensemble de champs à inclure dans la réponse
     * @return Une réponse contenant l'entité demandée ou un statut HTTP 404 (Non trouvé)
     */
    @Operation(operationId = "get{entity}")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> get(@PathVariable Integer id,
                                      @RequestParam(name = "fields", required = false) Set<String> fields) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("x-user-allowed-methods", getService().getUserAllowedMethodHeaders(id));
        T apiEntity = getService().getById(id);
        return new ResponseEntity<>(jsonModelFilter.getJsonModel(apiEntity,
                new JsonRepresentation(getRequestedFilter(fields))), responseHeaders, HttpStatus.OK);
    }

    /**
     * Met à jour une entité existante.
     *
     * @param id L'ID de l'entité à mettre à jour
     * @param entity L'entité mise à jour
     * @return Une réponse contenant l'entité mise à jour ou un statut HTTP 404 (Non trouvé)
     */
    @Operation(operationId = "update{entity}")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(HttpServletRequest request, @PathVariable Integer id, @Valid @RequestBody T entity) {
        validateRequest(request);
        return new ResponseEntity<>(jsonModelFilter.getJsonModel(getService().update(entity, id),
                new JsonRepresentation(getRequestedFilter(null))), HttpStatus.OK);
    }

    /**
     * Supprime une entité par son ID.
     *
     * @param id L'ID de l'entité à supprimer
     * @return Une réponse avec un statut HTTP 200 (OK) si la suppression a réussi
     */
    @Operation(operationId = "delete{entity}")
    @DeleteMapping("/{id}")
    public ResponseEntity<T> delete(HttpServletRequest request, @PathVariable Integer id) {
        getService().delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ------------------ Méthode de validation ------------------

    /**
     * Valide la requête en vérifiant la limitation du débit et l'ajout à la file d'attente des requêtes.
     *
     * @param request La requête HTTP en cours
     */
    private void validateRequest(HttpServletRequest request) {
        // Vérification de la limitation de débit sauf en mode test
        if (!Arrays.asList(environment.getActiveProfiles()).contains("testing")) {
            if (!rateLimiterService.tryConsumeRequest()) {
                throw new MaxRequestGivenException("Trop de requêtes. Veuillez réessayer plus tard.");
            }
            try {
                requestQueueService.addRequestToQueue(request);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Service indisponible. Veuillez réessayer plus tard.");
            }
        }
    }

    /**
     * Récupère les filtres demandés pour l'entité.
     *
     * @param fields Champs à inclure dans la réponse
     * @return Les filtres appliqués
     */
    protected Set<String> getRequestedFilter(Set<String> fields) {
        if (fields == null || fields.isEmpty()) {
            fields = getDefaultFilter();
        }
        return fields;
    }
}
