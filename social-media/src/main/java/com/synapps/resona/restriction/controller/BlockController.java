package com.synapps.resona.restriction.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.member.UserPrincipal;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.restriction.dto.BlockedMemberResponse;
import com.synapps.resona.restriction.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Block", description = "사용자 차단 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class BlockController {

  private final BlockService blockService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "사용자 차단", description = "특정 사용자를 차단합니다. 차단된 사용자의 피드, 댓글 등이 보이지 않게 됩니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "BLOCK_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"CANNOT_BLOCK_SELF", "ALREADY_BLOCKED"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/member/{memberId}/block")
  public ResponseEntity<SuccessResponse<Void>> blockMember(HttpServletRequest request,
      @PathVariable Long memberId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberDto = MemberDto.from(userPrincipal);
    blockService.blockMember(memberId, memberDto);
    return ResponseEntity
        .status(SocialSuccessCode.BLOCK_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.BLOCK_SUCCESS,
            createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "사용자 차단 해제", description = "차단한 사용자를 해제합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "UNBLOCK_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"NOT_BLOCKED"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/member/{memberId}/unblock")
  public ResponseEntity<SuccessResponse<Void>> unblockMember(HttpServletRequest request,
      @PathVariable Long memberId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberDto = MemberDto.from(userPrincipal);
    blockService.unblockMember(memberId, memberDto);
    return ResponseEntity
        .status(SocialSuccessCode.UNBLOCK_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNBLOCK_SUCCESS,
            createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "차단한 사용자 조회", description = "차단한 사용자를 조회할 수 있습니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "BLOCK_LIST_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @GetMapping("/member/my-blocks")
  public ResponseEntity<SuccessResponse<List<BlockedMemberResponse>>> readBlockedMembers(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberDto = MemberDto.from(userPrincipal);
    List<BlockedMemberResponse> response = blockService.getBlockedMembers(memberDto);
    return ResponseEntity
        .status(SocialSuccessCode.BLOCK_LIST_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.BLOCK_LIST_SUCCESS,
            createRequestInfo(request.getRequestURI()), response));
  }
}