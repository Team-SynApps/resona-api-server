package synapps.resona.api.mysql.member.entity.member_details;

public enum MBTI {
  INFP, INFJ, INTP, INTJ, ISFP, ISFJ, ISTP, ISTJ, ENFP, ENFJ, ENTP, ENTJ, ESFP, ESFJ, ESTP, ESTJ, NONE;

  public static MBTI of(String mbti) {
    for (MBTI b : MBTI.values()) {
      if (b.name().equals(mbti)) {
        return b;
      }
    }
    return NONE;
  }
}
