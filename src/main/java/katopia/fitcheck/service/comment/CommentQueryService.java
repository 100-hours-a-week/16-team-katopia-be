package katopia.fitcheck.service.comment;

import katopia.fitcheck.domain.comment.Comment;
import katopia.fitcheck.dto.comment.response.CommentListResponse;
import katopia.fitcheck.dto.comment.response.CommentSummary;
import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.service.post.PostFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;
    private final PostFinder postFinder;

    @Transactional(readOnly = true)
    public CommentListResponse list(Long postId, String sizeValue, String after) {
        postFinder.requireExists(postId);

        int size = CursorPagingHelper.resolvePageSize(sizeValue);
        List<Comment> comments = loadComments(postId, size, after);
        List<CommentSummary> summaries = comments.stream()
                .map(CommentSummary::of)
                .toList();

        String nextCursor = CursorPagingHelper.resolveNextCursor(comments, size, Comment::getCreatedAt, Comment::getId);
        return CommentListResponse.of(summaries, nextCursor);
    }

    private List<Comment> loadComments(Long postId, int size, String after) {
        PageRequest pageRequest = PageRequest.of(0, size);
        if (after == null || after.isBlank()) {
            return commentRepository.findLatestByPostId(postId, pageRequest);
        }
        CursorPagingHelper.Cursor cursor = CursorPagingHelper.decodeCursor(after);
        return commentRepository.findPageAfter(postId, cursor.createdAt(), cursor.id(), pageRequest);
    }

}
