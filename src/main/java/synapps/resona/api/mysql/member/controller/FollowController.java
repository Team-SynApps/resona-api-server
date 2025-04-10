package synapps.resona.api.mysql.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.member.dto.response.MemberProfileDto;
import synapps.resona.api.mysql.member.service.FollowService;

import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    /**
     * 로그인된 사용자가 특정 memberId를 팔로우함
     */
    @PostMapping("/{memberId}")
    public ResponseEntity<?> follow(@PathVariable Long memberId,
                                    HttpServletRequest request) {
        followService.follow(memberId);

        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of("followed"));
        return ResponseEntity.ok(responseData);
    }

    /**
     * 로그인된 사용자가 특정 memberId를 언팔로우함
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> unfollow(@PathVariable Long memberId,
                                      HttpServletRequest request) {
        followService.unfollow(memberId);

        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of("unfollowed"));
        return ResponseEntity.ok(responseData);
    }

    /**
     * 특정 사용자의 팔로워 목록 조회
     */
    @GetMapping("/{memberId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long memberId,
                                          HttpServletRequest request) {
        List<MemberProfileDto> followers = followService.getFollowers(memberId);

        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, followers);
        return ResponseEntity.ok(responseData);
    }

    /**
     * 특정 사용자의 팔로잉 목록 조회
     */
    @GetMapping("/{memberId}/followings")
    public ResponseEntity<?> getFollowings(@PathVariable Long memberId,
                                           HttpServletRequest request) {
        List<MemberProfileDto> followings = followService.getFollowings(memberId);

        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, followings);
        return ResponseEntity.ok(responseData);
    }
}
