package synapps.resona.api.mysql.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.ServerInfoConfig;
import synapps.resona.api.global.dto.MetaDataDto;
import synapps.resona.api.global.dto.ResponseDto;
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

    @PutMapping
    public ResponseEntity<?> editProfile(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @Valid @RequestBody ProfileRegisterRequest profileRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.editProfile(profileRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteProfile(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(profileService.deleteProfile()));
        return ResponseEntity.ok(responseData);
    }
}
