package synapps.resona.api.mysql.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.member.dto.request.profile.DuplicateTagRequest;
import synapps.resona.api.mysql.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.mysql.member.service.ProfileService;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;
  private final ServerInfoConfig serverInfo;

  private MetaDataDto createSuccessMetaData(String queryString) {
    return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  /**
   * security context에 존재하는 유저의 정보를 가져오기 때문에 권한 체크를 하지 않아도 됨.
   *
   * @param request
   * @param profileRequest
   * @return
   * @throws Exception
   */
  @PostMapping
  public ResponseEntity<?> registerProfile(HttpServletRequest request,
      @Valid @RequestBody ProfileRequest profileRequest) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(profileService.register(profileRequest)));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping
  public ResponseEntity<?> readProfile(HttpServletRequest request) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.readProfile()));
    return ResponseEntity.ok(responseData);
  }

//    @GetMapping("/{memberId}")
//    public ResponseEntity<?> getProfileByMemberId(HttpServletRequest request,
//                                                  HttpServletResponse response,
//                                                  @PathVariable Long memberId) throws Exception {
//        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
//        ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.getProfileByMemberId(memberId)));
//        return ResponseEntity.ok(responseData);
//    }

  @PutMapping
  public ResponseEntity<?> editProfile(HttpServletRequest request,
      @Valid @RequestBody ProfileRequest profileRequest) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(profileService.editProfile(profileRequest)));
    return ResponseEntity.ok(responseData);
  }

  @DeleteMapping
  public ResponseEntity<?> deleteProfile(HttpServletRequest request) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.deleteProfile()));
    return ResponseEntity.ok(responseData);
  }

  @PostMapping("/duplicate-tag")
  public ResponseEntity<?> checkDuplicateId(HttpServletRequest request,
      @RequestBody DuplicateTagRequest duplicateTagRequest) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(profileService.checkDuplicateTag(duplicateTagRequest.getTag())));
    return ResponseEntity.ok(responseData);
  }

  // 닉네임 중복 요청 로직 들어가야 함
}
