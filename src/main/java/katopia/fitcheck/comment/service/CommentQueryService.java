package katopia.fitcheck.comment.service;

import katopia.fitcheck.comment.domain.Comment;
import katopia.fitcheck.comment.dto.CommentListResponse;
import katopia.fitcheck.comment.dto.CommentSummary;
import katopia.fitcheck.comment.repository.CommentRepository;
import katopia.fitcheck.global.pagination.CursorPagingHelper;
import katopia.fitcheck.post.service.PostFinder;
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
                .map(comment -> CommentSummary.of(comment))
                .toList();

        String nextCursor = null;
        if (!comments.isEmpty() && comments.size() == size) {
            Comment last = comments.getLast();
            nextCursor = CursorPagingHelper.encodeCursor(last.getCreatedAt(), last.getId());
        }

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
