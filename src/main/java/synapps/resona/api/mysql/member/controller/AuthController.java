package synapps.resona.api.mysql.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.ErrorMeta;
import synapps.resona.api.global.dto.metadata.Meta;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.global.error.core.GlobalErrorCode;
import synapps.resona.api.mysql.member.dto.request.auth.AppleLoginRequest;
import synapps.resona.api.mysql.member.dto.request.auth.LoginRequest;
import synapps.resona.api.mysql.member.dto.request.auth.RefreshRequest;
import synapps.resona.api.mysql.member.dto.response.TokenResponse;
import synapps.resona.api.mysql.member.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final ServerInfoConfig serverInfo;

  private Meta createSuccessMetaData(String queryString) {
    return Meta.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  @PostMapping
  public ResponseEntity<?> authenticateUser(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody LoginRequest loginRequest) {
    TokenResponse tokenResponse = authService.login(loginRequest);
    Meta metaData = createSuccessMetaData(request.getQueryString());
    return ResponseEntity.ok(new ResponseDto(metaData, List.of(tokenResponse)));
  }

  @PostMapping("/apple")
  public ResponseEntity<?> appleLogin(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody AppleLoginRequest appleRequest) throws Exception {
    TokenResponse tokenResponse = authService.appleLogin(request, response, appleRequest);
    Meta metaData = createSuccessMetaData(request.getQueryString());
    return ResponseEntity.ok(new ResponseDto(metaData, List.of(tokenResponse)));
  }

  @GetMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(
      HttpServletRequest request,
      @RequestBody RefreshRequest refreshRequest) {
    TokenResponse tokenResponse = authService.refresh(request, refreshRequest);
    Meta metaData = createSuccessMetaData(request.getQueryString());
    return ResponseEntity.ok(new ResponseDto(metaData, List.of(tokenResponse)));
  }

  @GetMapping("/member")
  public ResponseEntity<?> memberExists(HttpServletRequest request, HttpServletResponse response) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    return ResponseEntity.ok()
        .body(new ResponseDto(metaData, List.of(authService.isMember(request, response))));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ResponseDto> handleDataValidationException(
      AuthenticationException ex,
      HttpServletRequest request
  ) {
    ErrorMeta metaData = ErrorMeta.createErrorMetaData(
        GlobalErrorCode.INVALID_CLIENT.getStatus().value(),
        "계정정보가 일치하지 않습니다.",
        request.getRequestURI(),
        serverInfo.getApiVersion(),
        serverInfo.getServerName(),
        GlobalErrorCode.INVALID_CLIENT.getCode()
    );

    ResponseDto responseData = new ResponseDto(
        metaData,
        List.of(Map.of(
            "validationErrors", ex.getMessage()
        ))
    );

    return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
  }
}