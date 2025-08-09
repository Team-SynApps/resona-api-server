package synapps.resona.api.mysql.member.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import synapps.resona.api.member.entity.member.MemberRefreshToken;

public class MemberRefreshTokenTest {

  @Test
  void testMemberRefreshTokenCreation() {
    MemberRefreshToken token = new MemberRefreshToken("test@example.com", "refreshToken123");

    assertNotNull(token);
    assertEquals("test@example.com", token.getMemberEmail());
    assertEquals("refreshToken123", token.getRefreshToken());
  }

  @Test
  void testSettersAndGetters() {
    MemberRefreshToken token = new MemberRefreshToken();

    token.setMemberEmail("newuser@example.com");
    token.setRefreshToken("newRefreshToken456");

    assertEquals("newuser@example.com", token.getMemberEmail());
    assertEquals("newRefreshToken456", token.getRefreshToken());
  }

//    @Test
//    void testEqualsAndHashCode() {
//        MemberRefreshToken token1 = new MemberRefreshToken("user@example.com", "token123");
//        MemberRefreshToken token2 = new MemberRefreshToken("user@example.com", "token123");
//        MemberRefreshToken token3 = new MemberRefreshToken("otheruser@example.com", "token456");
//
//        assertEquals(token1, token2);
//        assertNotEquals(token1, token3);
//        assertEquals(token1.hashCode(), token2.hashCode());
//        assertNotEquals(token1.hashCode(), token3.hashCode());
//    }
}
