package synapps.resona.api.external.email;

import synapps.resona.api.global.config.ServerInfoConfig;
import synapps.resona.api.global.dto.MetaDataDto;
import synapps.resona.api.global.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final ServerInfoConfig serverInfo;
    private int number; // 이메일 인증 숫자를 저장하는 변수

    private MetaDataDto createSuccessMetaData(String queryString){
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    // 인증 이메일 전송
    @PostMapping()
    public ResponseEntity<?> sendMail(HttpServletRequest request, String mail) {
        HashMap<String, Object> map = new HashMap<>();

        try {
            number = mailService.sendMail(mail);
            String num = String.valueOf(number);

            map.put("success", Boolean.TRUE);
            map.put("number", num);
        } catch (Exception e) {
            map.put("success", Boolean.FALSE);
            map.put("error", e.getMessage());
        }
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(map));

        return ResponseEntity.ok(responseData);
    }

    // 인증번호 일치여부 확인
    @GetMapping()
    public ResponseEntity<?> mailCheck(HttpServletRequest request, @RequestParam String userNumber) {

        boolean isMatch = userNumber.equals(String.valueOf(number));
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(isMatch));

        return ResponseEntity.ok(responseData);
    }
}
