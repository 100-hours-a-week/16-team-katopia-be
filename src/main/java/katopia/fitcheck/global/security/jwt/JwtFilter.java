package katopia.fitcheck.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = jwtProvider.extractBearerToken(request);

        // AT가 없거나, 회원가입 전용 토큰(registrationTokenFilter에서 전처리)인 경우
        if (accessToken == null || jwtProvider.isTokenType(accessToken, TokenType.REGISTRATION)) {
            filterChain.doFilter(request, response);
            return;
        }

        // AT가 있는데, 시큐리티 컨텍스트가 없는 경우
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Long memberId = jwtProvider.extractMemberId(accessToken, JwtProvider.TokenType.ACCESS);
            if (memberId == null) {
                throw new AuthException(AuthErrorCode.INVALID_AT);
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    new MemberPrincipal(memberId),
                    accessToken,
                    Collections.emptyList()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
