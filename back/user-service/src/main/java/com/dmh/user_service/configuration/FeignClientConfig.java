package com.dmh.user_service.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignClientConfig /*implements RequestInterceptor*/ {
/*
    private final SecurityService securityService;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer "+securityService.getUserToken());
    }
*/}
