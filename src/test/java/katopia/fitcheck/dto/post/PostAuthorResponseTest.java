package katopia.fitcheck.dto.post;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Gender;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.dto.post.response.PostAuthorResponse;
import katopia.fitcheck.global.constants.MemberDisplayConstants;
import katopia.fitcheck.global.security.oauth2.SocialProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostAuthorResponseTest {

    @Nested
    class WithdrawnCases {

        @Test
        @DisplayName("TC-POST-AUTHOR-S-01 탈퇴 회원은 프로필 정보를 숨김")
        void tcPostAuthor01_withdrawnMember_hidesProfile() {
            Member member = Member.builder()
                    .id(1L)
                    .nickname("origin")
                    .oauth2Provider(SocialProvider.KAKAO)
                    .oauth2UserId("oauth")
                    .accountStatus(AccountStatus.WITHDRAWN)
                    .build();

            PostAuthorResponse response = PostAuthorResponse.of(member);

            assertThat(response.id()).isNull();
            assertThat(response.nickname()).isEqualTo(MemberDisplayConstants.WITHDRAWN_NICKNAME);
            assertThat(response.profileImageObjectKey()).isNull();
            assertThat(response.gender()).isNull();
            assertThat(response.height()).isNull();
            assertThat(response.weight()).isNull();
        }
    }

    @Nested
    class ActiveCases {

        @Test
        @DisplayName("TC-POST-AUTHOR-S-02 활성 회원은 원본 프로필 정보를 반환")
        void tcPostAuthor02_activeMember_returnsProfile() {
            Member member = Member.builder()
                    .id(1L)
                    .nickname("origin")
                    .oauth2Provider(SocialProvider.KAKAO)
                    .oauth2UserId("oauth")
                    .accountStatus(AccountStatus.ACTIVE)
                    .gender(Gender.F)
                    .height((short) 165)
                    .weight((short) 55)
                    .build();

            PostAuthorResponse response = PostAuthorResponse.of(member);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.nickname()).isEqualTo("origin");
            assertThat(response.profileImageObjectKey()).isNull();
            assertThat(response.gender()).isEqualTo("F");
            assertThat(response.height()).isEqualTo((short) 165);
            assertThat(response.weight()).isEqualTo((short) 55);
        }
    }
}
