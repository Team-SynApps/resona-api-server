package com.synapps.resona.common.entity;

import com.synapps.resona.entity.profile.CountryCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Author {
  private Long memberId;
  private String nickname;
  private String profileImageUrl;
  private CountryCode countryOfResidence;

  private Author(Long memberId, String nickname, String profileImageUrl, CountryCode countryOfResidence) {
    this.memberId = memberId;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.countryOfResidence = countryOfResidence;
  }

  public static Author of(Long memberId, String nickname, String profileImageUrl, CountryCode countryOfResidence) {
    return new Author(memberId, nickname, profileImageUrl, countryOfResidence);
  }
}
