package katopia.fitcheck.dto.comment;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.dto.comment.response.CommentAuthorResponse;
import katopia.fitcheck.global.constants.MemberDisplayConstants;
import katopia.fitcheck.global.security.oauth2.SocialProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentAuthorResponseTest {

    @Nested
    class WithdrawnCases {

        @Test
        @DisplayName("TC-COMMENT-AUTHOR-01 탈퇴 회원은 닉네임을 탈퇴 회원으로 표시")
        void tcCommentAuthor01_withdrawnMember_returnsWithdrawnNickname() {
            Member member = Member.builder()
                    .id(1L)
                    .nickname("origin")
                    .oauth2Provider(SocialProvider.KAKAO)
                    .oauth2UserId("oauth")
                    .accountStatus(AccountStatus.WITHDRAWN)
                    .build();

            CommentAuthorResponse response = CommentAuthorResponse.of(member);

            assertThat(response.nickname()).isEqualTo(MemberDisplayConstants.WITHDRAWN_NICKNAME);
            assertThat(response.profileImageObjectKey()).isNull();
        }
    }

    @Nested
    class ActiveCases {

        @Test
        @DisplayName("TC-COMMENT-AUTHOR-02 활성 회원은 원본 닉네임 반환")
        void tcCommentAuthor02_activeMember_returnsNickname() {
            Member member = Member.builder()
                    .id(1L)
                    .nickname("origin")
                    .oauth2Provider(SocialProvider.KAKAO)
                    .oauth2UserId("oauth")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            CommentAuthorResponse response = CommentAuthorResponse.of(member);

            assertThat(response.nickname()).isEqualTo("origin");
        }
    }
}
