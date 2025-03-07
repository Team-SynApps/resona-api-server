package synapps.resona.api.mysql.member.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.member.dto.request.auth.DuplicateIdRequest;
import synapps.resona.api.mysql.member.dto.request.auth.SignupRequest;
import synapps.resona.api.mysql.member.dto.request.member.MemberPasswordChangeDto;
import synapps.resona.api.mysql.member.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }


    @PostMapping("/join")
    public ResponseEntity<?> join(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @Valid @RequestBody SignupRequest signupRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.signUp(signupRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUser(HttpServletRequest request,
                                     HttpServletResponse response) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.getMember()));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getMemberDetailInfo(HttpServletRequest request,
                                                 HttpServletResponse response) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.getMemberDetailInfo()));
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/duplicate-id")
    public ResponseEntity<?> checkDuplicateId(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestBody DuplicateIdRequest duplicateIdRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.checkDuplicateId(duplicateIdRequest)));
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestBody MemberPasswordChangeDto requestBody) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseDto = new ResponseDto(metaData, List.of(memberService.changePassword(request, requestBody)));
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser(HttpServletRequest request,
                                        HttpServletResponse response) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.deleteUser()));
        return ResponseEntity.ok(responseData);
    }

}