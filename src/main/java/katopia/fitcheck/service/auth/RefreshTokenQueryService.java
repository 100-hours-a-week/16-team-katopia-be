package katopia.fitcheck.service.auth;

import katopia.fitcheck.domain.auth.RefreshToken;
import katopia.fitcheck.repository.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenQueryService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenRepository.findByTokenHash(tokenHash);
    }
}
