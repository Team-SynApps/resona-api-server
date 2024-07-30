package com.synapps.atch.mysql.member.controller;

import com.synapps.atch.global.dto.ResponseDto;
import com.synapps.atch.mysql.member.dto.request.LoginRequest;
import com.synapps.atch.mysql.member.service.AuthService;
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
        return ResponseEntity.ok().body(new ResponseDto(true, List.of(authService.isMember(request, response))));
    }
}