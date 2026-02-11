package katopia.fitcheck.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationTokenFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private RegistrationTokenFilter filter;

    @Test
    @DisplayName("TC-REG-FILTER-S-01 회원가입 요청 쿠키 정상 처리")
    void tcRegFilterS01_setsRegistrationMemberId() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/members");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtProvider.extractCookieValue(any(HttpServletRequest.class), eq(JwtProvider.REGISTRATION_COOKIE)))
                .thenReturn("reg");
        when(jwtProvider.extractMemberId("reg", JwtProvider.TokenType.REGISTRATION)).thenReturn(1L);
        doNothing().when(chain).doFilter(any(), any());

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(), any());
    }

    @Test
    @DisplayName("TC-REG-FILTER-F-01 회원가입 쿠키로 리프레시 요청 차단")
    void tcRegFilterF01_blocksRegistrationCookieOnRefresh() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", JwtProvider.REFRESH_PATH);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtProvider.extractCookieValue(any(HttpServletRequest.class), eq(JwtProvider.REGISTRATION_COOKIE)))
                .thenReturn("reg");
        when(jwtProvider.isTokenType(eq("reg"), eq(JwtProvider.TokenType.REGISTRATION)))
                .thenReturn(true);
        when(jwtProvider.clearRegistrationCookie())
                .thenReturn(org.springframework.http.ResponseCookie.from(JwtProvider.REGISTRATION_COOKIE, "")
                        .maxAge(0)
                        .path(JwtProvider.REGISTRATION_PATH)
                        .build());

        assertThatThrownBy(() -> filter.doFilter(request, response, chain))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.INVALID_TEMP_TOKEN_PATH);

        assertThat(response.getHeaders("Set-Cookie")).isNotEmpty();
    }

    @Test
    @DisplayName("TC-REG-FILTER-F-02 회원가입 요청 시 등록 쿠키 미존재")
    void tcRegFilterF02_blocksMissingRegistrationCookie() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/members");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtProvider.extractCookieValue(any(HttpServletRequest.class), eq(JwtProvider.REGISTRATION_COOKIE)))
                .thenReturn(null);

        assertThatThrownBy(() -> filter.doFilter(request, response, chain))
                .isInstanceOf(AuthException.class)
                .extracting(ex -> ((AuthException) ex).getErrorCode())
                .isEqualTo(AuthErrorCode.NOT_FOUND_TEMP_TOKEN);
    }
}
