package com.synapps.resona.oauth.apple;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("local-only")
class AppleClientTest {

  @Autowired
  private AppleClient appleClient;

  @Test
  @DisplayName("apple 서버와 통신하여 Apple public keys 응답을 받는다")
  void getPublicKeys() {
    ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
    List<ApplePublicKey> keys = applePublicKeys.getKeys();

    boolean isRequestedKeysNonNull = keys.stream()
        .allMatch(this::isAllNotNull);
    assertThat(isRequestedKeysNonNull).isTrue();
  }

  private boolean isAllNotNull(ApplePublicKey applePublicKey) {
    return Objects.nonNull(applePublicKey.getKty()) && Objects.nonNull(applePublicKey.getKid()) &&
        Objects.nonNull(applePublicKey.getUse()) && Objects.nonNull(applePublicKey.getAlg()) &&
        Objects.nonNull(applePublicKey.getN()) && Objects.nonNull(applePublicKey.getE());
  }
}