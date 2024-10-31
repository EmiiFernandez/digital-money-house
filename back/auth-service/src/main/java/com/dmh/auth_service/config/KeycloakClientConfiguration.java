package com.dmh.auth_service.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakClientConfiguration {

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.serverUrl}")
    private String serverurl;
    @Value("${keycloak.clientId}")
    private String clientid;
    @Value("${keycloak.clientSecret}")
    private String clientsecret;

    @Bean
    public Keycloak getInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverurl)
                .realm(realm)
                .clientId(clientid)
                .clientSecret(clientsecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
