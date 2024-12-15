package com.dmh.auth_service.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String serverUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String resource;
    private Integer tokenValidity;
    private Integer refreshTokenValidity;
    private String authorizationGrantType;
    private Boolean publicClient;
    private Boolean bearerOnly;
    private String principalAttribute;
    private String sslRequired;
}

