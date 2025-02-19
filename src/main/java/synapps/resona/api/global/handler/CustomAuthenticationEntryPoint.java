package synapps.resona.api.global.handler;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.ErrorMetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.global.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final ServerInfoConfig serverInfo;
    private static final Logger logger = LogManager.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ErrorCode errorCode = ErrorCode.TOKEN_NOT_FOUND;  // 기본적으로 토큰이 없는 경우로 처리
        logger.error("Authentication error: {}", errorCode.getMessage(), authException);

        ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                request.getRequestURI(),
                serverInfo.getVersionNumber(),
                serverInfo.getServerName(),
                errorCode.getCode()
        );

        ResponseDto responseData = new ResponseDto(
                metaData,
                List.of(Map.of("error", errorCode.getMessage()))
        );

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(responseData);
        response.getWriter().write(jsonResponse);
    }
}