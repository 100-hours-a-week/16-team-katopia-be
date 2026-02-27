package katopia.fitcheck.global.config;

import katopia.fitcheck.global.security.handler.RestAccessDeniedHandler;
import katopia.fitcheck.global.security.handler.RestAuthenticationEntryPoint;
import katopia.fitcheck.global.security.jwt.JwtFilter;
import katopia.fitcheck.global.security.jwt.RegistrationTokenFilter;
import katopia.fitcheck.global.security.oauth2.CustomOAuth2UserService;
import katopia.fitcheck.global.security.oauth2.OAuth2AuthenticationFailureHandler;
import katopia.fitcheck.global.security.oauth2.OAuth2RegistrationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2RegistrationSuccessHandler oAuth2RegistrationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final RegistrationTokenFilter registrationTokenFilter;
    private final JwtFilter jwtFilter;
    private final Environment environment;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean allowSwagger = environment.acceptsProfiles(Profiles.of("dev", "local"));
        boolean allowDevOnly = environment.acceptsProfiles(Profiles.of("dev", "local"));
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(EndpointRequest.to("health")).permitAll();
                    if (allowDevOnly) {
                        auth.requestMatchers(EndpointRequest.to("prometheus", "metrics")).permitAll();
                    }
                    auth.requestMatchers(
                            "/bookmarks.*",
                            "/oauth2/**",
                            "/login/**",
                            "/error",
                            "/.well-known/**"
                    ).permitAll();
                    if (allowSwagger) {
                        auth.requestMatchers(
                                "/api/swagger-ui/**",
                                "/api/v3/api-docs/**"
                        ).permitAll();
                    }
                    if (allowDevOnly) {
                        auth.requestMatchers("/api/dev/**").permitAll();
                    }
                    // 공개 API { 사용자 공개 프로필, 닉네임 중복 검증 }
                    auth.requestMatchers(HttpMethod.GET, "/api/members/check").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/api/members").permitAll();       // 회원가입
                    auth.requestMatchers(HttpMethod.GET, "/api/members/*").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/members/*/posts").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/posts/*").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/members/me").authenticated();
                    auth.requestMatchers(HttpMethod.POST, "/api/auth/tokens").permitAll();   // RTR
                    auth.requestMatchers(HttpMethod.DELETE, "/api/auth/tokens").permitAll(); // 로그아웃
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        .successHandler(oAuth2RegistrationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .addFilterBefore(jwtFilter, AuthorizationFilter.class)
                .addFilterBefore(registrationTokenFilter, JwtFilter.class);
        /*
        RegistrationTokenFilter - JwtFilter - AuthorizationFilter
         */

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // TODO 운영/배포 서버 도메인 추가 설정 예정
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
