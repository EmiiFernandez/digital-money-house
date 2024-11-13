package com.dmh.gateway.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.keycloak")
public class KeycloakProperties {
    private String realm;
    private String serverUrl;
    private String clientId;
    private String clientSecret;
    private String issuerUri;
}
