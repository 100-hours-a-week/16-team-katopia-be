package katopia.fitcheck.service.member;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.global.security.jwt.JwtProvider.Token;
import katopia.fitcheck.global.security.jwt.JwtProvider.TokenPair;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Gender;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.member.MemberProfileValidator;
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
    public SignupResult signup(Long memberId, String normalizedNickname, String gender) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TEMP_TOKEN));

        if (member.getAccountStatus() == AccountStatus.ACTIVE) {
            throw new AuthException(AuthErrorCode.ALREADY_REGISTERED);
        }
        if (member.getAccountStatus() == AccountStatus.WITHDRAWN) {
            throw new AuthException(AuthErrorCode.WITHDRAWN_MEMBER);
        }

        Gender parsedGender = profileValidator.parseGender(gender);
        boolean nicknameChanged = !normalizedNickname.equals(member.getNickname());
        if (nicknameChanged && memberRepository.existsByNickname(normalizedNickname)) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        try {
            member.completeRegistration(normalizedNickname, parsedGender);
            memberRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
        TokenPair tokenPair = jwtProvider.issueTokens(member.getId());
        return buildSignupResult(member, tokenPair);
    }

    private SignupResult buildSignupResult(Member member, TokenPair tokenPair) {
        return new SignupResult(member, tokenPair.accessToken().token(),
                jwtProvider.buildRefreshCookie(tokenPair.refreshToken()),
                jwtProvider.clearRegistrationCookie()
        );
    }

    public record SignupResult(Member member,
                               String accessToken,
                               ResponseCookie refreshCookie,
                               ResponseCookie clearRegistrationCookie
    ) { }
}
