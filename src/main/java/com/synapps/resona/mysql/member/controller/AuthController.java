package com.synapps.resona.mysql.member.controller;

import com.synapps.resona.global.config.ServerInfoConfig;
import com.synapps.resona.global.dto.MetaDataDto;
import com.synapps.resona.global.dto.ResponseDto;
import com.synapps.resona.mysql.member.dto.request.LoginRequest;
import com.synapps.resona.mysql.member.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString){
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping
    public ResponseEntity<?> authenticateUser(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody LoginRequest loginRequest) {
        return authService.login(request, response, loginRequest);
    }

//    @PostMapping("/apple")
//    public ResponseEntity<?> appleLogin(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestBody AppleLoginRequest appleRequest
//    ) {
//        return authService.appleOAuthLogin(request, response, appleRequest);
//    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken (HttpServletRequest request, HttpServletResponse response) {
        return authService.refresh(request, response);
    }

    @GetMapping("/member")
    public ResponseEntity<?> memberExists(HttpServletRequest request, HttpServletResponse response) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        return ResponseEntity.ok().body(new ResponseDto(metaData, List.of(authService.isMember(request, response))));
    }
}