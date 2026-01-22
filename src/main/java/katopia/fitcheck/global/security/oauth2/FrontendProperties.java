package katopia.fitcheck.global.security.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.frontend")
public class FrontendProperties {
    private String baseUrl;
}
