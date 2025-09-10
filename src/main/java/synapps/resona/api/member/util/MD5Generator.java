package synapps.resona.api.member.util;

import java.security.MessageDigest;

public class MD5Generator implements HashGenerator {

  @Override
  public String generateHash(String input) {
    try {
      // SHA-256 해시 알고리즘 사용
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] hashBytes = digest.digest(input.getBytes());

      // 바이트 배열을 16진수 문자열로 변환
      StringBuilder hashString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hashString.append('0');
        }
        hashString.append(hex);
      }

      System.out.println("SHA-256 Hash: " + hashString);
      return hashString.toString();
    } catch (Exception e) {
      System.out.println("Algorithm not found: " + e.getMessage());
    }

    return null;
  }
}
