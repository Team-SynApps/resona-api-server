package synapps.resona.api.mysql.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.global.dto.response.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "FCM 토큰 등록 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/token")
    public ResponseEntity<SuccessResponse<String>> registerToken(HttpServletRequest request, @RequestBody TokenRegisterRequest tokenRegisterRequest) {
        String result = memberNotificationService.registerToken(tokenRegisterRequest);
        return ResponseEntity
            .status(MemberSuccessCode.REGISTER_FCM_TOKEN_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.REGISTER_FCM_TOKEN_SUCCESS, createRequestInfo(request.getQueryString()), result));
    }

    @Operation(summary = "알림 설정 조회", description = "사용자의 현재 알림 설정을 조회합니다. (인증 필요)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림 설정 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/setting")
    public ResponseEntity<SuccessResponse<NotificationSettingResponse>> getNotificationSetting(HttpServletRequest request) {
        NotificationSettingResponse setting = memberNotificationService.getNotificationSetting();
        return ResponseEntity
            .status(MemberSuccessCode.GET_NOTIFICATION_SETTING_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.GET_NOTIFICATION_SETTING_SUCCESS, createRequestInfo(request.getQueryString()), setting));
    }

    @Operation(summary = "알림 설정 수정", description = "사용자의 알림 설정을 수정합니다. (인증 필요)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림 설정 수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/setting")
    public ResponseEntity<SuccessResponse<Void>> updateNotificationSetting(HttpServletRequest request, @RequestBody NotificationSettingUpdateRequest updateRequest) {
        memberNotificationService.updateNotificationSetting(updateRequest);
        return ResponseEntity
            .status(MemberSuccessCode.UPDATE_NOTIFICATION_SETTING_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.UPDATE_NOTIFICATION_SETTING_SUCCESS, createRequestInfo(request.getQueryString())));
    }

    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다. (커서 기반 페이징, 인증 필요)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<CursorResult<NotificationResponse>>> getNotifications(
        HttpServletRequest request,
        @Parameter(description = "다음 페이지를 위한 커서 ID (첫 페이지는 비워둠)") @RequestParam(required = false) Long cursorId,
        @Parameter(description = "페이지 당 알림 수") @RequestParam(defaultValue = "10") Integer size) {
        CursorResult<NotificationResponse> notifications = memberNotificationService.getNotifications(cursorId, size);
        return ResponseEntity
            .status(MemberSuccessCode.GET_NOTIFICATIONS_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.GET_NOTIFICATIONS_SUCCESS, createRequestInfo(request.getQueryString()), notifications));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 처리합니다. (인증 필요)")
    @Parameter(name = "notificationId", description = "읽음 처리할 알림의 ID", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 알림",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<SuccessResponse<Void>> readNotification(HttpServletRequest request, @PathVariable Long notificationId) {
        memberNotificationService.readNotification(notificationId);
        return ResponseEntity
            .status(MemberSuccessCode.READ_NOTIFICATION_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.READ_NOTIFICATION_SUCCESS, createRequestInfo(request.getQueryString())));
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다. (인증 필요)")
    @Parameter(name = "notificationId", description = "삭제할 알림의 ID", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 알림",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<SuccessResponse<Void>> deleteNotification(HttpServletRequest request, @PathVariable Long notificationId) {
        memberNotificationService.deleteNotification(notificationId);
        return ResponseEntity
            .status(MemberSuccessCode.DELETE_NOTIFICATION_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.DELETE_NOTIFICATION_SUCCESS, createRequestInfo(request.getQueryString())));
    }
}