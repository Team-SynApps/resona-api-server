package synapps.resona.api.external.email;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.external.email.exception.EmailException;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.Meta;
import synapps.resona.api.global.dto.response.ResponseDto;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class MailController {

  private final MailService mailService;
  private final ServerInfoConfig serverInfo;

  private Meta createSuccessMetaData(String queryString) {
    return Meta.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  private Meta createFailMetaData(int status, String message, String queryString) {
    return Meta.createErrorMetaData(status, message, queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  /**
   * 이메일 인증번호 전송을 위한 API
   *
   * @param request HttpServletRequest
   * @param mail    이메일
   * @return 남은 발송 횟수 리턴
   * @throws EmailException 예외 발생시 이메일 예외 던짐
   */
  @PostMapping()
  public ResponseEntity<?> sendMail(HttpServletRequest request, String mail) throws EmailException {
    HashMap<String, Object> result = mailService.send(mail);

    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(result));
    return ResponseEntity.ok(responseData);
  }

  /**
   * @param request       HttpServletRequest
   * @param response      HttpServletResponse
   * @param emailCheckDto email, number
   * @return accessToken
   * @throws EmailException 예외 발생시 이메일 예외
   */
  @PostMapping("/temp-token")
  public ResponseEntity<?> mailCheckAndIssueToken(HttpServletRequest request,
      HttpServletResponse response, @Valid @RequestBody EmailCheckDto emailCheckDto)
      throws EmailException {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        mailService.verifyMailAndIssueToken(emailCheckDto.getEmail(), emailCheckDto.getNumber()));
    return ResponseEntity.ok(responseData);
  }

  // 인증번호 일치여부 확인
//    @PostMapping("/verification")
//    public ResponseEntity<?> mailCheck(HttpServletRequest request, @Valid @RequestBody EmailCheckDto emailCheckDto) throws EmailException {
//        boolean isMatch = emailCheckDto.getNumber().equals(redisService.getCode(emailCheckDto.getEmail()));
//        HashMap<String, Object> map = new HashMap<>();
//
//        if (redisService.isEmailCheckAvailable(emailCheckDto.getEmail())) {
//            throw EmailException.trialExceeded();
//        }
//
//        if (isMatch) {
//            int remainingCount = redisService.getRemainingEmailAuthenticates(emailCheckDto.getEmail());
//            map.put("remaining count", remainingCount);
//            map.put("isMatch", isMatch);
//            MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
//            ResponseDto responseData = new ResponseDto(metaData, List.of(map));
//            return ResponseEntity.ok(responseData);
//        }
//
//        MetaDataDto metaData = createFailMetaData(ErrorCode.NOT_ACCEPTABLE.getStatus().value(), ErrorCode.NOT_ACCEPTABLE.getMessage(), request.getQueryString());
//
//        int remainingCount = redisService.getRemainingEmailAuthenticates(emailCheckDto.getEmail());
//        map.put("remaining count", remainingCount);
//
//        ResponseDto responseData = new ResponseDto(metaData, List.of(map));
//        return new ResponseEntity<>(responseData, HttpStatus.NOT_ACCEPTABLE);
//    }
}
