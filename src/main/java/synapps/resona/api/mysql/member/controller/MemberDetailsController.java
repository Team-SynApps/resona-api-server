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
import synapps.resona.api.mysql.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.mysql.member.service.MemberDetailsService;

import java.util.List;

@RestController
@RequestMapping("/member-details")
@RequiredArgsConstructor
public class MemberDetailsController {
    private final MemberDetailsService memberDetailsService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> registerPersonalInfo(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  @Valid @RequestBody MemberDetailsRequest memberDetailsRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberDetailsService.register(memberDetailsRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> readPersonalInfo(HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberDetailsService.getPersonalInfo()));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> editPersonalInfo(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @Valid @RequestBody MemberDetailsRequest memberDetailsRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberDetailsService.editPersonalInfo(memberDetailsRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> deletePersonalInfo(HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        memberDetailsService.deletePersonalInfo();
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberDetailsService.deletePersonalInfo()));
        return ResponseEntity.ok(responseData);
    }
}
