package katopia.fitcheck.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.headers.Header;
import katopia.fitcheck.dto.notification.response.NotificationListResponse;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.docs.Docs;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationApiSpec {

    @Operation(summary = "알림 목록 조회", description = Docs.CURSOR_PAGING_DES)
    @ApiResponse(
            responseCode = "200",
            description = "알림 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = NotificationListResponse.class))
    )
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    ResponseEntity<APIResponse<NotificationListResponse>> listNotifications(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = Docs.PAGE_DES, example = Docs.PAGE)
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @Parameter(description = Docs.CURSOR_DES, example = Docs.CURSOR)
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    );

    @Operation(summary = "알림 읽음 처리", description = "알림 읽음 시각을 기록합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "알림 읽음 처리 성공",
            content = @Content(schema = @Schema(implementation = katopia.fitcheck.dto.notification.response.NotificationSummary.class))
    )
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    @ApiResponse(responseCode = "404", description = Docs.NOT_FOUND_DES, content = @Content)
    ResponseEntity<APIResponse<katopia.fitcheck.dto.notification.response.NotificationSummary>> readNotification(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long notificationId
    );

    @Operation(summary = "알림 SSE 연결", description = "실시간 알림 스트림을 연결합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "SSE 연결 성공",
            headers = {
                    @Header(
                            name = "Content-Type",
                            description = "SSE 스트림 응답 타입(text/event-stream)"
                    ),
                    @Header(
                            name = "Cache-Control",
                            description = "SSE 캐시 방지(no-cache)"
                    ),
                    @Header(
                            name = "Connection",
                            description = "SSE 연결 유지(keep-alive)"
                    ),
                    @Header(
                            name = "X-Accel-Buffering",
                            description = "Nginx 버퍼링 비활성(no)"
                    )
            }
    )
    @ApiResponse(responseCode = "401", description = Docs.AT_MISSING_OR_INVALID_DES, content = @Content)
    SseEmitter connectNotificationStream(
            @AuthenticationPrincipal MemberPrincipal principal,
            HttpServletResponse response
    );
}
