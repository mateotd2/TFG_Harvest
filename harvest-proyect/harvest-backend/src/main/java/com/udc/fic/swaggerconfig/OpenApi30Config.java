package com.udc.fic.swaggerconfig;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(description = "Api para la gestion de recursos de una empresa viticultora",title = "Harvest-BackEnd Api", version = "v0.1", contact = @Contact(name = "Mateo Tilves Freijeiro", email = "mateo.tilves@udc.es")
        , summary = "SWAGGER UI para la documentacion del API REST"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApi30Config {
}
