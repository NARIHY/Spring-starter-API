package com.tm_service.com.tmoto.base.repository.criteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseCriteriaRepository<E> {

    private final EntityManager entityManager;
    private final Class<E> domainType;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCriteriaRepository.class);

    public BaseCriteriaRepository(Class<E> clazz, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.domainType = clazz;
    }

    // Recherche générique avec critères
    public Page<E> findByCriteria(Pageable pageInfo, MultiValueMap<String, String> criteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<E> query = builder.createQuery(domainType);
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

        Root<E> root = query.from(domainType);
        Root<E> countRoot = countQuery.from(domainType);

        Set<String> keys = new HashSet<>(criteria.keySet());
        if (!keys.isEmpty()) {
            for (String criterionKey : keys) {
                if (shouldRemoveCriterion(criterionKey, root)) {
                    criteria.remove(criterionKey);
                }
            }
        }

        query = buildSearchQuery(builder, query, root, criteria);
        countQuery = buildSearchQuery(builder, countQuery, countRoot, criteria);

        long total = executeCountQuery(countQuery);
        List<E> results = executeSearchQuery(query, pageInfo);

        return new PageImpl<>(results, pageInfo, total);
    }

    // Méthode pour exécuter la requête de comptage des résultats
    private long executeCountQuery(CriteriaQuery<Long> countQuery) {
        TypedQuery<Long> tq = entityManager.createQuery(countQuery);
        return tq.getSingleResult();
    }

    // Méthode pour exécuter la requête de recherche avec pagination
    private List<E> executeSearchQuery(CriteriaQuery<E> query, Pageable pageInfo) {
        TypedQuery<E> tq = entityManager.createQuery(query);
        tq.setFirstResult((int) pageInfo.getOffset());
        tq.setMaxResults(pageInfo.getPageSize());
        return tq.getResultList();
    }

    // Méthode générique pour construire la requête de recherche
    private <V> CriteriaQuery<V> buildSearchQuery(CriteriaBuilder builder, CriteriaQuery<V> cq, Root<E> root,
                                                  MultiValueMap<String, String> criteria) {
        Predicate predicate = builder.conjunction();  // Utilise une conjonction par défaut (AND)

        for (String key : criteria.keySet()) {
            Path<?> path = getPath(key, root);
            List<String> values = criteria.get(key);
            Predicate tempPredicate = createPredicateForValues(builder, path, values);
            predicate = builder.and(predicate, tempPredicate);
        }

        cq.where(predicate);
        return cq;
    }

    // Créer un prédicat pour un champ donné et une liste de valeurs
    private Predicate createPredicateForValues(CriteriaBuilder builder, Path<?> path, List<String> values) {
        Predicate predicate = null;
        for (String value : values) {
            if (predicate == null) {
                predicate = builder.equal(path, value);
            } else {
                predicate = builder.or(predicate, builder.equal(path, value));
            }
        }
        return predicate;
    }

    // Récupérer le chemin du paramètre basé sur son nom (support pour les chemins imbriqués)
    private Path<?> getPath(String name, Root<E> root) {
        String[] params = name.split("\\.");
        Path<?> path = root;
        for (String param : params) {
            path = path.get(param);
        }
        return path;
    }

    // Vérifie si un critère peut être supprimé, c'est-à-dire si le champ n'existe pas dans le modèle
    private boolean shouldRemoveCriterion(String criterionKey, Path<?> path) {
        return false; // Implémentation simplifiée, à personnaliser si nécessaire
    }
}
