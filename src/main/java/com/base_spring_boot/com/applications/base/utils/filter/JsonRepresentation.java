package com.base_spring_boot.com.applications.base.utils.filter;

import java.util.LinkedHashSet;
import java.util.Set;

public final class JsonRepresentation {

    private Set<String> attributes = new LinkedHashSet<>();

    public JsonRepresentation(Set<String> attributes) {
        if (attributes == null) {
            this.attributes = null;
        } else
            this.attributes.addAll(attributes);
    }

    public JsonRepresentation add(String attributePath) {
        this.attributes.add(attributePath);
        return this;
    }

    public JsonRepresentation add(JsonRepresentation jsonRepresentation) {
        this.attributes.addAll(jsonRepresentation.getAttributes());
        return this;
    }

    public Set<String> getAttributes() {
        return attributes;
    }

}