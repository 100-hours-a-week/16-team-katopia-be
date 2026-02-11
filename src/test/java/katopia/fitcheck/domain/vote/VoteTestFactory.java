package katopia.fitcheck.domain.vote;

import katopia.fitcheck.domain.member.Member;

import java.time.LocalDateTime;
import java.util.List;

public final class VoteTestFactory {

    private VoteTestFactory() {
    }

    public static Vote vote(Member member, String title, LocalDateTime expiresAt) {
        return Vote.create(member, title, expiresAt, List.of());
    }
}
