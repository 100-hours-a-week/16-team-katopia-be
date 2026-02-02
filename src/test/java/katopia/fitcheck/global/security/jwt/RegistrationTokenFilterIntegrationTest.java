package katopia.fitcheck.global.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Autowired;
import katopia.fitcheck.controller.MemberController;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TestRegistrationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = MemberController.class
        )
)
@ActiveProfiles("test")
@Import({
        RegistrationTokenFilter.class,
        TestRegistrationController.class,
        RegistrationTokenFilterIntegrationTest.TestSecurityConfig.class
})
class RegistrationTokenFilterIntegrationTest {

    @MockitoBean
    private JwtProvider jwtProvider;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(jwtProvider);
        when(jwtProvider.extractBearerToken(any(HttpServletRequest.class))).thenReturn(null);
    }

    @Test
    @DisplayName("회원가입 쿠키가 허가되지 않은 경로로 요청되면 만료")
    void registrationCookieExpiresOnInvalidPath() throws Exception {
        when(jwtProvider.extractCookieValue(any(HttpServletRequest.class), eq(JwtProvider.REGISTRATION_COOKIE)))
                .thenReturn("reg");
        when(jwtProvider.clearRegistrationCookie()).thenReturn(expiredRegistrationCookie());

        mockMvc.perform(get("/api/other")
                        .cookie(new Cookie(JwtProvider.REGISTRATION_COOKIE, "reg")))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("registration_token=")));
    }

    @Test
    @DisplayName("닉네임 중복 검증 호출은 회원가입 쿠키가 있어도 통과")
    void registrationCookieIsKeptOnNicknameCheck() throws Exception {
        when(jwtProvider.extractCookieValue(any(HttpServletRequest.class), eq(JwtProvider.REGISTRATION_COOKIE)))
                .thenReturn("reg");

        mockMvc.perform(get("/api/members/check")
                        .cookie(new Cookie(JwtProvider.REGISTRATION_COOKIE, "reg")))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Set-Cookie"));

        verify(jwtProvider, never()).clearRegistrationCookie();
    }

    @Test
    @DisplayName("회원가입 요청은 회원가입 쿠키로만 통과")
    void registrationCookieAllowsSignup() throws Exception {
        when(jwtProvider.extractCookieValue(any(HttpServletRequest.class), eq(JwtProvider.REGISTRATION_COOKIE)))
                .thenReturn("reg");
        when(jwtProvider.extractMemberId(eq("reg"), eq(JwtProvider.TokenType.REGISTRATION)))
                .thenReturn(1L);

        mockMvc.perform(post("/api/members")
                        .cookie(new Cookie(JwtProvider.REGISTRATION_COOKIE, "reg")))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    private ResponseCookie expiredRegistrationCookie() {
        return ResponseCookie.from(JwtProvider.REGISTRATION_COOKIE, "")
                .maxAge(0)
                .path(JwtProvider.REGISTRATION_PATH)
                .build();
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(
                HttpSecurity http,
                RegistrationTokenFilter registrationTokenFilter
        ) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .exceptionHandling(eh -> eh.authenticationEntryPoint((request, response, ex) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }))
                    .addFilterBefore(registrationTokenFilter, FilterSecurityInterceptor.class);
            return http.build();
        }
    }
}
