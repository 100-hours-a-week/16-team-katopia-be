package katopia.fitcheck.chat.controller;

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
import katopia.fitcheck.chat.controller.spec.ChatRoomApiSpec;
import katopia.fitcheck.chat.service.message.ChatMessageCommandService;
import katopia.fitcheck.chat.service.message.ChatMessageQueryService;
import katopia.fitcheck.chat.service.room.ChatRoomCommandService;
import katopia.fitcheck.chat.service.room.ChatRoomQueryService;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.ChatSuccessCode;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController implements ChatRoomApiSpec {

    private final ChatRoomCommandService chatRoomCommandService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatMessageCommandService chatMessageCommandService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final SecuritySupport securitySupport;

    @Override
    @PostMapping
    public ResponseEntity<APIResponse<ChatRoomCreateResponse>> createRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody ChatRoomCreateRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatRoomCreateResponse response = chatRoomCommandService.createRoom(memberId, request);
        return APIResponse.ok(ChatSuccessCode.CHAT_ROOM_CREATED, response);
    }

    @Override
    @GetMapping
    public ResponseEntity<APIResponse<ChatRoomJoinedListResponse>> listRooms(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatRoomJoinedListResponse response = chatRoomQueryService.listRooms(memberId, size, after);
        return APIResponse.ok(ChatSuccessCode.CHAT_ROOM_LISTED, response);
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<APIResponse<ChatRoomAllListResponse>> listAllRooms(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatRoomAllListResponse response = chatRoomQueryService.listAllRooms(memberId, size, after);
        return APIResponse.ok(ChatSuccessCode.CHAT_ROOM_ALL_LISTED, response);
    }

    @Override
    @PostMapping("/{roomId}/join")
    public ResponseEntity<APIResponse<ChatRoomMembershipResponse>> joinRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable String roomId
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatRoomMembershipResponse response = chatRoomCommandService.joinRoom(memberId, roomId);
        return APIResponse.ok(ChatSuccessCode.CHAT_ROOM_JOINED, response);
    }

    @Override
    @DeleteMapping("/{roomId}/leave")
    public ResponseEntity<APIResponse<ChatRoomMembershipResponse>> leaveRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable String roomId
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatRoomMembershipResponse response = chatRoomCommandService.leaveRoom(memberId, roomId);
        return APIResponse.ok(ChatSuccessCode.CHAT_ROOM_LEFT, response);
    }

    @Override
    @PatchMapping("/{roomId}")
    public ResponseEntity<APIResponse<ChatRoomUpdateResponse>> updateRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable String roomId,
            @Valid @RequestBody ChatRoomUpdateRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatRoomUpdateResponse response = chatRoomCommandService.updateRoom(memberId, roomId, request);
        return APIResponse.ok(ChatSuccessCode.CHAT_ROOM_UPDATED, response);
    }

    @Override
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable String roomId
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        chatRoomCommandService.deleteRoom(memberId, roomId);
        return APIResponse.noContent(ChatSuccessCode.CHAT_ROOM_DELETED);
    }

    @Override
    @PostMapping("/{roomId}/messages")
    public ResponseEntity<APIResponse<ChatMessageResponse>> createMessage(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable String roomId,
            @Valid @RequestBody ChatMessageCreateRequest request
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatMessageResponse response = chatMessageCommandService.createMessage(memberId, roomId, request);
        return APIResponse.ok(ChatSuccessCode.CHAT_MESSAGE_CREATED, response);
    }

    @Override
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<APIResponse<ChatMessageListResponse>> listMessages(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable String roomId,
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        ChatMessageListResponse response = chatMessageQueryService.listMessages(memberId, roomId, size, after);
        return APIResponse.ok(ChatSuccessCode.CHAT_MESSAGE_LISTED, response);
    }
}
