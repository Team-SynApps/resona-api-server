package synapps.resona.api.mysql.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.member.dto.request.profile.ProfileRegisterRequest;
import synapps.resona.api.mysql.member.service.ProfileService;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> registerProfile(HttpServletRequest request,
                                             HttpServletResponse response,
                                             @Valid @RequestBody ProfileRegisterRequest profileRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.register(profileRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping
    public ResponseEntity<?> readProfile(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.getProfile()));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<?> getProfileByMemberId(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  @PathVariable Long memberId) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.getProfileByMemberId(memberId)));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> editProfile(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @Valid @RequestBody ProfileRegisterRequest profileRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.editProfile(profileRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> deleteProfile(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.deleteProfile()));
        return ResponseEntity.ok(responseData);
    }

    // 닉네임 중복 요청 로직 들어가야 함
}
