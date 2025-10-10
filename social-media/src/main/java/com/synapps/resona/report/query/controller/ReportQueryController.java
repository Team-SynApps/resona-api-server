package com.synapps.resona.report.query.controller;

import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.report.common.entity.ReportStatus;
import com.synapps.resona.report.dto.ReportDto;
import com.synapps.resona.report.query.service.ReportQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Report Query", description = "신고 조회 API (관리자용)")
@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportQueryController {

  private final ReportQueryService reportQueryService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "신고 목록 조회", description = "상태별로 신고 목록을 조회합니다. (관리자 권한 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_REPORTS_SUCCESS"))
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Page<ReportDto>>> getReports(
      HttpServletRequest request,
      @RequestParam(defaultValue = "PENDING") ReportStatus status,
      @PageableDefault(size = 20) Pageable pageable
  ) {
    Page<ReportDto> reports = reportQueryService.getReportsByStatus(status, pageable);
    return ResponseEntity.status(SocialSuccessCode.GET_REPORTS_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_REPORTS_SUCCESS, createRequestInfo(request.getRequestURI()), reports));
  }
}
