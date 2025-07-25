package synapps.resona.api.mysql.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.mysql.member.dto.request.notification.NotificationSettingUpdateRequest;
import synapps.resona.api.mysql.member.dto.request.notification.TokenRegisterRequest;
import synapps.resona.api.mysql.member.dto.response.notification.NotificationResponse;
import synapps.resona.api.mysql.member.dto.response.notification.NotificationSettingResponse;
import synapps.resona.api.mysql.member.service.MemberNotificationService;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Tag(name = "Member Notification", description = "회원 알림 관련 API")
public class MemberNotificationController {

    private final MemberNotificationService memberNotificationService;

    @PostMapping("/token")
    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 서버에 등록합니다.")
    public ResponseEntity<String> registerToken(@RequestBody TokenRegisterRequest tokenRegisterRequest) {
        return ResponseEntity.ok(memberNotificationService.registerToken(tokenRegisterRequest));
    }

    @GetMapping("/setting")
    @Operation(summary = "알림 설정 조회", description = "사용자의 현재 알림 설정을 조회합니다.")
    public ResponseEntity<NotificationSettingResponse> getNotificationSetting() {
        return ResponseEntity.ok(memberNotificationService.getNotificationSetting());
    }

    @PutMapping("/setting")
    @Operation(summary = "알림 설정 수정", description = "사용자의 알림 설정을 수정합니다.")
    public ResponseEntity<Void> updateNotificationSetting(@RequestBody NotificationSettingUpdateRequest request) {
        memberNotificationService.updateNotificationSetting(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다. (커서 기반 페이징)")
    public ResponseEntity<CursorResult<NotificationResponse>> getNotifications(
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(memberNotificationService.getNotifications(cursorId, size));
    }

    @PostMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 처리합니다.")
    public ResponseEntity<Void> readNotification(@PathVariable Long notificationId) {
        memberNotificationService.readNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        memberNotificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}
