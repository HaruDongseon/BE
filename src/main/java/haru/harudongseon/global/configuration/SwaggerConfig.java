package haru.harudongseon.global.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "하루 동선 Application API 명세서",
                description = "하루 동선 Application API 명세서",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {

    private static final String TOKEN_PREFIX = "Bearer";

    @Bean
    public OpenAPI openAPI() {
        final String securityName = "JWT";
        final SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityName);
        final SecurityScheme securityScheme = new SecurityScheme()
                .name(securityName)
                .type(SecurityScheme.Type.HTTP)
                .scheme(TOKEN_PREFIX)
                .bearerFormat(securityName);

        final Components components = new Components()
                .addSecuritySchemes(securityName, securityScheme);

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
