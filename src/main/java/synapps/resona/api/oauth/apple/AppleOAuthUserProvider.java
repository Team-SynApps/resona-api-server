package synapps.resona.api.oauth.apple;

import io.jsonwebtoken.Claims;
import java.security.PublicKey;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import synapps.resona.api.member.dto.response.OAuthPlatformMemberResponse;

@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider {

  private final AppleJwtParser appleJwtParser;
  private final AppleClient appleClient;
  private final PublicKeyGenerator publicKeyGenerator;
  private final AppleClaimsValidator appleClaimsValidator;

  public OAuthPlatformMemberResponse getApplePlatformMember(String identityToken) throws Exception {
    // 받은 토큰 값 로깅
    System.out.println("Received identity token: " + identityToken);

    try {
      Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
      // 파싱된 헤더 값 로깅
      System.out.println("Parsed headers: " + headers);

      ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
      PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

      Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
      validateClaims(claims);
      return new OAuthPlatformMemberResponse(claims.getSubject(),
          claims.get("email", String.class));
    } catch (Exception e) {
      // 상세한 에러 정보 로깅
      System.err.println("Error processing token: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  private void validateClaims(Claims claims) throws Exception {
    if (!appleClaimsValidator.isValid(claims)) {
      throw new Exception("Apple OAuth Claims 값이 올바르지 않습니다.");
    }
  }
}
