package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import katopia.fitcheck.dto.vote.request.VoteCreateRequest;
import katopia.fitcheck.dto.vote.request.VoteParticipationRequest;
import katopia.fitcheck.dto.vote.response.VoteCandidateResponse;
import katopia.fitcheck.dto.vote.response.VoteCreateResponse;
import katopia.fitcheck.dto.vote.response.VoteListResponse;
import katopia.fitcheck.dto.vote.response.VoteResultResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

    public interface VoteApiSpec {

    @Operation(summary = "투표 생성", description = "투표를 생성하고 항목(이미지)을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "투표 생성 성공", content = @Content(schema = @Schema(implementation = VoteCreateResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<VoteCreateResponse>> createVote(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody VoteCreateRequest request
    );

    @Operation(summary = "내 투표 목록 조회", description = "커서 기반 인피니티 스크롤을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "투표 목록 조회 성공", content = @Content(schema = @Schema(implementation = VoteListResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    ResponseEntity<APIResponse<VoteListResponse>> listVotes(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES, example = Docs.CURSOR)
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "참여 가능한 투표 조회", description = "참여하지 않은 최신 투표 1건을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "투표 조회 성공", content = @Content(schema = @Schema(implementation = VoteCandidateResponse.class)))
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<VoteCandidateResponse>> getCandidateVote(
            @AuthenticationPrincipal MemberPrincipal principal
    );

    @Operation(summary = "투표 결과 조회", description = "작성자 또는 참여자만 결과를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "투표 결과 조회 성공", content = @Content(schema = @Schema(implementation = VoteResultResponse.class)))
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<VoteResultResponse>> getVote(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long voteId
    );

    @Operation(summary = "투표 참여", description = "선택한 항목에 투표 후 결과를 반환합니다.")
    @ApiResponse(responseCode = "201", description = "투표 참여 성공", content = @Content(schema = @Schema(implementation = VoteResultResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @ApiResponse(responseCode = "409", description = "이미 참여한 | 종료된 투표", content = @Content)
    ResponseEntity<APIResponse<VoteResultResponse>> participateVote(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long voteId,
            @Valid @RequestBody VoteParticipationRequest request
    );

    @Operation(summary = "투표 삭제", description = "작성자만 투표를 삭제할 수 있습니다.")
    @ApiResponse(responseCode = "204", description = "투표 삭제 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<Void> deleteVote(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long voteId
    );
}
