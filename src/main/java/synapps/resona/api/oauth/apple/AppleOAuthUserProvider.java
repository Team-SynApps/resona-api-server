package synapps.resona.api.oauth.apple;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import synapps.resona.api.mysql.member.dto.response.OAuthPlatformMemberResponse;

import java.security.PublicKey;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider {

    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public OAuthPlatformMemberResponse getApplePlatformMember(String identityToken) throws Exception {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        return new OAuthPlatformMemberResponse(claims.getSubject(), claims.get("email", String.class));
    }

    private void validateClaims(Claims claims) throws Exception {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new Exception("Apple OAuth Claims 값이 올바르지 않습니다.");
        }
    }
}
