package com.dmh.auth_service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/*
 * Clase de configuracion para el convertidor de autenticacion JWT.
 * Esta clase actua como un DTO (Objeto de Transferencia de Datos) que carga la configuracion
 * del convertidor de autenticacion JWT desde las propiedades de la aplicacion.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt.auth.converter")
public class JwtAuthConverterProperties {

    // Identificador del recurso JWT
    private String resourceId;

    // Atributo principal utilizado en la autenticacion JWT
    private String principalAttribute;

    /*
     * Metodo para obtener el identificador del recurso JWT.
     * @return El identificador del recurso JWT.
     */
    public String getResourceId() {
        return resourceId;
    }

    /*
     * Metodo para establecer el identificador del recurso JWT.
     * @param resourceId El identificador del recurso JWT.
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /*
     * Metodo para obtener el atributo principal utilizado en la autenticacion JWT.
     * @return El nombre del atributo principal.
     */
    public String getPrincipalAttribute() {
        return principalAttribute;
    }

    /*
     * Metodo para establecer el atributo principal utilizado en la autenticacion JWT.
     * @param principalAttribute El nombre del atributo principal.
     */
    public void setPrincipalAttribute(String principalAttribute) {
        this.principalAttribute = principalAttribute;
    }
}
