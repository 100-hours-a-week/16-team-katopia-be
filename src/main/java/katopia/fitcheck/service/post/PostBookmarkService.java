package katopia.fitcheck.service.post;

import katopia.fitcheck.domain.member.Member;
import katopia.fitcheck.domain.post.Post;
import katopia.fitcheck.domain.post.PostBookmark;
import katopia.fitcheck.dto.post.response.PostBookmarkResponse;
import katopia.fitcheck.dto.post.response.PostListResponse;
import katopia.fitcheck.dto.post.response.PostSummary;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.PostBookmarkErrorCode;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.repository.post.PostBookmarkRepository;
import katopia.fitcheck.service.member.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostBookmarkService {

    private final PostBookmarkRepository postBookmarkRepository;
    private final PostFinder postFinder;
    private final MemberFinder memberFinder;

    @Transactional
    public PostBookmarkResponse bookmark(Long memberId, Long postId) {
        if (postBookmarkRepository.existsByMemberIdAndPostId(memberId, postId)) {
            throw new BusinessException(PostBookmarkErrorCode.ALREADY_BOOKMARKED);
        }
        Member member = memberFinder.findActiveByIdOrThrow(memberId);
        Post post = postFinder.findByIdOrThrow(postId);
        postBookmarkRepository.save(PostBookmark.of(member, post));
        return PostBookmarkResponse.of(postId, true);
    }

    @Transactional
    public void unbookmark(Long memberId, Long postId) {
        PostBookmark bookmark = postBookmarkRepository.findByMemberIdAndPostId(memberId, postId)
                .orElseThrow(() -> new BusinessException(PostBookmarkErrorCode.BOOKMARK_NOT_FOUND));
        postBookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public PostListResponse listBookmarks(Long memberId, String sizeValue, String after) {
        memberFinder.findActiveByIdOrThrow(memberId);
        int size = CursorPagingHelper.resolvePageSize(sizeValue);

        List<PostSummary> summaries = loadBookmarks(memberId, size, after).stream()
                .map(row -> PostSummary.of(row.getId(), row.getImageObjectKey(), row.getCreatedAt()))
                .toList();

        String nextCursor = null;
        if (!summaries.isEmpty() && summaries.size() == size) {
            PostSummary last = summaries.getLast();
            nextCursor = CursorPagingHelper.encodeCursor(last.createdAt(), last.id());
        }

        return PostListResponse.of(summaries, nextCursor);
    }

    private List<katopia.fitcheck.repository.post.PostSummaryProjection> loadBookmarks(
            Long memberId,
            int size,
            String after
    ) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return postBookmarkRepository.findLatestBookmarks(memberId, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return postBookmarkRepository.findBookmarksPageAfter(memberId, cursor.createdAt(), cursor.id(), pageRequest);
    }
}
