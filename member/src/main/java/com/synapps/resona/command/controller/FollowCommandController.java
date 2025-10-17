package com.synapps.resona.command.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.MemberSuccessCode;
import com.synapps.resona.command.dto.response.MemberProfileDto;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.command.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Follow Command", description = "팔로우/언팔로우 API")
@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowCommandController {

  private final FollowService followService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "사용자 팔로우", description = "특정 사용자를 팔로우합니다. (인증 필요)")
  @Parameter(name = "memberId", description = "팔로우할 사용자의 ID", required = true, in = ParameterIn.PATH)
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "FOLLOW_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "FOLLOWING_NOT_FOUND", "FOLLOWING_MYSELF", "ALREADY_FOLLOWING"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/{memberId}")
  public ResponseEntity<SuccessResponse<Void>> follow(@PathVariable Long memberId, HttpServletRequest request) {
    followService.follow(memberId);
    return ResponseEntity
        .status(MemberSuccessCode.FOLLOW_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.FOLLOW_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "사용자 언팔로우", description = "특정 사용자를 언팔로우합니다. (인증 필요)")
  @Parameter(name = "memberId", description = "언팔로우할 사용자의 ID", required = true, in = ParameterIn.PATH)
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "UNFOLLOW_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "FOLLOWING_NOT_FOUND", "FOLLOW_RELATIONSHIP_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/{memberId}")
  public ResponseEntity<SuccessResponse<Void>> unfollow(@PathVariable Long memberId, HttpServletRequest request) {
    followService.unfollow(memberId);
    return ResponseEntity
        .status(MemberSuccessCode.UNFOLLOW_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.UNFOLLOW_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}