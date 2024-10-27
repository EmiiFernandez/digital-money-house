package com.dmh.auth_service.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 componente de Spring encargado de convertir un objeto JWT (JSON Web Token) en un token de autenticacion de
 Spring Security (AbstractAuthenticationToken).
 Este token se utiliza para autenticar y autorizar a los usuarios en la aplicacion.
 */
@Component                                      //Convertimos el JWT en un Abstract....
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    // Convertidor de autoridades JWT proporcionado por Spring Security
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    // Propiedades de configuracioon relacionadas con el convertidor de autenticacion JWT
    private final JwtAuthConverterProperties properties;

    // Constructor que recibe las propiedades como parametro
    public JwtAuthConverter(JwtAuthConverterProperties properties) {
        this.properties = properties;
    }

    // Metodo de conversion principal que convierte un JWT en un token de autenticacion de Spring Security
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Combinar las autoridades del usuario, los roles de recursos y los roles de realm
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                Stream.concat(extractResourceRoles(jwt).stream(), extractResourceRolesRealm(jwt).stream())
        ).collect(Collectors.toSet());

        // Crear y devolver un token de autenticacion de JWT personalizado
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    // Metodo para obtener el nombre del principal (usuario) del JWT
    // Utiliza la configuracion definida en las propiedades
    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB; // Claim predeterminado para el nombre de usuario
        if (properties.getPrincipalAttribute() != null) {
            claimName = properties.getPrincipalAttribute();
        }
        return jwt.getClaim(claimName);
    }

    // Metodo para extraer los roles de recursos del JWT
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("realm_access");
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        if (resourceAccess == null
                || (resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null
                || (resourceRoles = (Collection<String>) resource.get("roles")) == null) {
            return Set.of(); // Devolver un conjunto vacio si no hay roles de recursos
        }
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    // Metodo para extraer los roles de realm del JWT
    private Collection<? extends GrantedAuthority> extractResourceRolesRealm(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("realm_access");
        Collection<String> resourceRoles;
        if (resourceAccess == null
                || (resourceRoles = (Collection<String>) resourceAccess.get("roles")) == null) {
            return Set.of(); // Devolver un conjunto vacio si no hay roles de realm
        }
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
