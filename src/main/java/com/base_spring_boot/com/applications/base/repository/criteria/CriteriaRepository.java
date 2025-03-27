package com.base_spring_boot.com.applications.base.repository.criteria;

import com.base_spring_boot.com.applications.base.controller.exception.FunctionalErrorException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.xml.bind.DatatypeConverter;
import org.hibernate.metamodel.mapping.ordering.ast.PluralAttributePath;
import org.hibernate.query.sqm.tree.predicate.SqmLikePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Repository
public abstract class CriteriaRepository<E> {
    private final EntityManager entityManager;
    private final Class<E> domainType;
    private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaRepository.class);

    public CriteriaRepository(Class<E> clazz, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.domainType = JpaEntityInformationSupport.getEntityInformation(clazz, entityManager).getJavaType();
    }

    public Page<E> findByCriteria(Pageable pageInfo, MultiValueMap<String, String> criteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        // build 2 queries : 1 for searching elements, 1 for counting results
        CriteriaQuery<E> query = builder.createQuery(domainType);
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

        Root<E> root = query.from(domainType);
        Root<E> countRoot = countQuery.from(domainType);

        Boolean forceOr = Boolean.FALSE;

        if (criteria.getFirst("fromSearch") != null) {
            forceOr = Boolean.valueOf(criteria.getFirst("fromSearch"));
            criteria.remove("fromSearch");
        }

        //remove criteria not in the model attributes
        Set<String> keys = new HashSet<>(criteria.keySet());
        if (!keys.isEmpty()) {
            for (String criterionKey : keys) {
                if (shouldRemoveCriterion(criterionKey, root)) {
                    criteria.remove(criterionKey);
                }
            }
        }

        // adds predicates to both queries, based on criteria
        query = buildSearchQuery(builder, query, root, criteria, forceOr).distinct(forceOr);
        query = addSort(builder, query, root, pageInfo.getSort());
        query.select(root);

        // select distinct answers
        countQuery = buildSearchQuery(builder, countQuery, countRoot, criteria, forceOr.booleanValue()).distinct(true);
        countQuery.select(builder.countDistinct(countRoot));

        // Query for counting elements
        long total = 0;
        try {
            TypedQuery<Long> tqc = entityManager.createQuery(countQuery);
            total = tqc.getSingleResult();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        // Search query
        TypedQuery<E> tq = entityManager.createQuery(query);
        int pageSize = pageInfo.getPageSize();
        tq.setFirstResult(Math.toIntExact(pageInfo.getOffset()));
        tq.setMaxResults(pageSize);

        // search results
        List<E> res = null;
        try {
            res = tq.getResultList();
        } catch (Exception e) {
            LOGGER.error((e.getMessage()));
        }

        // return results page
        return new PageImpl<>(res, pageInfo, total);

    }

    private CriteriaQuery<E> addSort(CriteriaBuilder builder, CriteriaQuery<E> query, Root<E> root, Sort sorts) {

        if (sorts == null) {
            return query;
        }
        List<Order> orders = new ArrayList<>();

        sorts.forEach(sort -> orders.add(sort.isAscending() ? builder.asc(getPath(sort.getProperty(), root))
                : builder.desc(getPath(sort.getProperty(), root))
        ));

        query.orderBy(orders);

        return query;
    }

    private <V> CriteriaQuery<V> buildSearchQuery(CriteriaBuilder builder, CriteriaQuery<V> cq, Root<E> root,
                                                  MultiValueMap<String, String> criteria, boolean forceOr) {

        Predicate predicate = null;
        List<Predicate> predicates = new ArrayList<>();

        Set<Map.Entry<String, List<String>>> entrySet = criteria.entrySet();
        // building OR predicates (different values for one property)
        for (Map.Entry<String, List<String>> entry : entrySet) {
            String param = entry.getKey();
            Path<?> pathParam = getPath(param, root);
            Predicate pTemp = null;
            boolean first = true;
            for (String value : entry.getValue()) {
                if (first) {
                    first = false;
                    pTemp = getPredicate(builder, pathParam, value);
                } else {
                    pTemp = builder.or(pTemp, getPredicate(builder, pathParam, value));
                }
            }
            if (pTemp != null) {
                predicates.add(pTemp);
            }
        }

        boolean firstPredicate = true;
        if (forceOr) {
            boolean firstOrPredicate = true;
            // all like predicates are or....
            Predicate orPredicate = null;
            for (Predicate other : predicates) {
                if (other instanceof SqmLikePredicate) {
                    if (firstOrPredicate) {
                        firstOrPredicate = false;
                        orPredicate = other;
                    } else {
                        orPredicate = builder.or(orPredicate, other);
                    }
                } else {
                    if (firstPredicate) {
                        firstPredicate = false;
                        predicate = other;
                    } else {
                        predicate = builder.and(predicate, other);
                    }

                }
            }
            if (predicate == null) {
                predicate = orPredicate;
            } else if (orPredicate != null) {
                predicate = builder.and(predicate, orPredicate);
            }

        } else {
            // building AND predicates (different properties)
            for (Predicate other : predicates) {
                if (firstPredicate) {
                    firstPredicate = false;
                    predicate = other;
                } else {
                    predicate = builder.and(predicate, other);
                }
            }
        }
        if (predicate != null) {
            cq.where(predicate);
        }

        return cq;

    }

    private Predicate getPredicate(CriteriaBuilder builder, Path path, String value) {
        if (path.getJavaType().getName().contains("List")) {
            return builder.isMember(getValue(path, value), path);
        }
        if (value.contains("%")) {
            return builder.like(builder.lower(path), value.toLowerCase());
        }
        return builder.equal(path, getValue(path, value));
    }

    /**
     * get the Appropriate value, in most case the value itself for enum try to translate in the appropriate enum
     *
     * @param pathParam
     * @param value
     * @return
     */
    private Object getValue(Path<?> pathParam, String value) {

        // for list of Enum
        if (pathParam instanceof PluralAttributePath) {
            PluralAttributePath pluralPath = (PluralAttributePath) pathParam;
            Class<?> bindableJavaType = pluralPath.getPluralAttribute().getClass();
            if (bindableJavaType.isEnum()) {
                try {
                    Method fromValue = bindableJavaType.getDeclaredMethod("fromValue", String.class);
                    return ((Enum<?>) fromValue.invoke(null, value)).name();
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                }
                try {
                    Method valueOf = bindableJavaType.getDeclaredMethod("valueOf", String.class);
                    return ((Enum<?>) valueOf.invoke(null, value)).name();
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    String message = "Invalid value given for criteria";
                    throw new FunctionalErrorException(message + " : " + e.getMessage());
                }
            }
        }

        if (pathParam.getJavaType().getName().contains("Boolean")) {
            if (value.equalsIgnoreCase("1") || value.equalsIgnoreCase("yes")
                    || value.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        // for Enum, Boolean, Integer...
        try {
            Method valueOf = pathParam.getJavaType().getDeclaredMethod("valueOf", String.class);
            return valueOf.invoke(null, value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // method valueOf not available
        }

        // for Enum
        if (pathParam.getJavaType().isEnum()) {
            try {
                Method fromValue = pathParam.getJavaType().getDeclaredMethod("fromValue", String.class);
                return fromValue.invoke(null, value);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                String message = "Invalid value given for criteria";
                throw new FunctionalErrorException(message + " : " + e.getMessage());
            }
        }

        // for dates
        if (pathParam.getJavaType().equals(Date.class)) {
            try {
                return DatatypeConverter.parseDateTime(value).getTime();
            } catch (Exception e) {
                String message = "Invalid date given for criteria";
                throw new FunctionalErrorException(message + " : " + e.getMessage());
            }
        }

        return value;
    }

    private Path<?> getPath(String name, Root<E> root) {

        String[] params = name.split("\\.");

        boolean useGet = true;
        EntityType<?> metaModel = root.getModel();
        for (int i = 0; i < params.length - 1; i++) {
            if (isPlural(params[i], metaModel)) {
                useGet = false;
                break;
            }
            metaModel = (EntityType<?>) metaModel.getSingularAttribute(params[i]).getType();
        }

        if (useGet) {
            return getPathWithGet(params, root);
        }

        return getPathWithJoin(params, root);
    }

    private Path<?> getPathWithGet(String[] params, Root<E> root) {
        Path<?> path = root;
        for (String param : params) {
            path = path.get(param);
        }
        return path;
    }

    private boolean shouldRemoveCriterion(String criterionKey, Path<?> path) {
        Set<Attribute> attributes = ((Root) path).getModel().getAttributes();

        if (!attributes.isEmpty()) {
            for (var attribute : attributes) {
                if (criterionKey.contains(attribute.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    private Path<?> getPathWithJoin(String[] params, Root<E> root) {
        Join<?, ?> path = null;
        if (params.length > 0) {
            path = root.join(params[0], JoinType.LEFT);
        }
        for (int i = 1; i < (params.length - 1); i++) {
            path = path.join(params[i], JoinType.LEFT);
        }
        if (params.length > 1) {
            return path.get(params[params.length - 1]);
        }
        return path;
    }

    private <V> boolean isPlural(String name, ManagedType<V> model) {
        for (PluralAttribute plural : model.getDeclaredPluralAttributes()) {
            if (name.equals(plural.getName())) {
                return true;
            }
        }
        return false;
    }

}
