package com.synapps.resona.command.dto.response;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.member_details.MemberDetails;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.command.entity.profile.Profile;
import com.synapps.resona.entity.Language;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberRegisterResponseDto {

  private Long memberId;

  private String email;

  private String nickname;

  private String tag;

  private CountryCode nationality;

  private CountryCode countryOfResidence;

  private Set<Language> nativeLanguages;

  private Set<Language> interestingLanguages;

  private Integer timezone;

  private String birth;

  private String profileImageUrl;

  public static MemberRegisterResponseDto from(Member member, Profile profile,
      MemberDetails memberDetails) {
    return MemberRegisterResponseDto.builder()
        .memberId(member.getId())
        .email(member.getEmail())
        .tag(profile.getTag())
        .nationality(profile.getNationality())
        .countryOfResidence(profile.getCountryOfResidence())
        .nativeLanguages(profile.getNativeLanguages())
        .interestingLanguages(profile.getInterestingLanguages())
        .birth(profile.getBirth().toString())
        .nickname(profile.getNickname())
        .profileImageUrl(profile.getProfileImageUrl())
        .timezone(memberDetails.getTimezone())
        .build();
  }
}
