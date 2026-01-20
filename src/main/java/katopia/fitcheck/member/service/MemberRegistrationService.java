package katopia.fitcheck.member.service;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.global.security.jwt.JwtProvider.Token;
import katopia.fitcheck.global.security.jwt.JwtProvider.TokenPair;
import katopia.fitcheck.member.MemberRepository;
import katopia.fitcheck.member.domain.AccountStatus;
import katopia.fitcheck.member.domain.Member;
import katopia.fitcheck.member.domain.MemberProfileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberRegistrationService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final MemberProfileValidator profileValidator;

    @Transactional
    public SignupResult signup(Long memberId, String nickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TEMP_TOKEN));

        if (member.getAccountStatus() == AccountStatus.ACTIVE) {
            throw new AuthException(AuthErrorCode.ALREADY_REGISTERED);
        }
        if (member.getAccountStatus() == AccountStatus.WITHDRAWN) {
            throw new AuthException(AuthErrorCode.WITHDRAWN_MEMBER);
        }

        String normalized = profileValidator.normalizeNickname(nickname);
        boolean nicknameChanged = !normalized.equals(member.getNickname());
        if (nicknameChanged && memberRepository.existsByNickname(normalized)) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        try {
            member.completeRegistration(normalized);
            memberRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
        TokenPair tokenPair = jwtProvider.issueTokens(member.getId());
        return new SignupResult(member, tokenPair.accessToken(), tokenPair.refreshToken());
    }

    public ResponseCookie buildRefreshCookie(Token refreshToken) {
        return jwtProvider.buildRefreshCookie(refreshToken);
    }

    public record SignupResult(Member member,
                               JwtProvider.Token accessToken,
                               JwtProvider.Token refreshToken) {
    }
}
