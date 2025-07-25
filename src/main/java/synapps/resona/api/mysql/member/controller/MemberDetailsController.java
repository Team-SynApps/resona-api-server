package synapps.resona.api.mysql.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.Meta;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.mysql.member.service.MemberDetailsService;

@RestController
@RequestMapping("/member-details")
@RequiredArgsConstructor
public class MemberDetailsController {

  private final MemberDetailsService memberDetailsService;
  private final ServerInfoConfig serverInfo;

  private Meta createSuccessMetaData(String queryString) {
    return Meta.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  @PostMapping
  @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
  public ResponseEntity<?> registerPersonalInfo(HttpServletRequest request,
      @Valid @RequestBody MemberDetailsRequest memberDetailsRequest) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(memberDetailsService.register(memberDetailsRequest)));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping
  public ResponseEntity<?> readPersonalInfo(HttpServletRequest request) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(memberDetailsService.getMemberDetails()));
    return ResponseEntity.ok(responseData);
  }

//    @GetMapping("/{memberId}")
//    public ResponseEntity<?> getMemberDetailsByMemberId(HttpServletRequest request,
//                                                  HttpServletResponse response,
//                                                  @PathVariable Long memberId) throws Exception {
//        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
//        ResponseDto responseData = new ResponseDto(metaData, List.of(memberDetailsService.getMemberDetails(memberId)));
//        return ResponseEntity.ok(responseData);
//    }

  @PutMapping
  @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
  public ResponseEntity<?> editPersonalInfo(HttpServletRequest request,
      @Valid @RequestBody MemberDetailsRequest memberDetailsRequest) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(memberDetailsService.editMemberDetails(memberDetailsRequest)));
    return ResponseEntity.ok(responseData);
  }

  @DeleteMapping
  @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
  public ResponseEntity<?> deletePersonalInfo(HttpServletRequest request) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(memberDetailsService.deleteMemberDetails()));
    return ResponseEntity.ok(responseData);
  }
}
