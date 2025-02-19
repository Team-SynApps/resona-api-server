package synapps.resona.api.external.email;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import synapps.resona.api.external.email.exception.EmailException;
import synapps.resona.api.global.config.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.exception.ErrorCode;
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
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(isMatch));

        return ResponseEntity.ok(responseData);
    }

    // 인증번호 일치여부 확인
    @PostMapping("/temp_token")
    public ResponseEntity<?> mailCheckAndIssueToken(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody EmailCheckDto emailCheckDto) throws EmailException {

        boolean isMatch = emailCheckDto.getNumber().equals(redisService.getCode(emailCheckDto.getEmail()));
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        if(isMatch){
            ResponseDto responseData = new ResponseDto(metaData, List.of(tempTokenService.createTemporaryToken(request, response, emailCheckDto.getEmail())));
            return ResponseEntity.ok(responseData);
        }

        ResponseDto responseData = new ResponseDto(metaData, List.of(isMatch));
        return ResponseEntity.ok(responseData);
    }
}
