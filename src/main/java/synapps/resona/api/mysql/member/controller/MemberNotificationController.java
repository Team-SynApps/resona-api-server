package synapps.resona.api.mysql.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.mysql.member.code.AuthErrorCode;
import synapps.resona.api.mysql.member.code.MemberErrorCode;
import synapps.resona.api.mysql.member.code.MemberSuccessCode;
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
    private final ServerInfoConfig serverInfo;

    private RequestInfo createRequestInfo(String path) {
        return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
    }

    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 서버에 등록합니다. (인증 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "REGISTER_FCM_TOKEN_SUCCESS", responseClass = String.class))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
        @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
    })
    @PostMapping("/token")
    public ResponseEntity<SuccessResponse<String>> registerToken(HttpServletRequest request, @RequestBody TokenRegisterRequest tokenRegisterRequest) {
        String result = memberNotificationService.registerToken(tokenRegisterRequest);
        return ResponseEntity
            .status(MemberSuccessCode.REGISTER_FCM_TOKEN_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.REGISTER_FCM_TOKEN_SUCCESS, createRequestInfo(request.getRequestURI()), result));
    }

    @Operation(summary = "알림 설정 조회", description = "사용자의 현재 알림 설정을 조회합니다. (인증 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_NOTIFICATION_SETTING_SUCCESS", responseClass = NotificationSettingResponse.class))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
        @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
    })
    @GetMapping("/setting")
    public ResponseEntity<SuccessResponse<NotificationSettingResponse>> getNotificationSetting(HttpServletRequest request) {
        NotificationSettingResponse setting = memberNotificationService.getNotificationSetting();
        return ResponseEntity
            .status(MemberSuccessCode.GET_NOTIFICATION_SETTING_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.GET_NOTIFICATION_SETTING_SUCCESS, createRequestInfo(request.getRequestURI()), setting));
    }

    @Operation(summary = "알림 설정 수정", description = "사용자의 알림 설정을 수정합니다. (인증 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "UPDATE_NOTIFICATION_SETTING_SUCCESS"))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "NOTIFICATION_SETTING_NOT_FOUND"}),
        @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
    })
    @PutMapping("/setting")
    public ResponseEntity<SuccessResponse<Void>> updateNotificationSetting(HttpServletRequest request, @RequestBody NotificationSettingUpdateRequest updateRequest) {
        memberNotificationService.updateNotificationSetting(updateRequest);
        return ResponseEntity
            .status(MemberSuccessCode.UPDATE_NOTIFICATION_SETTING_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.UPDATE_NOTIFICATION_SETTING_SUCCESS, createRequestInfo(request.getRequestURI())));
    }

    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다. (커서 기반 페이징, 인증 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_NOTIFICATIONS_SUCCESS", cursor = true, listElementClass = NotificationResponse.class))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
        @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<CursorResult<NotificationResponse>>> getNotifications(
        HttpServletRequest request,
        @Parameter(description = "다음 페이지를 위한 커서 ID (첫 페이지는 비워둠)") @RequestParam(required = false) Long cursorId,
        @Parameter(description = "페이지 당 알림 수") @RequestParam(defaultValue = "10") Integer size) {
        // 실제 반환 타입이 SuccessResponse<CursorResult<...>> 이므로, SuccessResponse.of()를 직접 사용하지 않고
        // 서비스에서 반환된 CursorResult 객체를 그대로 body에 전달하는 것이 더 적절할 수 있습니다.
        // 현재 구조를 유지하기 위해 SuccessResponse.of를 사용합니다.
        CursorResult<NotificationResponse> notifications = memberNotificationService.getNotifications(cursorId, size);
        return ResponseEntity
            .status(MemberSuccessCode.GET_NOTIFICATIONS_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.GET_NOTIFICATIONS_SUCCESS, createRequestInfo(request.getRequestURI()), notifications));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 처리합니다. (인증 필요)")
    @Parameter(name = "notificationId", description = "읽음 처리할 알림의 ID", required = true, in = ParameterIn.PATH)
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "READ_NOTIFICATION_SUCCESS"))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "NOTIFICATION_NOT_FOUND"}),
        @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
    })
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<SuccessResponse<Void>> readNotification(HttpServletRequest request, @PathVariable Long notificationId) {
        memberNotificationService.readNotification(notificationId);
        return ResponseEntity
            .status(MemberSuccessCode.READ_NOTIFICATION_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.READ_NOTIFICATION_SUCCESS, createRequestInfo(request.getRequestURI())));
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다. (인증 필요)")
    @Parameter(name = "notificationId", description = "삭제할 알림의 ID", required = true, in = ParameterIn.PATH)
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "DELETE_NOTIFICATION_SUCCESS"))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"NOTIFICATION_NOT_FOUND"}),
        @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<SuccessResponse<Void>> deleteNotification(HttpServletRequest request, @PathVariable Long notificationId) {
        memberNotificationService.deleteNotification(notificationId);
        return ResponseEntity
            .status(MemberSuccessCode.DELETE_NOTIFICATION_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.DELETE_NOTIFICATION_SUCCESS, createRequestInfo(request.getRequestURI())));
    }
}