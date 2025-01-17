package com.base_spring_boot.com.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerUiConfig {
    @Bean
    public OpenAPI referentielOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("TM-service")
                        .description("TM-service Services API")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public OpenApiCustomizer customOpenApiCustomiser() {
        return openApi -> {
            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperations().forEach(operation -> {
                    String operationId = operation.getOperationId();
                    if (operationId != null && operationId.contains("{entity}")) {
                        String entityName = extractEntityNameFromPath(path);
                        operation.operationId(operationId.replace("{entity}", entityName).split("_")[0]);
                    }
                });
            });
        };
    }

    private String extractEntityNameFromPath(String path) {
        // Logic to extract entity name from path
        String[] entity = path.split("/");
        if (entity.length > 3) {
            String model = entity[3];
            var modelArray = model.split("-");
            StringBuilder result = new StringBuilder();
            for (String i: modelArray) {
                i = i.substring(0, 1).toUpperCase() + i.substring(1);
                result.append(i);
            }
            return result.toString();
        }
        return "Entity";
    }
}
