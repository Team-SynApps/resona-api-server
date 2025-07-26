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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.SuccessResponse;
import synapps.resona.api.mysql.member.code.MemberSuccessCode;
import synapps.resona.api.mysql.member.dto.response.MemberProfileDto;
import synapps.resona.api.mysql.member.service.FollowService;

@Tag(name = "Follow", description = "팔로우/언팔로우 및 목록 조회 API")
@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

  private final FollowService followService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "사용자 팔로우", description = "특정 사용자를 팔로우합니다. (인증 필요)")
  @Parameter(name = "memberId", description = "팔로우할 사용자의 ID", required = true, in = ParameterIn.PATH)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "팔로우 성공",
          content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/{memberId}")
  public ResponseEntity<SuccessResponse<Void>> follow(@PathVariable Long memberId,
      HttpServletRequest request) {
    followService.follow(memberId);
    return ResponseEntity
        .status(MemberSuccessCode.FOLLOW_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.FOLLOW_SUCCESS, createRequestInfo(request.getQueryString())));
  }

  @Operation(summary = "사용자 언팔로우", description = "특정 사용자를 언팔로우합니다. (인증 필요)")
  @Parameter(name = "memberId", description = "언팔로우할 사용자의 ID", required = true, in = ParameterIn.PATH)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "언팔로우 성공",
          content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{memberId}")
  public ResponseEntity<SuccessResponse<Void>> unfollow(@PathVariable Long memberId,
      HttpServletRequest request) {
    followService.unfollow(memberId);
    return ResponseEntity
        .status(MemberSuccessCode.UNFOLLOW_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.UNFOLLOW_SUCCESS, createRequestInfo(request.getQueryString())));
  }

  @Operation(summary = "팔로워 목록 조회", description = "특정 사용자의 팔로워 목록을 조회합니다.")
  @Parameter(name = "memberId", description = "조회할 사용자의 ID", required = true, in = ParameterIn.PATH)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "팔로워 목록 조회 성공"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{memberId}/followers")
  public ResponseEntity<SuccessResponse<List<MemberProfileDto>>> getFollowers(@PathVariable Long memberId,
      HttpServletRequest request) {
    List<MemberProfileDto> followers = followService.getFollowers(memberId);
    return ResponseEntity
        .status(MemberSuccessCode.GET_FOLLOWERS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.GET_FOLLOWERS_SUCCESS, createRequestInfo(request.getQueryString()), followers));
  }

  @Operation(summary = "팔로잉 목록 조회", description = "특정 사용자의 팔로잉 목록을 조회합니다.")
  @Parameter(name = "memberId", description = "조회할 사용자의 ID", required = true, in = ParameterIn.PATH)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회 성공"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{memberId}/followings")
  public ResponseEntity<SuccessResponse<List<MemberProfileDto>>> getFollowings(@PathVariable Long memberId,
      HttpServletRequest request) {
    List<MemberProfileDto> followings = followService.getFollowings(memberId);
    return ResponseEntity
        .status(MemberSuccessCode.GET_FOLLOWINGS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.GET_FOLLOWINGS_SUCCESS, createRequestInfo(request.getQueryString()), followings));
  }
}