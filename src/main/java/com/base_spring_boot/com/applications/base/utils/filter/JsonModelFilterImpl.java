package com.base_spring_boot.com.applications.base.utils.filter;
import com.base_spring_boot.com.applications.base.controller.exception.FunctionalErrorException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class JsonModelFilterImpl implements JsonModelFilter {

    public Object getJsonModel(final Object resource, JsonRepresentation jsonRepresentation) {
        if (jsonRepresentation.getAttributes() == null) {
            return resource;
        }
        try {
            return JacksonFilter.createNode(resource, jsonRepresentation);
        } catch (Exception e) {
            throw new FunctionalErrorException("Incorrect attributes : " + jsonRepresentation.getAttributes());
        }
    }

    public List<?> getJsonModels(final List<?> resources, JsonRepresentation jsonRepresentation) {
        if (jsonRepresentation.getAttributes() == null) {
            return resources;
        }
        try {
            return JacksonFilter.createNodes(resources, jsonRepresentation);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FunctionalErrorException("Incorrect attributes : " + jsonRepresentation.getAttributes());
        }
    }
}