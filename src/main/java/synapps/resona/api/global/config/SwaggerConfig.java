package synapps.resona.api.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = getSecurityScheme();
        SecurityRequirement securityRequirement = getSecurityRequireMent();

        return new OpenAPI()
                .info(new Info()
                        .title("전세계 채팅 프로젝트 유저 API")
                        .description("유저, sns 기본 기능들이 있습니다.")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(List.of(securityRequirement));
    }

    private SecurityScheme getSecurityScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");
    }
    private SecurityRequirement getSecurityRequireMent() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}