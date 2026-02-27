package katopia.fitcheck.global.security.jwt;

import lombok.Getter;
import lombok.Setter;
import katopia.fitcheck.global.policy.Policy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    /**
     * 토큰 발급자
     */
    private String issuer = "fit-check";

    /**
     * application-local-secret.yml / application-prod.yml 등에 정의된 Access Token 비밀키
     */
    private String accessTokenSecret;

    /**
     * application-local-secret.yml / application-prod.yml 등에 정의된 Refresh Token 비밀키
     */
    private String refreshTokenSecret;

    /**
     * 회원가입 전용 임시 토큰 TTL
     * 10분
     */
    private Duration registrationTokenTtl = Policy.JWT_REGISTRATION_TOKEN_TTL;


    /**
     * 액세스 토큰 TTL
     * 15분
     */
    private Duration accessTokenTtl = Policy.JWT_ACCESS_TOKEN_TTL;

    /**
     * 리프레시 토큰 TTL
     * 14일
     */
    private Duration refreshTokenTtl = Policy.JWT_REFRESH_TOKEN_TTL;
}
