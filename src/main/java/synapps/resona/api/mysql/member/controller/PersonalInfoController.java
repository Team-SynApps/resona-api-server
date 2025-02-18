package synapps.resona.api.mysql.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.member.dto.request.personal_info.PersonalInfoRequest;
import synapps.resona.api.mysql.member.service.PersonalInfoService;

import java.util.List;

@RestController
@RequestMapping("/personal-info")
@RequiredArgsConstructor
public class PersonalInfoController {
    private final PersonalInfoService personalInfoService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> registerPersonalInfo(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  @Valid @RequestBody PersonalInfoRequest personalInfoRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(personalInfoService.register(personalInfoRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> readPersonalInfo(HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(personalInfoService.getPersonalInfo()));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> editPersonalInfo(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @Valid @RequestBody PersonalInfoRequest personalInfoRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(personalInfoService.editPersonalInfo(personalInfoRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping
    @PreAuthorize("@memberSecurity.isCurrentUser(#request)")
    public ResponseEntity<?> deletePersonalInfo(HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        personalInfoService.deletePersonalInfo();
        ResponseDto responseData = new ResponseDto(metaData, List.of(personalInfoService.deletePersonalInfo()));
        return ResponseEntity.ok(responseData);
    }
}
