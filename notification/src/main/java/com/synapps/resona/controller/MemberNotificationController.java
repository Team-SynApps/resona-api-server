package com.synapps.resona.controller;

import com.synapps.resona.code.NotificationErrorCode;
import com.synapps.resona.code.NotificationSuccessCode;
import com.synapps.resona.dto.request.NotificationSettingUpdateRequest;
import com.synapps.resona.dto.request.TokenRegisterRequest;
import com.synapps.resona.dto.response.NotificationResponse;
import com.synapps.resona.dto.response.NotificationSettingResponse;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.service.MemberNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = NotificationSuccessCode.class, code = "REGISTER_FCM_TOKEN_SUCCESS", responseClass = String.class))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = NotificationErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
    })
    @PostMapping("/token")
    public ResponseEntity<SuccessResponse<String>> registerToken(
        HttpServletRequest request,
        @RequestBody TokenRegisterRequest tokenRegisterRequest,
        @AuthenticationPrincipal AuthenticatedUser user) {
        Long memberId = user.getMemberId();
        String result = memberNotificationService.registerToken(tokenRegisterRequest, memberId);
        return ResponseEntity
            .status(NotificationSuccessCode.REGISTER_FCM_TOKEN_SUCCESS.getStatus())
            .body(SuccessResponse.of(NotificationSuccessCode.REGISTER_FCM_TOKEN_SUCCESS, createRequestInfo(request.getRequestURI()), result));
    }

    @Operation(summary = "알림 설정 조회", description = "사용자의 현재 알림 설정을 조회합니다. (인증 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = NotificationSuccessCode.class, code = "GET_NOTIFICATION_SETTING_SUCCESS", responseClass = NotificationSettingResponse.class))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = NotificationErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
    })
    @GetMapping("/setting")
    public ResponseEntity<SuccessResponse<NotificationSettingResponse>> getNotificationSetting(
        HttpServletRequest request,
        @AuthenticationPrincipal AuthenticatedUser user) {
        Long memberId = user.getMemberId();
        NotificationSettingResponse setting = memberNotificationService.getNotificationSetting(memberId);
        return ResponseEntity
            .status(NotificationSuccessCode.GET_NOTIFICATION_SETTING_SUCCESS.getStatus())
            .body(SuccessResponse.of(NotificationSuccessCode.GET_NOTIFICATION_SETTING_SUCCESS, createRequestInfo(request.getRequestURI()), setting));
    }

    @Operation(summary = "알림 설정 수정", description = "사용자의 알림 설정을 수정합니다. (인증 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = NotificationSuccessCode.class, code = "UPDATE_NOTIFICATION_SETTING_SUCCESS"))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = NotificationErrorCode.class, codes = {"MEMBER_NOT_FOUND", "NOTIFICATION_SETTING_NOT_FOUND"}),
    })
    @PutMapping("/setting")
    public ResponseEntity<SuccessResponse<Void>> updateNotificationSetting(
        HttpServletRequest request,
        @RequestBody NotificationSettingUpdateRequest updateRequest,
        @AuthenticationPrincipal AuthenticatedUser user) {
        Long memberId = user.getMemberId();
        memberNotificationService.updateNotificationSetting(updateRequest, memberId);
        return ResponseEntity
            .status(NotificationSuccessCode.UPDATE_NOTIFICATION_SETTING_SUCCESS.getStatus())
            .body(SuccessResponse.of(NotificationSuccessCode.UPDATE_NOTIFICATION_SETTING_SUCCESS, createRequestInfo(request.getRequestURI())));
    }

    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다. (커서 기반 페이징, 인증 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = NotificationSuccessCode.class, code = "GET_NOTIFICATIONS_SUCCESS", cursor = true, listElementClass = NotificationResponse.class))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = NotificationErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<CursorResult<NotificationResponse>>> getNotifications(
        HttpServletRequest request,
        @Parameter(description = "다음 페이지를 위한 커서 ID (첫 페이지는 비워둠)") @RequestParam(required = false) Long cursorId,
        @Parameter(description = "페이지 당 알림 수") @RequestParam(defaultValue = "10") Integer size,
        @AuthenticationPrincipal AuthenticatedUser user) {
        Long memberId = user.getMemberId();
        CursorResult<NotificationResponse> notifications = memberNotificationService.getNotifications(cursorId, size, memberId);
        return ResponseEntity
            .status(NotificationSuccessCode.GET_NOTIFICATIONS_SUCCESS.getStatus())
            .body(SuccessResponse.of(NotificationSuccessCode.GET_NOTIFICATIONS_SUCCESS, createRequestInfo(request.getRequestURI()), notifications));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 처리합니다. (인증 필요)")
    @Parameter(name = "notificationId", description = "읽음 처리할 알림의 ID", required = true, in = ParameterIn.PATH)
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = NotificationSuccessCode.class, code = "READ_NOTIFICATION_SUCCESS"))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = NotificationErrorCode.class, codes = {"MEMBER_NOT_FOUND", "NOTIFICATION_NOT_FOUND"}),
    })
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<SuccessResponse<Void>> readNotification(
        HttpServletRequest request,
        @PathVariable Long notificationId,
        @AuthenticationPrincipal AuthenticatedUser user) {
        Long memberId = user.getMemberId();
        memberNotificationService.readNotification(notificationId, memberId);
        return ResponseEntity
            .status(NotificationSuccessCode.READ_NOTIFICATION_SUCCESS.getStatus())
            .body(SuccessResponse.of(NotificationSuccessCode.READ_NOTIFICATION_SUCCESS, createRequestInfo(request.getRequestURI())));
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다. (인증 필요)")
    @Parameter(name = "notificationId", description = "삭제할 알림의 ID", required = true, in = ParameterIn.PATH)
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = NotificationSuccessCode.class, code = "DELETE_NOTIFICATION_SUCCESS"))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = NotificationErrorCode.class, codes = {"NOTIFICATION_NOT_FOUND"}),
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<SuccessResponse<Void>> deleteNotification(
        HttpServletRequest request,
        @PathVariable Long notificationId,
        @AuthenticationPrincipal AuthenticatedUser user) {
        Long memberId = user.getMemberId();
        memberNotificationService.deleteNotification(notificationId, memberId);
        return ResponseEntity
            .status(NotificationSuccessCode.DELETE_NOTIFICATION_SUCCESS.getStatus())
            .body(SuccessResponse.of(NotificationSuccessCode.DELETE_NOTIFICATION_SUCCESS, createRequestInfo(request.getRequestURI())));
    }
}