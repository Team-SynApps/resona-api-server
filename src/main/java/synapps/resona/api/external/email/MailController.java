package synapps.resona.api.external.email;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import synapps.resona.api.external.email.exception.EmailException;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.mysql.member.service.TempTokenService;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final ServerInfoConfig serverInfo;
    private final RedisService redisService;
    private final TempTokenService tempTokenService;

    private MetaDataDto createSuccessMetaData(String queryString){
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    private MetaDataDto createFailMetaData(int status, String message, String queryString) {
        return MetaDataDto.createErrorMetaData(status, message, queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    // 인증 이메일 전송
    @PostMapping()
    public ResponseEntity<?> sendMail(HttpServletRequest request, String mail) throws EmailException {
        HashMap<String, Object> map = new HashMap<>();

        if (!redisService.canSendEmail(mail)) {
            map.put("success", Boolean.FALSE);
            map.put("error", "일일 최대 발송 횟수를 초과했습니다.");
            map.put("remainingAttempts", 0);
            throw EmailException.trialExceeded();

        } else {
            int number = mailService.sendMail(mail);
            String num = String.valueOf(number);

            redisService.setCode(mail, num);
            map.put("success", Boolean.TRUE);
            map.put("remainingAttempts", redisService.getRemainingEmailSends(mail));
        }

        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(map));

        return ResponseEntity.ok(responseData);
    }

    // 인증번호 일치여부 확인
    @PostMapping("/verification")
    public ResponseEntity<?> mailCheck(HttpServletRequest request, @Valid @RequestBody EmailCheckDto emailCheckDto) throws EmailException {
        boolean isMatch = emailCheckDto.getNumber().equals(redisService.getCode(emailCheckDto.getEmail()));
        HashMap<String, Object> map = new HashMap<>();

        if (!redisService.canCheckNumber(emailCheckDto.getEmail())) {
            throw EmailException.trialExceeded();
        } else if(!isMatch) {
            MetaDataDto metaData = createFailMetaData(406, "인증번호가 일치하지 않습니다.", request.getQueryString());

            int remainingCount = redisService.getRemainingNumberMatch(emailCheckDto.getEmail());
            map.put("remaining count", remainingCount);

            ResponseDto responseData = new ResponseDto(metaData, List.of(map));
            return new ResponseEntity(responseData, HttpStatus.NOT_ACCEPTABLE);
        }

        int remainingCount = redisService.getRemainingNumberMatch(emailCheckDto.getEmail());
        map.put("remaining count", remainingCount);
        map.put("isMatch", isMatch);
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(map));
        return ResponseEntity.ok(responseData);
    }

    // 인증번호 일치여부 확인 후 토큰 발급
    @PostMapping("/temp-token")
    public ResponseEntity<?> mailCheckAndIssueToken(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody EmailCheckDto emailCheckDto) throws EmailException {
        boolean isMatch = emailCheckDto.getNumber().equals(redisService.getCode(emailCheckDto.getEmail()));

        if (!redisService.canCheckNumber(emailCheckDto.getEmail())) {
            throw EmailException.trialExceeded();
        } else if(isMatch) {
            MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
            ResponseDto responseData = new ResponseDto(metaData, List.of(tempTokenService.createTemporaryToken(request, response, emailCheckDto.getEmail())));
            return ResponseEntity.ok(responseData);
        }

        MetaDataDto metaData = createFailMetaData(406, "인증번호가 일치하지 않습니다.", request.getQueryString());
        HashMap<String, Object> map = new HashMap<>();

        int remainingCount = redisService.getRemainingNumberMatch(emailCheckDto.getEmail());
        map.put("remaining count", remainingCount);

        ResponseDto responseData = new ResponseDto(metaData, List.of(map));
        return new ResponseEntity(responseData, HttpStatus.NOT_ACCEPTABLE);
    }
}
