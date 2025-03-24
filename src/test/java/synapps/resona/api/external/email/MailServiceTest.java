package synapps.resona.api.external.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import synapps.resona.api.external.email.exception.EmailException;
import synapps.resona.api.mysql.member.dto.response.TempTokenResponse;
import synapps.resona.api.mysql.member.service.TempTokenService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private RedisService redisService;

    @Mock
    private TempTokenService tempTokenService;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @DisplayName("정상적인 이메일 인증시 임시 토큰을 포함한 리스트가 반환되어야 한다.")
    @Test
    public void testVerifyMailAndIssueToken_validCode() throws EmailException {
        String email = "test@example.com";
        String validCode = "123456";

        // 더미 TempTokenResponse 객체 생성
        TempTokenResponse tempTokenResponse = new TempTokenResponse(null, null, false);

        // Mock 설정
        when(redisService.isEmailCheckAvailable(email)).thenReturn(true);
        when(redisService.getCode(email)).thenReturn(validCode);
        when(tempTokenService.createTemporaryToken(email)).thenReturn(tempTokenResponse);

        // 메서드 실행
        List<?> result = mailService.verifyMailAndIssueToken(email, validCode);

        // 결과 검증
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(tempTokenResponse, result.get(0));

        // 올바른 코드인 경우 countCheck가 호출되지 않아야 함
        verify(redisService, never()).countCheck(email);
    }


    @DisplayName("인증번호가 같지 않을 때, 예외를 발생시켜야 한다.")
    @Test
    public void testVerifyMailAndIssueToken_invalidCode() throws EmailException {
        String email = "test@example.com";
        String providedCode = "111111"; // 잘못된 코드
        String storedCode = "123456";   // Redis에 저장된 올바른 코드
        int remainingAttempts = 4;

        // 잘못된 인증 코드 상황을 위한 Mock 설정
        when(redisService.isEmailCheckAvailable(email)).thenReturn(true);
        when(redisService.getCode(email)).thenReturn(storedCode);
        when(redisService.getRemainingEmailAuthenticates(email)).thenReturn(remainingAttempts);

        // 잘못된 코드 입력 시 EmailException 발생 여부 확인
        EmailException exception = assertThrows(EmailException.class, () -> {
            mailService.verifyMailAndIssueToken(email, providedCode);
        });

        // 유효한 인증 메일이 아닌지 확인
        EmailException expected = EmailException.invalidEmailCode(remainingAttempts);
        assertEquals(expected.getErrorCode(), exception.getErrorCode());

        // 잘못된 코드 입력으로 인해 countCheck가 1회 증가했는지 검증
        verify(redisService, times(1)).countCheck(email);
    }

    @DisplayName("일일 이메일 발송 횟수 초과 시, 예외를 발생시켜야 한다.")
    @Test
    public void testSend_emailSendTrialExceeded() throws EmailException {
        String email = "test@example.com";

        // 이메일 발송 가능 여부가 false인 상황으로 설정
        when(redisService.isEmailSendAvailable(email)).thenReturn(false);

        // send() 메서드 호출 시 EmailException 발생 여부 확인
        EmailException exception = assertThrows(EmailException.class, () -> {
            mailService.send(email);
        });

        // 일일 발송 횟수 초과 에러인지 확인
        EmailException expected = EmailException.sendTrialExceeded();
        assertEquals(expected.getErrorCode(), exception.getErrorCode());
    }

    @DisplayName("인증 검증 횟수가 초과된 경우, 예외를 발생시켜야 한다.")
    @Test
    public void testVerifyMailAndIssueToken_verifyTrialExceeded() throws EmailException {
        String email = "test@example.com";
        String code = "123456";

        // 인증 검증 횟수가 초과된 상황을 위한 Mock 설정
        when(redisService.isEmailCheckAvailable(email)).thenReturn(false);

        // 인증 검증 초과 시 EmailException 발생 여부 확인
        EmailException exception = assertThrows(EmailException.class, () -> {
            mailService.verifyMailAndIssueToken(email, code);
        });

        // 이메일 검증 횟수를 초과했을 때, 예외 발생
        EmailException expected = EmailException.verifyTrialExceeded();
        assertEquals(expected.getErrorCode(), exception.getErrorCode());

        // 인증 시도 초과이면 getCode 호출은 일어나지 않아야 한다.
        verify(redisService, never()).getCode(email);
    }
}