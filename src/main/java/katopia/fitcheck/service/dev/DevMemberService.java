package katopia.fitcheck.service.dev;

import katopia.fitcheck.repository.comment.CommentRepository;
import katopia.fitcheck.repository.member.MemberRepository;
import katopia.fitcheck.service.member.MemberFinder;
import katopia.fitcheck.repository.post.PostLikeRepository;
import katopia.fitcheck.repository.post.PostRepository;
import katopia.fitcheck.repository.post.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DevMemberService {

    private final MemberRepository memberRepository;
    private final MemberFinder memberFinder;
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void hardDeleteMember(Long memberId) {
        memberFinder.findByIdOrThrow(memberId);

        List<Long> postIds = postRepository.findIdsByMemberId(memberId);
        for (Long postId : postIds) {
            commentRepository.deleteByPostId(postId);
            postLikeRepository.deleteByPostId(postId);
            postTagRepository.deleteByPostId(postId);
            postRepository.deleteById(postId);
        }

        commentRepository.deleteByMemberId(memberId);
        postLikeRepository.deleteByMemberId(memberId);
        memberRepository.deleteById(memberId);
    }
}
