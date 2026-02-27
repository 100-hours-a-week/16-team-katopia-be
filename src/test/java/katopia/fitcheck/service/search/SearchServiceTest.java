package katopia.fitcheck.service.search;

import katopia.fitcheck.domain.member.AccountStatus;
import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.dto.search.MemberSearchResponse;
import katopia.fitcheck.repository.member.MemberFollowRepository;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.support.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberFollowRepository memberFollowRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SearchValidator searchValidator;

    @InjectMocks
    private SearchService searchService;

    @Test
    @DisplayName("TC-SEARCH-S-04 계정 검색 시 팔로잉 여부 포함")
    void tcSearchS04_includesFollowingStatus() {
        Member member1 = MemberTestFactory.member(1L, "alice");
        Member member2 = MemberTestFactory.member(2L, "bob");
        when(searchValidator.requireQuery("al")).thenReturn("al");
        when(memberRepository.searchLatestByNickname(eq("al"), eq(AccountStatus.ACTIVE), any(PageRequest.class)))
                .thenReturn(List.of(member1, member2));
        when(memberFollowRepository.findFollowedIds(eq(10L), eq(List.of(1L, 2L))))
                .thenReturn(Set.of(2L));

        MemberSearchResponse response = searchService.searchUsers(10L, "al", "20", null);

        assertThat(response.members()).hasSize(2);
        assertThat(response.members().get(0).isFollowing()).isFalse();
        assertThat(response.members().get(1).isFollowing()).isTrue();
    }
}
