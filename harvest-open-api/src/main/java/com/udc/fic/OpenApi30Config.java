package com.udc.fic;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration

@SecurityScheme(
        in = SecuritySchemeIn.HEADER,

        name = "Authorization",
        type = SecuritySchemeType.APIKEY,
        bearerFormat = "JWT",
        scheme = "Bearer",
        description = "Autorizacion con header JWT usando una cabecera esquema. \n Ingrese en el siguiente entrada de texto: 'Bearer' + [espacio] + [token obtenido con signin] \n Ejemplo : \"Bearer eysdafawwda... \" "
)
@OpenAPIDefinition(

        security = {@SecurityRequirement(name = "Authorization")},
        info = @Info(description = "Api para la gestion de recursos de una empresa viticultora",
                title = "Harvest-BackEnd Api", version = "v0.1", contact = @Contact(name = "Mateo Tilves Freijeiro",
                email = "mateo.tilves@udc.es"),
                summary = "SWAGGER UI para la documentacion del API REST")
//        tags = {@Tag(name ="Autenticado",description = ""),@Tag(name ="Trabajadores",description = "")},
)
public class OpenApi30Config {

//    @Bean
//    public GroupedOpenApi openApi(){
//        return GroupedOpenApi.builder().group("custom").packagesToScan("com.udc.fic.harvest.controller").build();
//    }
//
//    @Bean
//    @Primary
//    public SwaggerUiConfigParameters swaggerUiConfigParameters() {
//        return new SwaggerUiConfigParameters();
//    }
}
