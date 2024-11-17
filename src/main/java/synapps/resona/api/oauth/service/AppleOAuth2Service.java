package synapps.resona.api.oauth.service;

import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import synapps.resona.api.oauth.token.AuthTokenProvider;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class AppleOAuth2Service {
    private final AuthTokenProvider tokenProvider;
    private final Environment env;

    public String getAppleClientSecret() throws Exception {
        String teamId = env.getProperty("security.oauth2.client.registration.apple.team-id");
        String clientId = env.getProperty("security.oauth2.client.registration.apple.client-id");
        String keyId = env.getProperty("security.oauth2.client.registration.apple.key-id");
        String keyPath = env.getProperty("security.oauth2.client.registration.apple.key-path");

        // Apple JWT 토큰 생성
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

        String clientSecret = Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId)
                .signWith(getPrivateKey(keyPath), SignatureAlgorithm.ES256)
                .compact();

        return clientSecret;
    }

    private PrivateKey getPrivateKey(String keyPath) throws Exception {
        String privateKeyContent = Files.readString(Paths.get(keyPath));
        privateKeyContent = privateKeyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }
}