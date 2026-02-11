package katopia.fitcheck.controller;

import katopia.fitcheck.controller.spec.VoteApiSpec;
import katopia.fitcheck.dto.vote.request.VoteCreateRequest;
import katopia.fitcheck.dto.vote.request.VoteParticipationRequest;
import katopia.fitcheck.dto.vote.response.VoteCandidateResponse;
import katopia.fitcheck.dto.vote.response.VoteCreateResponse;
import katopia.fitcheck.dto.vote.response.VoteListResponse;
import katopia.fitcheck.dto.vote.response.VoteResultResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.VoteSuccessCode;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.service.vote.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController implements VoteApiSpec {

    private final VoteService voteService;
    private final SecuritySupport securitySupport;

    @PostMapping
    @Override
    public ResponseEntity<APIResponse<VoteCreateResponse>> createVote(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody VoteCreateRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);

        VoteCreateResponse response = voteService.create(memberId, request);

        return APIResponse.ok(VoteSuccessCode.VOTE_CREATED, response);
    }

    @GetMapping
    @Override
    public ResponseEntity<APIResponse<VoteListResponse>> listVotes(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "after", required = false) String after
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        VoteListResponse response = voteService.listMine(memberId, size, after);

        return APIResponse.ok(VoteSuccessCode.VOTE_LISTED, response);
    }

    @GetMapping("/candidates")
    @Override
    public ResponseEntity<APIResponse<VoteCandidateResponse>> getCandidateVote(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = securitySupport.requireMemberId(principal);

        VoteCandidateResponse response = voteService.findLatestCandidate(memberId);

        return APIResponse.ok(VoteSuccessCode.VOTE_CANDIDATE_FETCHED, response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<APIResponse<VoteResultResponse>> getVote(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long voteId
    ) {
        Long memberId = securitySupport.requireMemberId(principal);

        VoteResultResponse response = voteService.getResult(memberId, voteId);

        return APIResponse.ok(VoteSuccessCode.VOTE_FETCHED, response);
    }

    @PostMapping("/{id}/participations")
    @Override
    public ResponseEntity<APIResponse<VoteResultResponse>> participateVote(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long voteId,
            @Valid @RequestBody VoteParticipationRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);

        VoteResultResponse response = voteService.participate(memberId, voteId, request);

        return APIResponse.ok(VoteSuccessCode.VOTE_PARTICIPATED, response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteVote(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long voteId
    ) {
        Long memberId = securitySupport.requireMemberId(principal);

        voteService.delete(memberId, voteId);

        return APIResponse.noContent(VoteSuccessCode.VOTE_DELETED);
    }
}
