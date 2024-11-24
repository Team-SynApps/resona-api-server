package synapps.resona.api.mysql.member.controller;

import synapps.resona.api.global.config.ServerInfoConfig;
import synapps.resona.api.global.dto.MetaDataDto;
import synapps.resona.api.global.dto.ResponseDto;
import synapps.resona.api.mysql.member.dto.request.AppleLoginRequest;
import synapps.resona.api.mysql.member.dto.request.LoginRequest;
import synapps.resona.api.mysql.member.service.AuthService;
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

    @PostMapping("/apple")
    public ResponseEntity<?> appleLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody AppleLoginRequest appleRequest
    ) throws Exception {
        return authService.appleLogin(request, response, appleRequest);
    }

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