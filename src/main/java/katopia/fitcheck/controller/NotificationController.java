package katopia.fitcheck.controller;

import katopia.fitcheck.controller.spec.NotificationApiSpec;
import katopia.fitcheck.dto.notification.response.NotificationListResponse;
import katopia.fitcheck.dto.notification.response.NotificationSummary;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.NotificationSuccessCode;
import katopia.fitcheck.global.policy.Policy;
import katopia.fitcheck.global.security.SecuritySupport;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import katopia.fitcheck.service.notification.NotificationService;
import katopia.fitcheck.service.notification.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationApiSpec {

    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;
    private final SecuritySupport securitySupport;

    @GetMapping
    @Override
    public ResponseEntity<APIResponse<NotificationListResponse>> listNotifications(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(value = Policy.PAGE_VALUE, required = false) String size,
            @RequestParam(value = Policy.CURSOR_VALUE, required = false) String after
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        NotificationListResponse body = notificationService.getList(memberId, size, after);
        return APIResponse.ok(NotificationSuccessCode.NOTIFICATION_LISTED, body);
    }

    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<APIResponse<NotificationSummary>> readNotification(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable("id") Long notificationId
    ) {
        Long memberId = securitySupport.requireMemberId(principal);
        NotificationSummary body = notificationService.markRead(memberId, notificationId);
        return APIResponse.ok(NotificationSuccessCode.NOTIFICATION_READ, body);
    }

    @GetMapping("/stream")
    @Override
    public SseEmitter connectNotificationStream(@AuthenticationPrincipal MemberPrincipal principal) {
        Long memberId = securitySupport.requireMemberId(principal);
        List<NotificationSummary> unread = notificationService.getLatestUnread(memberId, 10);
        return notificationSseService.connect(memberId, unread);
    }
}
