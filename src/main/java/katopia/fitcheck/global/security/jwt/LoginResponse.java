package katopia.fitcheck.global.security.jwt;

import katopia.fitcheck.member.domain.AccountStatus;

public record LoginResponse(
    AccountStatus status,
    String email,
    String accessToken
) { }
