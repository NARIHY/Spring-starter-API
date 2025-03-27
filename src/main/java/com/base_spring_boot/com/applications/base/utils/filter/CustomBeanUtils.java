package com.base_spring_boot.com.applications.base.utils.filter;

import org.springframework.beans.PropertyAccessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CustomBeanUtils {

    // Create a logger instance
    private static final Logger log = LoggerFactory.getLogger(CustomBeanUtils.class);

    private CustomBeanUtils() {
    }

    public static Object getNestedProperty(Object bean, String propertyName) {
        try {
            var propertyAccessr = PropertyAccessorFactory.forBeanPropertyAccess(bean);
            return propertyAccessr.getPropertyValue(propertyName);
        } catch (Exception e) {
            log.warn("Cannot find property with name {}", propertyName); // Use parameterized logging
        }
        return null;
    }
}
