package com.dmh.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenEnhanceFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .cast(JwtAuthenticationToken.class)
                .map(token -> {
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-Id", token.getToken().getSubject())
                            .header("X-User-Roles", String.join(",", token.getAuthorities().toString()))
                            .build();
                    return exchange.mutate().request(request).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }
}