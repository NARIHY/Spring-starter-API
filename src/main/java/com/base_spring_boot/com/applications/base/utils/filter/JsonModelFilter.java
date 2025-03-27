package com.base_spring_boot.com.applications.base.utils.filter;
import java.util.List;

public interface JsonModelFilter {
    Object getJsonModel(final Object resource, JsonRepresentation jsonRepresentation);

    List<?> getJsonModels(final List<?> resources, JsonRepresentation jsonRepresentation);
}