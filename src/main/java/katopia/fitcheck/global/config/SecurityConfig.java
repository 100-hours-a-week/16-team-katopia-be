package katopia.fitcheck.global.config;

import katopia.fitcheck.global.security.handler.RestAccessDeniedHandler;
import katopia.fitcheck.global.security.handler.RestAuthenticationEntryPoint;
import katopia.fitcheck.global.security.jwt.JwtFilter;
import katopia.fitcheck.global.security.jwt.RegistrationTokenFilter;
import katopia.fitcheck.global.security.oauth2.CustomOAuth2UserService;
import katopia.fitcheck.global.security.oauth2.OAuth2RegistrationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2RegistrationSuccessHandler oAuth2RegistrationSuccessHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final RegistrationTokenFilter registrationTokenFilter;
    private final JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EndpointRequest.to("health")).permitAll()
//                        .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/oauth2/**",
                                "/login/**",
                                "/error",
                                "/.well-known/**"
                        ).permitAll()
                        // 공개 API { 사용자 프로필 상세보기, 닉네임 중복 검증 }
                        .requestMatchers(HttpMethod.GET, "/api/members", "/api/members?nickname=*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/tokens").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        .successHandler(oAuth2RegistrationSuccessHandler)
                )
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(registrationTokenFilter, JwtFilter.class);
        /*
        RegistrationTokenFilter - JwtFilter - UsernamePasswordAuthenticationFilter
         */

        return http.build();
    }
}
