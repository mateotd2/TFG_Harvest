package com.udc.fic;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration

@SecurityScheme(
        name = "bearerAuth",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Autorizacion con header JWT usando una cabecera esquema. \n Ingrese en el siguiente entrada de texto: 'Bearer' + [espacio] + [token obtenido con signin] \n Ejemplo : \"Bearer eysdafawwda... \" "
)

@OpenAPIDefinition(

        info = @Info(description = "Api para la gestion de recursos de una empresa viticultora",
                title = "Harvest-BackEnd Api", version = "v0.1", contact = @Contact(name = "Mateo Tilves Freijeiro",
                email = "mateo.tilves@udc.es"),
                summary = "SWAGGER UI para la documentacion del API REST")

)
public class OpenApi30Config {

}
