package com.synapps.atch.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = getSecurityScheme();
        SecurityRequirement securityRequirement = getSecurityRequireMent();

        Server server = new Server();
        server.setUrl(getUrl());

        return new OpenAPI()
                .info(new Info()
                        .title("전세계 채팅 프로젝트 유저 API")
                        .description("유저, sns 기본 기능들이 있습니다.")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(List.of(securityRequirement));

    }

    private String getUrl() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String scheme = request.getHeader("X-Forwarded-Proto");
        String host = request.getHeader("X-Forwarded-Host");
        String port = request.getHeader("X-Forwarded-Port");

        if (scheme != null && host != null) {
            if (port != null && !port.equals("80") && !port.equals("443")) {
                return scheme + "://" + host + ":" + port;
            } else {
                return scheme + "://" + host;
            }
        }

        // Fallback to request URL if headers are not available
        return request.getRequestURL().toString().replaceFirst("/swagger-ui.*", "");
    }

    private SecurityScheme getSecurityScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");
    }
    private SecurityRequirement getSecurityRequireMent() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}
