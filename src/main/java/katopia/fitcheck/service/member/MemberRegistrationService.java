package katopia.fitcheck.service.member;

import katopia.fitcheck.dto.member.request.MemberSignupRequest;
import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.global.security.jwt.JwtProvider.TokenPair;
import katopia.fitcheck.service.auth.RefreshTokenService;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.service.member.MemberProfileInputResolver.ResolvedProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberRegistrationService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberProfileInputResolver profileInputResolver;


    @Transactional
    public MemberRegistrationService.SignupResult signup(Long memberId, MemberSignupRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TEMP_TOKEN));

        if (member.getAccountStatus() == AccountStatus.ACTIVE) {
            throw new AuthException(AuthErrorCode.ALREADY_REGISTERED);
        }

        if (member.getAccountStatus() == AccountStatus.WITHDRAWN && LocalDateTime.now().isBefore(member.getDeletedAt().plusDays(14))) {
            throw new AuthException(AuthErrorCode.WITHDRAWN_MEMBER);
        }

        /*
        nickname, gender is not null
         */
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        ResolvedProfile resolved = profileInputResolver.resolveForSignup(member, request);
        try {
            member.completeRegistration(
                    request,
                    resolved.gender(),
                    resolved.height(),
                    resolved.weight(),
                    resolved.enableRealtimeNotification(),
                    resolved.styles(),
                    resolved.profileImageObjectKey()
            );
            memberRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_NICKNAME);
        }


        TokenPair tokenPair = jwtProvider.issueTokens(memberId);
        refreshTokenService.issue(memberId, tokenPair.refreshToken());
        return new MemberRegistrationService.SignupResult(
                member.getId(),
                member.getAccountStatus().name(),
                tokenPair.accessToken().token(),
                jwtProvider.buildRefreshCookie(tokenPair.refreshToken()),
                jwtProvider.clearRegistrationCookie()
        );
    }

    public record SignupResult(
            Long id,
            String accountStatus,
            String accessToken,
            ResponseCookie refreshCookie,
            ResponseCookie clearRegistrationCookie
    ) { }
}
