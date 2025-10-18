package com.synapps.resona.report.command.controller;

import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.report.code.AdminSuccessCode;
import com.synapps.resona.report.command.service.SanctionService;
import com.synapps.resona.report.dto.request.SanctionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "관리자 기능 API")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SanctionService sanctionService;
    private final ServerInfoConfig serverInfoConfig;

    @Operation(summary = "사용자 제재", description = "특정 사용자를 제재합니다. (관리자 권한 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = AdminSuccessCode.class, code = "SANCTION_SUCCESS"))
    @ApiErrorSpec({
            @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"}),
            @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})
    })
    @PostMapping("/sanctions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> sanctionUser(HttpServletRequest servletRequest, @Valid @RequestBody SanctionRequest request) {
        sanctionService.sanctionUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(AdminSuccessCode.SANCTION_SUCCESS, new RequestInfo(serverInfoConfig.getApiVersion(), serverInfoConfig.getServerName(), servletRequest.getRequestURI())));
    }
}
