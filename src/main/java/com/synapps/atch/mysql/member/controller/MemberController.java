package com.synapps.atch.mysql.member.controller;


import com.synapps.atch.global.config.ServerInfoConfig;
import com.synapps.atch.global.dto.MetaDataDto;
import com.synapps.atch.global.dto.ResponseDto;
import com.synapps.atch.mysql.member.dto.request.DuplicateIdRequest;
import com.synapps.atch.mysql.member.dto.request.SignupRequest;
import com.synapps.atch.mysql.member.exception.MemberException;
import com.synapps.atch.mysql.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString){
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

    @PostMapping("/duplicate-id")
    public ResponseEntity<?> checkDuplicateId(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestBody DuplicateIdRequest duplicateIdRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.checkDuplicateId(duplicateIdRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser(HttpServletRequest request,
                                        HttpServletResponse response) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.deleteUser()));
        return ResponseEntity.ok(responseData);
    }

}