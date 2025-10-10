package com.synapps.resona.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.synapps.resona.entity.member.MemberRefreshToken;
import org.junit.jupiter.api.Test;

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
