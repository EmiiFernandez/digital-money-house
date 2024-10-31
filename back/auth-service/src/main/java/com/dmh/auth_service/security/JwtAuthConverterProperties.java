package com.dmh.auth_service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/*
 * Clase de configuracion para el convertidor de autenticacion JWT.
 * Esta clase actua como un DTO (Objeto de Transferencia de Datos) que carga la configuracion
 * del convertidor de autenticacion JWT desde las propiedades de la aplicacion.
 *//*
@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt.auth.converter")
public class JwtAuthConverterProperties {

    // Identificador del recurso JWT
    private String resourceId;

    // Atributo principal utilizado en la autenticacion JWT
    private String principalAttribute;


    public String getResourceId() {
        return resourceId;
    }


    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }


    public String getPrincipalAttribute() {
        return principalAttribute;
    }


    public void setPrincipalAttribute(String principalAttribute) {
        this.principalAttribute = principalAttribute;
    }
}
*/