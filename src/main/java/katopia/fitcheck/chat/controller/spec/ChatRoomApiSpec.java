package katopia.fitcheck.chat.controller.spec;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import katopia.fitcheck.chat.api.request.ChatRoomCreateRequest;
import katopia.fitcheck.chat.api.request.ChatMessageCreateRequest;
import katopia.fitcheck.chat.api.request.ChatRoomUpdateRequest;
import katopia.fitcheck.chat.api.response.ChatRoomCreateResponse;
import katopia.fitcheck.chat.api.response.ChatRoomAllListResponse;
import katopia.fitcheck.chat.api.response.ChatRoomJoinedListResponse;
import katopia.fitcheck.chat.api.response.ChatRoomMembershipResponse;
import katopia.fitcheck.chat.api.response.ChatMessageListResponse;
import katopia.fitcheck.chat.api.response.ChatMessageResponse;
import katopia.fitcheck.chat.api.response.ChatRoomUpdateResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Chat", description = "채팅방/채팅 메시지 REST API")
public interface ChatRoomApiSpec {

    @Operation(summary = "채팅방 생성", description = "로그인한 활성 사용자가 채팅방을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "채팅방 생성 성공", content = @Content(schema = @Schema(implementation = ChatRoomCreateResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<ChatRoomCreateResponse>> createRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody ChatRoomCreateRequest request
    );

    @Operation(summary = "내 채팅방 목록 조회", description = "참여 중인 채팅방을 최신 참여 순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공", content = @Content(schema = @Schema(implementation = ChatRoomJoinedListResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<ChatRoomJoinedListResponse>> listRooms(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = "채팅방 목록 커서(joinedAt|memberDocumentId)", example = "2026-03-11T12:34:56Z|member-doc-1")
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "전체 채팅방 목록 조회", description = "생성된 전체 채팅방을 최신 수정 순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "전체 채팅방 목록 조회 성공", content = @Content(schema = @Schema(implementation = ChatRoomAllListResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<ChatRoomAllListResponse>> listAllRooms(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = "전체 채팅방 목록 커서(updatedAt|roomId)", example = "2026-03-11T12:34:56Z|67d2f7c4a8b13e4d91c0ab12")
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "채팅방 참여", description = "참여하지 않은 채팅방에 참여합니다.")
    @ApiResponse(responseCode = "200", description = "채팅방 참여 성공", content = @Content(schema = @Schema(implementation = ChatRoomMembershipResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    @ApiResponse(responseCode = "409", description = "이미 참여 중인 채팅방", content = @Content)
    ResponseEntity<APIResponse<ChatRoomMembershipResponse>> joinRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("roomId") String roomId
    );

    @Operation(summary = "채팅방 퇴장", description = "참여 중인 채팅방에서 퇴장합니다.")
    @ApiResponse(responseCode = "200", description = "채팅방 퇴장 성공", content = @Content(schema = @Schema(implementation = ChatRoomMembershipResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<ChatRoomMembershipResponse>> leaveRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("roomId") String roomId
    );

    @Operation(summary = "채팅방 수정", description = "채팅방 생성자만 제목/썸네일을 수정할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "채팅방 수정 성공", content = @Content(schema = @Schema(implementation = ChatRoomUpdateResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<ChatRoomUpdateResponse>> updateRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("roomId") String roomId,
            @Valid @RequestBody ChatRoomUpdateRequest request
    );

    @Operation(summary = "채팅방 삭제", description = "채팅방 생성자만 채팅방과 연관 참여/메시지를 삭제할 수 있습니다.")
    @ApiResponse(responseCode = "204", description = "채팅방 삭제 성공", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "403", description = Docs.ACCESS_DENIED_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<Void> deleteRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("roomId") String roomId
    );

    @Operation(summary = "(dev) 채팅 메시지 생성", description = "참여 중인 채팅방에 텍스트 또는 이미지 메시지를 저장합니다.")
    @ApiResponse(responseCode = "201", description = "채팅 메시지 생성 성공", content = @Content(schema = @Schema(implementation = ChatMessageResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<ChatMessageResponse>> createMessage(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("roomId") String roomId,
            @Valid @RequestBody ChatMessageCreateRequest request
    );

    @Operation(summary = "채팅 메시지 목록 조회", description = "참여 중인 채팅방의 메시지를 최신순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "채팅 메시지 목록 조회 성공", content = @Content(schema = @Schema(implementation = ChatMessageListResponse.class)))
    @ApiResponse(responseCode = "400", description = Docs.INPUT_VALIDATION_DES, content = @Content)
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<ChatMessageListResponse>> listMessages(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("roomId") String roomId,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = "채팅 메시지 목록 커서(messageId)", example = "101")
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );
}
