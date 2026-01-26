package katopia.fitcheck.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Configuration
public class SwaggerConfig {
    private final String swaggerServerUrl;

    public SwaggerConfig(@Value("${app.swagger.server-url}") String swaggerServerUrl) {
        this.swaggerServerUrl = swaggerServerUrl;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fit Check Rest API Document")
                        .description("Fit Check Rest API")
                        .version("1.0.0")
                )
                .servers(List.of(
                        new Server().url(swaggerServerUrl).description("API 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Access Token을 입력해주세요."))
                )
                .addSecurityItem(new SecurityRequirement().addList(
                        "Authorization"
                ));
    }
}
